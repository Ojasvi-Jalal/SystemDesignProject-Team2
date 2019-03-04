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

    # # INVALID returns error as no position is given
    # print(">Failed move to:")
    # sio.emit("move_to", {})

    # # INVALID returns error as position of 10 is out of range
    # print(">Failed move_to pos = 10")
    # sio.emit("move_to", {"pos": 10})

    # # INVALID returns error as postion is out of range
    # print(">Failed move_to pos = -3")
    # sio.emit("move_to", {"pos": -3})

    # # Move robot to specific position
    # print(">Move to pos = 5")
    # sio.emit("move_to", {"pos": 5})

    # # Retrieve all of the data in the database
    # print(">Get data")
    # sio.emit("get_data")

    # # INVALID no position specified
    # print(">Invalid add item, no pos")
    # sio.emit("add_item", {"name": "No Pos!"})

    # # Add's a pineapples to the database
    # print(">add item Pine")
    # sio.emit("add_item", {"pos": 5, "name": "Pine", "barcode": "394830242"})
    # print(">add item Apple")
    # sio.emit("add_item", {"pos": 6, "name": "Apple", "barcode": "8765432"})
    # print(">add item Pineapple")
    # sio.emit("add_item", {"pos": 7, "name": "Pineapple", "barcode": None})

    # # Gets all of the data in the database
    # print(">Get data")
    # sio.emit("get_data")

    # # INVALID no posision id provided
    # print(">Invalid remove_item no pos")
    # sio.emit("remove_item",{})

    # # Remove item with position 6 from the database
    # print(">Remove pos = 6 (Apple)")
    # sio.emit("remove_item",{"pos": 6})

    # # INVALID as position out of range
    # print(">Invalid remove_item pos = 20")
    # sio.emit("remove_item",{"pos": 20})

    # # Retrieve all of the data from the database
    # print(">get_data")
    # sio.emit("get_data")

    print(">Store 1")
    sio.emit("retrieve_item", {"pos": 2})
    # sio.emit("store_item", {"pos": 2})
    # sio.emit("retrieve_item", {"pos": 5})

    sio.wait()

if __name__ == "__main__":
    main()