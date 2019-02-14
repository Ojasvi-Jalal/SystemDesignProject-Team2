# This file is used for development only
# It simulates the Android App to test out the socketIO code

import socketio

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
    sio.connect('http://0.0.0.0:8000')
    sio.emit("move_to", {})
    sio.emit("move_to", {"pos": 10})
    sio.emit("move_to", {"pos": -3})
    sio.emit("move_to", {"pos": 5})

    sio.emit("get_data")
    sio.emit("add_item", {"name": "No Pos!"})
    sio.emit("add_item", {"pos": 12, "name": "Pineapple", "barcode": "394830242"})
    sio.emit("get_data")
    sio.emit("remove_item",{})
    sio.emit("remove_item",{"pos": 2})
    sio.emit("remove_item",{"pos": 20})
    sio.emit("get_data")

    sio.wait()

if __name__ == "__main__":
    main()