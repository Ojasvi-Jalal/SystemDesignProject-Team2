import sys
from flask import Flask, request
from serial_io import *
import argparse
from config import * 
from flask_socketio import SocketIO, send, emit

app = Flask(__name__)
socketio = SocketIO(app)

    
# Move the shelf to a position. Example
# move_to, {pos: "8"}
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

if __name__ == '__main__':
    global sio
    global at_final_position

    parser = argparse.ArgumentParser()
    parser.add_argument("--mock-serial", action='store_true', help="Don't create or communicate over serial. Instead all communications are printed to standard out ")
    args = parser.parse_args()

    sio = SerialIO(RF_DEVICE, RF_DEVICE, args.mock_serial)
    at_final_position = False

    app.run(debug=True, port=8000, host='0.0.0.0')
