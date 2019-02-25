# python3 serve.py --mock-serial

import logging
import sys
# Server
from flask import Flask, request
# Serial to Arduino
from serial_io import *
# Debugging stuff
import argparse
# Configuration
from config import * 
# Server
from flask_socketio import SocketIO, send, emit
# Json
from dataAccess import Write, Read
from Item import Item
from Segment import Segment

app = Flask(__name__)
socketio = SocketIO(app)
# JSon
read = Read()
write = Write()

def db_get_all():
    logging.info("Get all items from database")
    segments = []
    for key in range(ROBOT_MIN_POS, ROBOT_MAX_POS + 1):
   	    segments.append(read.read_shelf(key))

    return list(map(lambda x: x.to_json(), segments))

def db_add(pos: int, name, expiry, barcode):
    logging.info("Adding item at position {} with name={}, expriry={}, barcode={}".format(pos, name, expiry, barcode))
    if pos in range(ROBOT_MIN_POS, ROBOT_MAX_POS + 1):
        write.update_shelf(pos, Item(barcode, name))
        return True
    else:
        return False

def db_remove(pos: int):
    logging.info("Removing item at pos {}".format(pos))
    if pos in range(ROBOT_MIN_POS, ROBOT_MAX_POS + 1):
        write.clear_shelf(pos)
        return True

    logging.error("Failed to remove item at pos {}: pos not in range".format(pos))
    return False
    
# Move the shelf to a position. Example
# move_to, {"pos": 8}
# This endpoint probabily won't be needed in the future but useful for testing
@socketio.on('move_to')
def handle_message(message):
    pos = message.get("pos")
    if pos is None:
        emit("move_to", {"success": False, "message": "No pos provided"})
        return 

    if not (ROBOT_MIN_POS <= pos <= ROBOT_MAX_POS):
        emit("move_to", {"success": False, "message": "Provided pos is out of range. Expected value between {} and {}".format(ROBOT_MIN_POS, ROBOT_MAX_POS)})
        return 

    sio.write_char(pos.__str__())
    emit("move_to", {"success": True})

@socketio.on("get_data")
def get_data():
    emit("get_data", db_get_all())

@socketio.on("add_item")
def add_item(item):
    if "pos" not in item:
        emit("add_item", {"success": False, "message": "No pos on item"})
        return 

    db_add(item.get("pos"), item.get("name"), item.get("expiry"), item.get("barcode"))
    emit("add_item", {"success": True})

@socketio.on("remove_item")
def remove_item(json):
    if "pos" not in json:
        emit("add_item", {"success": False, "message": "No pos provided"})
        return 

    if not db_remove(json["pos"]):
        emit("add_item", {"success": False, "message": "Could not find an item with that pos to remove"})
        return 

    emit("remove_item", {"success": True})


if __name__ == '__main__':
    global sio

    # Setup logging
    logging.basicConfig(
        format='%(asctime)s %(levelname)-8s %(message)s',
        level=logging.DEBUG,
        datefmt='%Y-%m-%d %H:%M:%S', filename='serve.log')

    logging.getLogger().addHandler(logging.StreamHandler())
    logging.getLogger().setLevel(logging.DEBUG)

    parser = argparse.ArgumentParser()
    parser.add_argument("--mock-serial", action='store_true', help="Don't create or communicate over serial. Instead all communications are printed to standard out ")
    args = parser.parse_args()

    sio = SerialIO(RF_DEVICE, RF_DEVICE, args.mock_serial)


    app.run(debug=True, port=8000, host='0.0.0.0')
