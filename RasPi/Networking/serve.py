from flask import Flask
from ..serial_io import *
sio = SerialIO("/dev/ttyACM0","/dev/ttyACM0")
bool = False
app = Flask(__name__)
@app.route('/')
def index():
    if(bool):
        sio.write_char('1')
        return 'Turned on'
    else:
        sio.write_char('0')
        return 'Turned off'
if __name__ == '__main__':
    app.run(debug=True, port=8000, host='0.0.0.0')
