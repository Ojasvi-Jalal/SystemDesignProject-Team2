# This file is used for development only
# It simulates the Android App to test out the socketIO code

import socketio

sio = socketio.Client()

@sio.on('connect')
def on_connect():
    print('connection established')

@sio.on('move_to')
def move_to_response(res):
    print(res)

def main():
    sio.connect('http://0.0.0.0:8000')
    sio.emit("move_to", {})
    sio.emit("move_to", {"pos": 10})
    sio.emit("move_to", {"pos": -3})
    sio.emit("move_to", {"pos": 5})
    sio.wait()

if __name__ == "__main__":
    main()