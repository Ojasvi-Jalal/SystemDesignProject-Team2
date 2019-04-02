from os.path import expanduser

ROBOT_MIN_POS = 0
ROBOT_MAX_POS = 7

# Name of the radio device on the raspberry pi and the baud rate to read and write at 
RF_DEVICE = "/dev/ttyACM0"
BAUD_RATE = 115200

# Name of JSON database file
SHELF_JSON_FILE = expanduser("~/shelf.json")

# I don't think this is used anywhere but I'm too scared to remove it 
WAIT_FOR_RESPONSE_MS=100


# Robot timeouts
# How long should the raspi wait for the robot to send completion status until we end a timeout error
# IMPORTANT: All times in milliseconds
# Timeout is how long to wait max for each character sent by serial. So total time could
# be longer that timeout if characters are being sent very slowly
RETRIVE_TIMEOUT = 30000
STORE_TIMEOUT = 30000
SCAN_TIMEOUT = 30000			# Per message
ORIGIN_TIMEOUT = 10000

# How often to ping Android when waiting for serial input

PING_INTERVAL_MS = 1000
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

# Lock file created to stop PIR sensor from running when shelf is moving
# This prevents the PIR sensor from trying to scan from movement detected by movement
PIR_BLOCK_FILE = expanduser("~/.disable_pir")

SCAN_TIMEOUT_MESSAGE = "Timeout"
TIMEOUT_ERROR_MESSAGE = "Timeout"