import sys
from flask import Flask, request
from serial_io import *
import argparse
from config import * 

app = Flask(__name__)

@app.route('/')
def index():
    global at_final_position
    if(at_final_position):
        at_final_position = False
        sio.write_char('0')
        return 'Going to 0'
    else:
        at_final_position = True
        sio.write_char('8')
        return 'Going to 8'

@app.route('/move_to_pos')
def move_to():
    pos = request.args.get("pos", type=int)
    if pos is None:
        return "No valid pos provided. Format: /move_to_pos?pos=<int>", 400

    if not (ROBOT_MIN_POS <= pos <= ROBOT_MAX_POS):
        return "Provided pos {} is out of range. pos must be between {} and {}".format(pos, ROBOT_MIN_POS, ROBOT_MAX_POS), 400

    # We write the ascii value of a number, not the actual number
    sio.write_char(pos.__str__())

    return "Moving to pos {}".format(pos)
    

if __name__ == '__main__':
    global sio
    global at_final_position

    parser = argparse.ArgumentParser()
    parser.add_argument("--mock-serial", action='store_true', help="Don't create or communicate over serial. Instead all communications are printed to standard out ")
    args = parser.parse_args()

    sio = SerialIO(RF_DEVICE, RF_DEVICE, args.mock_serial)
    at_final_position = False

    app.run(debug=True, port=8000, host='0.0.0.0')
