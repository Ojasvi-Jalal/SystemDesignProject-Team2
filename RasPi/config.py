ROBOT_MIN_POS = 0
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
SCAN_TIMEOUT = 10000			# Per message
# Data pin used by PIR
PIR_DATA_PIN = 15

# Minimun gap between scans triggered by PIR sensor. So 60 would be a rate of 1 / minutes
MIN_PIR_SCAN_INTERVAL = 10

# How long to wait (in seconds) after last motion detected until rescan takes place
DELAY_BEFORE_SCAN_SEC = 5

# Where to notify a scan needed
SCAN_URL = "http://localhost:8000/pir_scan"

# Name of a unknown item
# Unknown item is where the sensor detects an item on the shelf that is not in the database
# So a user has added an item to the shelf without the robort
UNKNOWN_ITEM_NAME = "Unknown"