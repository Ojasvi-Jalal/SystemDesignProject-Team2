# SystemDesignProject-Team2

## RasPi-SocketIO

R&D into replacing the flask API with socketIO

## RasPi 

A flask HTTP server runs on the Raspberry Pi. Requests can be send to this HTTP server to control
the robot. The Raspberry Pi communicates with the Arduino via the RF dongle.

1. Plug in the power supply to start the raspberry pi. It may take up to two minutes for the pi to start
2. Insert the RF dongle into the PI
3. From a DICE computer, SSH into the raspberry pi using the command `ssh student@gabumon`. The password is `password`
4. At the Raspberry PI command line, enter the following commands to start the Flask server

```bash
cd SystemDesignProject-Team2
python3 RasPi/Networking/serve.py
```

Once you see the line `Running on http://0.0.0.0:8000/ (Press CTRL+C to quit)` printed to the console
then the server is started and ready to start receiving requests.

## SocketIO API

### move_to

Move the robot to a shelf position. The event name is 'move_to' and takes one integer parameter called "pos" (the shelf position)

```python
# Send to position 5
socketio.send("move_to", {"pos", 5})
```

In response the server sends back a message (also on the "move_to" event):

```json
{
  "success": true
}
```

or alternativly, an error

```json
{
  "success": false,
  "message": "No pos provided"
}
```

#### Resolution steps
1. Ensure the RF dongle is plugged into the Raspberry PI USB port
2. Ensure that the RF dongle has a green LED lit
3. Unplug the dongle, wait 2 seconds, then plug the dongle back into the raspberry PI
4. Plug the dongle into the other USB port
5. On the RPI console run `ls /dev/ | grep ttyACM0` - if this command returns no output then the raspberry PI can not find the dongle. Try restarting the Raspberry Pi
6. Open `RasPi/Networking/config.py` and ensure the value of `RF_DEVICE` is set to the correct `/dev/` file (`/dev/ttyACM0`).
7. Finally try turning the Raspberry PI on and off again

The most likely cause is that you are not running the python script on the Raspberry PI. 

### Can not send request to the Raspberry Pi

Any GET requests send to the server on the Raspberry PI either fail or hang and then timeout.

1. Ensure that flask is running on the Raspberry PI. You should see the line `Running on http://0.0.0.0:8000/ (Press CTRL+C to quit)` on the Raspberry Pi console
2. Ensure that you have connected to the correct port (which will be **8000**)
3. Ensure you are connected to SDP_Wifi and **not eduroam**.