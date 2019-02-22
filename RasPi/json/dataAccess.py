import json
from Segment import Segment
from Item import Item

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
				print "Item barcode already in JSON"

		with open('items.json', 'w') as json_file:
			json.dump(items, json_file, indent = 4 , sort_keys=True)

	def remove_item(self, barcode):
		with open('items.json', 'r') as json_file:
			items = json.load(json_file)
			items['items'][:] = [elem for elem in items['items'] if elem.get('barcode') != barcode]

		with open('items.json', 'w') as json_file:
			json.dump(items, json_file, indent = 4 , sort_keys=True)

	def update_shelf(self, shelfID, item):
		entry = {"shelfID": shelfID, "itemName": item.name, "expiryDate": None, "barcode": item.barcode}
		 
		with open('shelf.json', 'r') as json_file:
			shelf = json.load(json_file)
			shelf['shelf'][shelfID] = entry

		with open('shelf.json', 'w') as json_file:
			json.dump(shelf, json_file, indent = 4 , sort_keys=True)

	def clear_shelf(self, shelfID):
		entry = {"shelfID": shelfID, "itemName": None, "expiryDate": None, "barcode": None}
		
		with open('shelf.json', 'r') as json_file:
			shelf = json.load(json_file)
			shelf['shelf'][shelfID] = entry

		with open('shelf.json', 'w') as json_file:
			json.dump(shelf, json_file, indent = 4 , sort_keys=True)

class Read(object):
	def __init__(self):
		pass

	def read_items(self):
		with open('items.json', 'r') as json_file:
			items = json.load(json_file)
			for item in items['items']:
				print item['name']

	def find_item(self, barcode):		
		with open('items.json', 'r') as json_file:
			items = json.load(json_file)
			item = next((elem for elem in items['items'] if elem['barcode'] == barcode), None)
			if item == None:
				return None
			else:
				return Item(item['barcode'], item['name'])
	
	def read_shelf(self, shelfID):
		with open('shelf.json', 'r') as json_file:
			shelf = json.load(json_file)
			segment = next((elem for elem in shelf['shelf'] if elem['shelfID'] == shelfID), None)
			if segment == None: 
				return None		
			else :
				return Segment(shelfID, segment['itemName'], segment['expiryDate'], segment['barcode'])	