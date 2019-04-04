import logging 
import requests
import sys

logging.basicConfig(
    format='%(asctime)s %(levelname)-8s %(message)s',
    level=logging.DEBUG,
    datefmt='%Y-%m-%d %H:%M:%S', filename='pir.log')
logging.getLogger().addHandler(logging.StreamHandler())

from config import * 
import sys
import time 
import argparse
from enum import Enum 
import os 

try:
    import RPi.GPIO as GPIO
except RuntimeError:
    print("Run script with sudo")
    sys.exit(1)

# State machine states used to track motion detection status
# See https://drive.google.com/file/d/1V4wKuB-AEGz0mrIDv-UvdJ3uS1dgsOfY/view?usp=sharing
# for state diagram
class State(Enum):
    NOTHING_DETECTED = "NOTHING_DETECTED"
    DETECTION = "DETECTION"
    AFTER_DETECTION = "AFTER_DETECTION"
    NOTIFY_SCAN_DETECTED = "NOTIFY_SCAN_DETECTED"
    COOLOFF_NOTHING_DETECTED = "COOLOF_NOTHING_DETECTED"
    COOLOFF_DETECTED = "COOLOFF_DETECTED"
    PIR_BLOCKED = "PIR_BLOCKED"
    COOLOFF_DETECTED_PIR_BLOCKED = "COOLOFF_DETECTED_PIR_BLOCKED"
    COOLOFF_NOTHING_DETECTED_PIR_BLOCKED = "COOLOFF_NOTHING_DETECTED_PIR_BLOCKED"

# Globals used to keep track of last PIR update
state = State.NOTHING_DETECTED
after_detection_start = 0
cooloff_start = 0 

def block_exists():
    return os.path.exists(PIR_BLOCK_FILE)

def notify_scan_needed():
    logging.info("Notifying serve.py scan needed as PIR detected movement")
    try:
        res = requests.get(SCAN_URL)
        result = res.text
        if result == "Success":
            logging.info("Scan succeded with result = {}".format(result))
        else:
            logging.error("Scan failed with result = {}".format(result))
    except RuntimeError:
        logging.exception("Failed to notify PIR scan to {}".format(SCAN_URL))


def on_pir_update(value: int):
    global state
    global after_detection_start
    global cooloff_start
    prev_state = state

    # First most important is to check for lock file
    if block_exists():
        if state == State.COOLOFF_DETECTED:
            state = State.COOLOFF_DETECTED_PIR_BLOCKED
        elif state == State.COOLOFF_NOTHING_DETECTED:
            state = State.COOLOFF_NOTHING_DETECTED_PIR_BLOCKED
        elif not (state == State.COOLOFF_NOTHING_DETECTED_PIR_BLOCKED and state != State.COOLOFF_DETECTED_PIR_BLOCKED):
            state = State.PIR_BLOCKED
    elif state == State.PIR_BLOCKED:
        state = State.NOTHING_DETECTED
    elif state == State.COOLOFF_DETECTED_PIR_BLOCKED:
        state = State.COOLOFF_DETECTED
    elif state == State.COOLOFF_NOTHING_DETECTED_PIR_BLOCKED:
        state = State.COOLOFF_NOTHING_DETECTED

    if value is not None:
        # Do the state transision transition
        if state == State.NOTHING_DETECTED:
            state = State.NOTHING_DETECTED if value == 0 else State.DETECTION
        elif state == State.DETECTION and value == 0:
            state = State.AFTER_DETECTION
            after_detection_start = time.time()
        elif state == State.AFTER_DETECTION and value == 1:
            # go back to detected state so timer will be restarted
            state = State.DETECTION
        elif state == State.COOLOFF_NOTHING_DETECTED and value == 1:
            state = State.COOLOFF_DETECTED


    if state == State.AFTER_DETECTION:
        # If we are in this state then do a time check
        if time.time() - after_detection_start >= DELAY_BEFORE_SCAN_SEC:
            # Enough time has passed to trigger a state scan
            state = State.NOTIFY_SCAN_DETECTED

    cooloff_complete = time.time() - cooloff_start >= MIN_PIR_SCAN_INTERVAL
    if state == State.COOLOFF_NOTHING_DETECTED and cooloff_complete:
        state = State.NOTHING_DETECTED

    if state == State.COOLOFF_DETECTED and cooloff_complete:
        state = State.AFTER_DETECTION
        # Remember to start timeer again to check for immediate movement
        after_detection_start = time.time()

    if state == State.NOTIFY_SCAN_DETECTED:
        if prev_state != state:
            logging.info("Transision from {} -> {}".format(prev_state.value, state.value))
        notify_scan_needed()
        state = State.COOLOFF_NOTHING_DETECTED
        cooloff_start = time.time()

    if prev_state != state:
        logging.info("Transision from {} -> {}".format(prev_state.value, state.value))


def loop():
    val = 0
    start = time.time()

    while True:
        new_val = GPIO.input(PIR_DATA_PIN)
        if new_val != val:
            print("{} {}".format(new_val, time.time() - start))
            on_pir_update(new_val)
            start = time.time()
            val = new_val
        else:
            # Important to allow for a time check if we are in State.AFTER_DETECTION
            if int(time.time() * 1000) % 500 == 0:
                on_pir_update(None)


def test_loop():
    val = 0
    start = time.time()

    while True:
        new_val = GPIO.input(PIR_DATA_PIN)
        if new_val != val:
            print("{} {}".format(new_val, time.time() - start))
            start = time.time()
            val = new_val

def main():
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(PIR_DATA_PIN,GPIO.IN) 

    if len(sys.argv) > 1:
        test_loop()
    else:    
        loop()



if __name__=="__main__":main()

