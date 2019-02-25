import json

class Segment:
	
	def __init__(self, shelfID, itemName, expiryDate, barcode):
		self.shelfID = shelfID
		self.itemName = itemName
		self.expiryDate = expiryDate
		self.barcode = barcode

	def __str__(self):
		return "ShelfID : " + str(self.shelfID)

	# Convert to json so it can be sent over socketio
	def to_json(self):
		return {
			"pos": self.shelfID,
			"name": self.itemName,
			"expiry": self.expiryDate,
			"barcode": self.barcode
		}

	__repr__ = __str__