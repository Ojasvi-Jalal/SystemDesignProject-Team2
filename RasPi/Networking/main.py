import logging
from request import Request

HOST = "utrecht"
PORT = "8000"

def main():
    logging.basicConfig(
        format='%(asctime)s %(levelname)-8s %(message)s',
        level=logging.DEBUG,
        datefmt='%Y-%m-%d %H:%M:%S', filename='output.log')

    stream_handler = logging.StreamHandler()
    stream_handler.setFormatter(logging.Formatter("%(asctime)s %(levelname)-8s %(message)s"))

    logging.getLogger().addHandler(stream_handler)

    #serial_io.create_test_serial_console()
    logging.info("testing request")
    req = Request(HOST, PORT)
    print(req.get(params={"test": 12, "another": 13}))

if __name__=="__main__":main()