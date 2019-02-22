# This file is used for development only
# It simulates the Android App to test out the socketIO code

import socketio
import sys

sio = socketio.Client()

@sio.on('connect')
def on_connect():
    print('connection established')

@sio.on('move_to')
def move_to_response(res):
    print("move_to")
    print(res)

@sio.on("get_data")
def get_data(res):
    print("get_data")
    print(res)


@sio.on("add_item")
def add_item(res):
    print("add_item")
    print(res)

def main():
    if len(sys.argv) == 1:
        url = "0.0.0.0:8000"
    else:
        url = sys.argv[1]

    full_url = 'http://{}'.format(url)
    print("Connecting to {}".format(full_url))
    
    sio.connect(full_url)

    # INVALID returns error as no position is given
    sio.emit("move_to", {})

    # INVALID returns error as position of 10 is out of range
    sio.emit("move_to", {"pos": 10})

    # INVALID returns error as postion is out of range
    sio.emit("move_to", {"pos": -3})

    # Move robot to specific position
    sio.emit("move_to", {"pos": 5})

    # Retrieve all of the data in the database
    sio.emit("get_data")

    # INVALID no position specified
    sio.emit("add_item", {"name": "No Pos!"})

    # Add's a pineapple to the database
    sio.emit("add_item", {"pos": 12, "name": "Pineapple", "barcode": "394830242"})

    # Gets all of the data in the database
    sio.emit("get_data")

    # INVALID no posision id provided
    sio.emit("remove_item",{})

    # Remove item with position 2 from the database
    sio.emit("remove_item",{"pos": 2})

    # INVALID as position out of range
    sio.emit("remove_item",{"pos": 20})

    # Retrieve all of the data from the database
    sio.emit("get_data")

    sio.wait()

if __name__ == "__main__":
    main()