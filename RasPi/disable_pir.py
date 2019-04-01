from config import * 
import os 
import logging 

def create_pir_block():
    with open(PIR_BLOCK_FILE, "w+") as f:
        f.write("a")

def remove_pir_block():
    if os.path.exists(PIR_BLOCK_FILE):
        os.remove(PIR_BLOCK_FILE)
    else:
        logging.warn("Tried to delete block file but {} does not exist".format(PIR_BLOCK_FILE))


# This class disables the PIR sensor when the robot is moving 
# This prevents the robot from triggering a scan event
# use like
#
# with DisablePir():
#	robot.move()
#	wait_to_return_to_origin()
#
# Outside of the wait block the PIR will start working again
# This will work even if an exception occurs in the wait statement 
# When inside the wait statement the PIR state machine will enter a PIR_BLOCKED state
# It will resume to NOTHING_DETECTED once the wait is over

class DisablePir:

	def __init__(self):
		pass

	def __enter__(self):
		create_pir_block()

	def __exit__(self, exc_type, exc_value, traceback):
		remove_pir_block()

