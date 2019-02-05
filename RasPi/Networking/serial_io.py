import serial
import struct
import logging

class SerialIO:

    def __init__(self, input_device:str, output_device: str):
        self.serial_in = serial.Serial(
            port=input_device,
            baudrate = 9600,
            parity=serial.PARITY_NONE,
            stopbits=serial.STOPBITS_ONE,
            bytesize=serial.EIGHTBITS,
            timeout=1
        )

        self.serial_out = serial.Serial(
            port=output_device,
            baudrate = 9600,
            parity=serial.PARITY_NONE,
            stopbits=serial.STOPBITS_ONE,
            bytesize=serial.EIGHTBITS,
            timeout=1
        )

    def write(self, message: str):
        self.serial_out.write(message)

    def write_char(self, char: str) -> bool:
        logging.debug("Sending to serial device: {}".format(char))
        if len(char) != 1:
            log.error("Tried to send char to serial device with length {}, must have length of 1".format(len(char)))            
            return False

        # Convert to ASCII value, so for example "l" -> 108
        send_code = ord(char)
        # Write ASCII value into string to send
        self.write(struct.pack('>B', send_code))
        return True

    def read_next(self) -> str:
        return self.serial_in.readline()

    def data_available(self) -> bool:
        return self.serial_in.in_waiting

# Code for testing
# Can enter character commands to send to arduino
def create_test_serial_console():
    sio = SerialIO("/dev/ttyACM0", "/dev/ttyACM0")

    while True:
        to_send = input("> ")
        if len(to_send) != 1:
            print("ERORR: Enter exacly one character to send")
            continue

        sio.write_char(to_send)

        while sio.data_available():
            print(sio.read_next())

