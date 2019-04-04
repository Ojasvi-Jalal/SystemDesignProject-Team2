from serial_io import *
from config import *

def main():
    sio = SerialIO(RF_DEVICE, RF_DEVICE, False)

    print("Connected to serial IO, writing: n")
    sio.write_char("n")

    print("Waiting for response...")
    scan_result = sio.read_lines_until("o", timeout_per_message=SCAN_TIMEOUT)
    print("Result = {}".format(scan_result))



if __name__=="__main__":main()