import sys
from flask import Flask, request
from serial_io import *
import argparse
from config import * 
from flask_socketio import SocketIO, send, emit

app = Flask(__name__)
socketio = SocketIO(app)

MOCK_DATA = [
    {"name": "Jam",
    "pos": 2,
    "expiry": "2019-10-03"
    },
    {"name": "Grape",
    "pos": 1,
    }
]

# Mock interactions with the database
# These will be replaced with actual database stuff once that's been completed
def db_get_all():
    return MOCK_DATA

def db_add(pos, name, expiry, barcode):
    MOCK_DATA.append({
        "name": name,
        "pos": pos,
        "expiry": expiry,
        "barcode": barcode
    })

def db_remove(pos: int):
    to_remove = None
    for idx, item in enumerate(MOCK_DATA):
        if item.get("pos") == pos:
            to_remove = idx
            break

    if to_remove is None:
        return False

    del MOCK_DATA[idx]
    return True

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
    emit("get_data", MOCK_DATA)

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
    global at_final_position

    parser = argparse.ArgumentParser()
    parser.add_argument("--mock-serial", action='store_true', help="Don't create or communicate over serial. Instead all communications are printed to standard out ")
    args = parser.parse_args()

    sio = SerialIO(RF_DEVICE, RF_DEVICE, args.mock_serial)
    at_final_position = False

    app.run(debug=True, port=8000, host='0.0.0.0')
