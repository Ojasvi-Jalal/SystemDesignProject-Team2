ROBOT_MIN_POS = 4
ROBOT_MAX_POS = 7
RF_DEVICE = "/dev/ttyACM0"
SHELF_JSON_FILE = "shelf.json"
WAIT_FOR_RESPONSE_MS=100
BAUD_RATE = 115200


# Robot timeouts
# How long should the raspi wait for the robot to send completion status until we end a timeout error
# IMPORTANT: All times in milliseconds
# Timeout is how long to wait max for each character sent by serial. So total time could
# be longer that timeout if characters are being sent very slowly
RETRIVE_TIMEOUT = 10000
STORE_TIMEOUT = 10000
