# This file is used for development only
# It simulates the Android App to test out the socketIO code

import socketio
import sys
import requests

sio = socketio.Client()

@sio.on('connect')
def on_connect():
    print('connection established')

@sio.on("message")
def on_connect(message):
    print(message)


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

@sio.on("retrieve_result")
def retrieve_result(res):
    print("RETRIEVE RESULT")
    print(res)

@sio.on("store_result")
def retrieve_result(res):
    print("STORE RESULT")
    print(res)
@sio.on("scan_result")

def retrieve_result(res):
    print("SCAN RESULT")
    print(res)

def main():
    if len(sys.argv) == 1:
        url = "0.0.0.0:8000"
    else:
        url = sys.argv[1]

    full_url = 'http://{}'.format(url)
    print("Connecting to {}".format(full_url))

    sio.connect(full_url)
    sio.emit("scan")
    #sio.emit("add_item", {"pos": 5, "name": "Pineapple"})
    #sio.emit("retrieve_item", {"pos": 3})
    #sio.emit("rsetrieve_item", {"pos": 4})
    sio.wait()




if __name__ == "__main__":
    main()
