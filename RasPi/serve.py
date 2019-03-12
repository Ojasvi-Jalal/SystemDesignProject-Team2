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
from dataAccess import Write, Read, init_database
from Item import Item
from Segment import Segment
from threading import Lock
import time
import os


app = Flask(__name__)
socketio = SocketIO(app)
# JSon
read = Read()
write = Write()

lock = Lock()

def db_get_all():
    with lock:
        logging.info("Get all items from database")
        segments = []
        for key in range(ROBOT_MIN_POS, ROBOT_MAX_POS + 1):
            segments.append(read.read_shelf(key))

        return list(map(lambda x: x.to_json(), segments))

def db_add(pos: int, name, expiry, barcode):
    with lock:
        logging.info("Adding item at position {} with name={}, expriry={}, barcode={}".format(pos, name, expiry, barcode))
        if pos in range(ROBOT_MIN_POS, ROBOT_MAX_POS + 1):
            write.update_shelf(pos, Item(barcode, name))
            return True
        else:
            return False

def db_remove(pos: int):
    with lock:
        logging.info("Removing item at pos {}".format(pos))
        if pos in range(ROBOT_MIN_POS, ROBOT_MAX_POS + 1):
            write.clear_shelf(pos)
            return True

        logging.error("Failed to remove item at pos {}: pos not in range".format(pos))
        return False

def send_item_retrieved(success, error_message = False):
    emit("retrieve_result", {"success": success, "error": error_message})
    
def send_item_stored(success, error_message = False):
    emit("store_result", {"success": success, "error": error_message})

def update_db_after_scan(existing_indexes):
    for i in range(ROBOT_MIN_POS, ROBOT_MAX_POS + 1):
        if read.read_shelf(i).itemName is not None:
            # Check that detector found item in this position
            if i not in existing_indexes:
                logging.info("Found item that exists in database but not detected in shelf: pos = {}. Removing from db...".format(i))
                db_remove(i)


# Move the shelf to a position. Example
# move_to, {"pos": 8}
# This endpoint probabily won't be needed in the future but useful for testing
@socketio.on('move_to')
def move_to(message):
    pos = message.get("pos")
    if pos is None:
        emit("move_to", {"success": False, "message": "No pos provided"})
        return 

    if not (ROBOT_MIN_POS <= pos <= ROBOT_MAX_POS):
        emit("move_to", {"success": False, "message": "Provided pos {} is out of range. Expected value between {} and {}".format(pos, ROBOT_MIN_POS, ROBOT_MAX_POS)})
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

    # Check if the shelf compartment is free
    with lock:
        shelf_item = read.read_shelf(item["pos"])
        if shelf_item.itemName is not None:
            emit("add_item", {"success": False, "message": "Position {} already contains the item {}".format(item["pos"], shelf_item.itemName)})
            return 
            
    db_add(item.get("pos"), item.get("name"), item.get("expiry"), item.get("barcode"))

    # Now get the robot to store the item at the specified position
    sio.write_char("s")
    sio.write_char(item["pos"].__str__())

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


@socketio.on("store_item")
def store_item(json):
    pos = json.get("pos")
    sio.write_char("s")
    sio.write_char(pos.__str__())

    res = wait_for_response()
    if res is None:
        send_item_stored(False, "Timeout")

@socketio.on("retrieve_item")
def retrieve_item(json):
    pos = json.get("pos")
    sio.write_char("r")
    sio.write_char(pos.__str__())

    res = wait_for_response()
    if res is None:
        send_item_retrieved(False, "Timeout")


@socketio.on("horizontal_move")
def horizontal_move(json):
    pos = json.get("pos")
    sio.write_char("h")
    sio.write_char(pos.__str__())


@socketio.on("scan")
def scan():
    sio.write_char("n")
    # Read positions of each shelf position

    while True:
        print(sio.wait_for_response())
        pass

    scan_result = sio.read_lines_until("o", max_attempts=10, timeout_per_message=10000)
    print(scan_result)
    logging.info("Scan result = {}".format(",".join(scan_result)))


@socketio.on("origin")
def origin():
    sio.write_char("o")

def main_run_once():
    pos = 5
    #  scan()

if __name__ == '__main__':
    global sio

    # Setup logging
    logging.basicConfig(
        format='%(asctime)s %(levelname)-8s %(message)s',
        level=logging.DEBUG,
        datefmt='%Y-%m-%d %H:%M:%S', filename='serve.log')

    logging.getLogger().addHandler(logging.StreamHandler())
    logging.getLogger().setLevel(logging.DEBUG)
    log = logging.getLogger('werkzeug')
    log.setLevel(logging.ERROR)

    parser = argparse.ArgumentParser()
    parser.add_argument("--mock-serial", action='store_true', help="Don't create or communicate over serial. Instead all communications are printed to standard out ")
    args = parser.parse_args()

    # Setup database
    init_database()

    # Setup serial
    sio = SerialIO(RF_DEVICE, RF_DEVICE, args.mock_serial)

    if os.environ.get('WERKZEUG_RUN_MAIN') is None:
        main_run_once()
        pass

    # Start server on port 8000
    app.run(debug=True, port=8000, host='0.0.0.0')

