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
            log.error("Tried to send char to serial device with length {}, must have length of 1".format(len(char)))            
            return False

        # Convert to ASCII value, so for example "l" -> 108
        send_code = ord(char)
        # Write ASCII value into string to send
        self.write(struct.pack('>B', send_code))
        return True

    def read_next(self) -> str:
        if self.mock_io:
            return None
        return self.serial_in.readline()

    def data_available(self) -> bool:
        if self.mock_io:
            return False
        return self.serial_in.in_waiting

    def wait_for_response(self, timeout_ms = 10000):
        response = "\r\n"
        time_started = time.time() 

        while response != "\r\n" and 1000 * (time.time() - time_started) < timeout_ms:
            time.sleep(WAIT_FOR_RESPONSE_MS / 1000)
            response = self.read_next()

        if not response == "\r\n":
            return response.replace("\r\n", "")
        
        logging.info("Failed to recieve any data: timeout")
        return None


    def read_lines_until(self, text, timeout_per_message=10000, max_attempts=10):
        lines = []
        time_started = time.time()
        attempts = 0
        while True:
            res = self.wait_for_response(timeout_per_message)
            if res == text:
                lines.append(text)
                return lines
            elif res is None:
                # Timeout occured
                logging.info("timeout occured at attempts = {}/{}".format(attempts, max_attempts))
                if attempts >= max_attempts:
                    logging.info("read_lines_until timeout due to max_attempts reached of {}".format(max_attempts))
                    return lines     # We can tell that it timed out due to the last element != text
                attempts += 1
            elif attempts != "":
                # Ignore empty new lines sent
                attempts = 0
                lines.append(res)


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

