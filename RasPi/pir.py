from config import * 
import sys
import time 

try:
	import RPi.GPIO as GPIO
except RuntimeError:
	print("Run script with sudo")
	sys.exit(1)

# State machine states used to track motion detection status
class State:
	NOTHING_DETECTED = 0
	DETECTION = 1
	AFTER_DETECTION = 2
	NOTIFY_SCAN_DETECTED = 3

# Globals used to keep track of last PIR update
state = State.NOTHING_DETECTED
after_detection_start = 0

def notify_scan_needed():
	# TODO Send request to server

def on_pir_update(value: int):
	global state
	global after_detection_start

	# Do the state transision transition
	if state == State.NOTHING_DETECTED:
		state = State.NOTHING_DETECTED if value == 0 else State.DETECTION
	elif state == State.DETECTION and value == 0:
		state = State.AFTER_DETECTION
		after_detection_start = time.time()
	elif state = State.AFTER_DETECTION and value == 1:
		# go back to detected state so timer will be restarted
		state = State.DETECTION

	if state == State.AFTER_DETECTION:
		# If we are in this state then do a time check
		if time.time() - after_detection_start >= DELAY_BEFORE_SCAN_SEC:
			# Enough time has passed to trigger a state scan
			notify_scan_needed()

def main():
	GPIO.setmode(GPIO.BCM)
	GPIO.setup(PIR_DATA_PIN,GPIO.IN)     

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
			if (start * 1000) % 500 == 0:
				on_pir_update(value)




if __name__=="__main__":main()

