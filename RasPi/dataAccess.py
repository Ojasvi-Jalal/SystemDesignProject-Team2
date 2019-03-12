import json
from Segment import Segment
from Item import Item
from config import *
import os.path
import logging

class Write(object):
	def __init__(self):
		pass

	def add_item(self, item):
		read = Read()
		entry = {"barcode" : item.barcode, "name" : item.name}

		with open('items.json', 'r') as json_file:
			items = json.load(json_file)
			if read.find_items(item.barcode) is None:
				items['items'].append(entry)
			else:
				# FIXME use return type
				print ("Item barcode already in JSON")

		with open('items.json', 'w') as json_file:
			json.dump(items, json_file, indent = 4 , sort_keys=True)

	def remove_item(self, barcode):
		with open('items.json', 'r') as json_file:
			items = json.load(json_file)
			items['items'][:] = [elem for elem in items['items'] if elem.get('barcode') != barcode]

		with open('items.json', 'w') as json_file:
			json.dump(items, json_file, indent = 4 , sort_keys=True)


	def update_shelf(self, shelfID, item):
		print("SHELF ID SHELF ID ")
		print(shelfID)
		entry = {"shelfID": shelfID, "itemName": item.name, "expiryDate": None, "barcode": item.barcode}
		 
		print("ç to pos {}: {}".format(shelfID, entry))
		with open(SHELF_JSON_FILE, 'r') as json_file:
			shelf = json.load(json_file)
			shelf[str(shelfID)] = entry

		with open(SHELF_JSON_FILE, 'w') as json_file:
			json.dump(shelf, json_file, indent = 4 , sort_keys=True)

		

	def clear_shelf(self, shelfID):
		entry = {"shelfID": shelfID, "itemName": None, "expiryDate": None, "barcode": None}
		
		with open(SHELF_JSON_FILE, 'r') as json_file:
			shelf = json.load(json_file)
			shelf[str(shelfID)] = entry

		with open(SHELF_JSON_FILE, 'w') as json_file:
			json.dump(shelf, json_file, indent = 4 , sort_keys=True)

class Read(object):
	def __init__(self):
		pass

	def read_items(self):
		# Prints out items
		with open('items.json', 'r') as json_file:
			items = json.load(json_file)
			for item in items['items']:
				print(item['name'])

	def find_item(self, barcode):		
		with open('items.json', 'r') as json_file:
			items = json.load(json_file)
			item = next((elem for elem in items['items'] if elem['barcode'] == barcode), None)
			if item == None:
				return None
			else:
				return Item(item['barcode'], item['name'])
	
	def read_shelf(self, shelfID):
		print("ENTER: read_items()")

		with open(SHELF_JSON_FILE, 'r') as json_file:
			shelf = json.load(json_file)
			key = shelfID.__str__()
			if key not in shelf:
				return None
			seg_json  = shelf[shelfID.__str__()]
			return Segment(shelfID, seg_json['itemName'], seg_json['expiryDate'], seg_json['barcode'])	

def generate_empty_shelf_json():
	# Do we need a shelf item for the origin at pos = 0?
	json_data = {}
	for pos in range(ROBOT_MIN_POS, ROBOT_MAX_POS + 1):
		json_data[str(pos)] = {
			"barcode": None,
			"expiryDate": None,
			"itemName": None,
			"shelfID": pos
		}

	with open(SHELF_JSON_FILE, "w+") as f:
		json.dump(json_data, f, indent = 4, sort_keys=True)

def shelf_file_exists():
	if not os.path.exists(SHELF_JSON_FILE):
		return False

	# Now verify the file is in the correct format
	# If it is broken then it should be regeneraterd

	with open(SHELF_JSON_FILE, 'r') as json_file:
		try:
			items = json.load(json_file)
		except ValueError:
			logging.error("Shelf file exists but is not valid json:")
			logging.error(json_file.read())
			return False

		if "shelf" not in items:
			return False

	return True

def init_database():
	if not shelf_file_exists():
		logging.info("Shelf file doesn't exist - creating now")
		generate_empty_shelf_json()
	else:
		logging.info("Shelf file already exists")