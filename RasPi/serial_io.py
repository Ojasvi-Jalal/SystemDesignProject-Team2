import serial
import struct
import logging
import time
from config import *

class SerialIO:

    def __init__(self, input_device:str, output_device: str, mock_io = False):
        self.mock_io = mock_io

        if not mock_io:
            self.serial_in = serial.Serial(
                port=input_device,
                baudrate = BAUD_RATE,
                parity=serial.PARITY_NONE,
                stopbits=serial.STOPBITS_ONE,
                bytesize=serial.EIGHTBITS,
                timeout=1
            )

            self.serial_out = serial.Serial(
                port=output_device,
                baudrate = BAUD_RATE,
                parity=serial.PARITY_NONE,
                stopbits=serial.STOPBITS_ONE,
                bytesize=serial.EIGHTBITS,
                timeout=1
            )

    def write(self, message: str):
        if self.mock_io:
            print("SEND {}".format(message))
            return
        self.serial_out.write(message)

    def write_char(self, char: str) -> bool:
        logging.debug("Sending to serial device: {}".format(char))
        if len(char) != 1:
            logging.error("Tried to send char to serial device with length {}, must have length of 1".format(len(char)))            
            return False

        # Convert to ASCII value, so for example "l" -> 108
        send_code = ord(char)
        # Write ASCII value into string to send
        self.write(struct.pack('>B', send_code))
        return True

    def read_next(self) -> str:
        if self.mock_io:
            return None

        return self.serial_in.readline().decode()

    def read_next_line(self):
        next = self.read_next()

        if next is None:
            return None
        elif next == "\r\n" or next == "":
            return None

        return next.replace("\r\n", "")

    def wait_for_next_line(self, timeout_ms=10000):
        time_started_ms = time.time() * 1000
        while True:
            line = self.read_next_line()
            if line is not None:
                return line

            time_now_ms = time.time() * 1000
            if time_now_ms - time_started_ms >= timeout_ms:
                logging.info("wait_for_next_line timed out after {}ms".format(time_now_ms - time_started_ms))
                return None # Indicate timeout
        
    def read_lines_until(self, text, timeout_per_message=10000):
        return self.read_lines_until_any([text], timeout_per_message)

    # Read lines from serial until it finds a line with text that matches exactly an entry 
    # in the options list
    def read_lines_until_any(self, options, timeout_per_message):
        lines = []
        time_started = time.time()
        while True:
            res = self.wait_for_next_line(timeout_per_message)
            logging.info("read_lines_until: read line {}".format(res))
            if res in options:
                lines.append(res)
                return lines
            elif res is None:
                # Timeout occured
                logging.info("timeout occured after {}ms".format(timeout_per_message))
                return lines # We know timeout occured as final item != text
            elif attempts != "":
                # Ignore empty new lines sent
                lines.append(res)


    def data_available(self):
        if self.mock_io:
            return False
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

