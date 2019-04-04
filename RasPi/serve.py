# python3 serve.py --mock-serial

import logging

logging.basicConfig(
    format='%(asctime)s %(levelname)-8s %(message)s',
    level=logging.DEBUG,
    datefmt='%Y-%m-%d %H:%M:%S', filename='serve.log')

logging.getLogger().addHandler(logging.StreamHandler())
logging.getLogger().setLevel(logging.DEBUG)
log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)

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
import socketio
# Json
from dataAccess import Write, Read, init_database
from Item import Item
from Segment import Segment
from threading import Lock
import time
import os
from disable_pir import DisablePir

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
            write.update_shelf(pos, Item(barcode, name, expiry))
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

def send_item_retrieved(success, error_message = None):
    emit("retrieve_result", {"success": success, "error": error_message})

def send_item_stored(success, error_message = None):
    emit("store_result", {"success": success, "error": error_message})

def send_scan_complete(success, error_message = None, use_local_socketio = False):
    res = {"success": success, "error": error_message}
    if use_local_socketio:
        socketio.emit("scan_result", res)
    else:
        emit("scan_result", res)


def update_db_after_scan(existing_indexes):
    logging.info("Updating database from scan result")
    for i in range(ROBOT_MIN_POS, ROBOT_MAX_POS + 1):
        if read.read_shelf(i).itemName is not None:
            # Check that detector found item in this position
            if i not in existing_indexes:
                logging.info("Found item that exists in database but not detected in shelf: pos = {}. Removing from db...".format(i))
                db_remove(i)
        else:
            # Check that detector did not find item in this posision
            if i in existing_indexes:
                logging.info("Found item that does not exist in item database at pos = {} - Adding unknown item to database".format(i))
                # We need to add an unknown item to the database
                db_add(i, UNKNOWN_ITEM_NAME, None, None)


@socketio.on("get_data")
def get_data():
    emit("get_data", db_get_all())

@socketio.on("add_item")
def add_item(item):
    # Check format of socketio request
    if "pos" not in item:
        emit("add_item", {"success": False, "message": "No pos on item"})
        return
    pos = item.get("pos")

    # Check if the shelf compartment is free
    # It's important to use the lock here as only one thread should access the file at once
    with lock:
        shelf_item = read.read_shelf(item["pos"])

        # The shelf position is full so return an error to the Android app
        if shelf_item.itemName is not None:
            emit("add_item", {"success": False, "message": "Position {} already contains the item {}".format(item["pos"], shelf_item.itemName)})
            return

    with DisablePir():
        # Now get the robot to store the item at the specified position
        logging.info("Sending store command to robot: s{}".format(pos))
        sio.write_char("s")
        sio.write_char(item["pos"].__str__())

        logging.info("Waiting for robot to store item and return to origin...")
        # Finally wait for the robot to return back to the origion
        store_error = get_store_retrieve_error(STORE_TIMEOUT)
        if store_error is not None:
            logging.error("Timeout occured when storing item {} at position {}".format(item.get("name"), pos))
            send_item_stored(False, store_error)
            return

        # Success, Add the new item to the database
        db_add(item.get("pos"), item.get("name"), item.get("expiry"), item.get("barcode"))

        # Send success back to the android app
        logging.info("item stored successfully")
        send_item_stored(True)

@socketio.on("retrieve_item")
def retrieve_item(json):

    with DisablePir():
        pos = json.get("pos")

        logging.info("Got request to retieve item at position {} - sending command r{}".format(pos, pos))
        sio.write_char("r")
        sio.write_char(pos.__str__())

        # Send the new updated database to the Android App
        emit("get_data", db_get_all())

        # Read the status of the robot. This waits until it returns to origin (when it sends the character 'o')
        logging.info("Waiting for robot to return back to origin")
        retrieve_error = get_store_retrieve_error(RETRIVE_TIMEOUT)
        if retrieve_error is not None:
            # Timeout occured if we didn't recieve a 'o' after a while
            logging.error("Timeout occured when attempting to retrieve using r{}".format(pos))
            send_item_retrieved(False, retrieve_error)
            return

        # Success, remove item
        db_remove(pos)

        # Everything has completed succcessfully, indicate success to Android app
        logging.info("Item successfully retrieved")
        send_item_retrieved(True)


@socketio.on("scan")
def scan():
    logging.info("Got request from Android to perform scan. Sending scan command: n")
    if do_scan():
        logging.info("Sending scan success message to Android")
        send_scan_complete(True)
    else:
        logging.info("Sending scan failed message to Android")
        send_scan_complete(False, SCAN_TIMEOUT_MESSAGE)

    emit("get_data", db_get_all())

# use_local_socketio means = True means to use socketio context no implicit flask_scoketio context
# see https://github.com/miguelgrinberg/Flask-SocketIO/issues/40
def do_scan(use_local_socketio = False):
    logging.info("do_scan called()")
    with DisablePir():
        logging.info("Sending scan")
        sio.write_char("n")
        # Read positions of each shelf position
        logging.info("Waiting for result")

        def ping():
            if use_local_socketio:
                socketio.send("Ping!")
            else:
                send("Ping!")

        scan_result = sio.read_lines_until("o", timeout_per_message=SCAN_TIMEOUT, ping_func=ping)
        logging.info("Scan result = {}".format(",".join(scan_result)))

        if len(scan_result) == 0 or scan_result[-1] != "o":
            logging.error("Robot scan failed due to timeout. Got response {}".format(scan_result))
            return False

        if len(scan_result) < 2 or scan_result[-2] != "e":
            logging.info("Invalid scan result: expected 'e' but found none")
            return False

        try:
            pos_ints = []
            for x in scan_result[:-2]:
                pos_ints.append(int(x))
            update_db_after_scan(pos_ints)
        except ValueError:
            logging.exception("Failed to interpret scan result: got strings that could not be parsed into ints")
            return False

        logging.info("Scanning complete")
        return True

def get_store_retrieve_error(timeout):
    def ping():
        logging.info("store_retrieve ping!")
        send("Ping!")

    res = sio.read_lines_until("o", timeout_per_message=timeout, ping_func=ping, ping_timeout_ms=PING_INTERVAL_MS)
    logging.info("get_store_retrieve_error: got response of {}".format(res))
    if len(res) == 0:
        return TIMEOUT_ERROR_MESSAGE

    # Success
    if res[-1] == "o":
        return None

    # Exception
    if res[0] == "x":
        if len(res) == 0:
            return "Unknown exception"
        return res[1]

    return "Unknown error"

# PIR initiated scan
@app.route('/pir_scan')
def index():
    logging.info("pir_scan: performing scan as requested by PIR sensor")
    socketio.emit("scan")
    res = do_scan(use_local_socketio=True)

    logging.info("Did scan res = {}. Sending data update to Android after scan".format(res))
    socketio.emit("get_data", db_get_all())

    if res:
        send_scan_complete(True, use_local_socketio=True)
    else:
        send_scan_complete(False, error_message=SCAN_TIMEOUT_MESSAGE, use_local_socketio=True)

    if res:
        return "Success"
    else:
        return "Failed"

def main_run_once():
    pass

if __name__ == '__main__':
    global sio

    parser = argparse.ArgumentParser()
    parser.add_argument("--mock-serial", action='store_true', help="Don't create or communicate over serial. Instead all communications are printed to standard out ")
    args = parser.parse_args()
    sio = SerialIO(RF_DEVICE, RF_DEVICE, args.mock_serial)
    # Setup database
    init_database()

    if os.environ.get('WERKZEUG_RUN_MAIN') is None:
        main_run_once()
        pass

    # Start server on port 8000
    app.run(debug=True, port=8000, host='0.0.0.0')
