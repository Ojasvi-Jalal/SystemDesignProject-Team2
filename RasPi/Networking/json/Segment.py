import json

class Segment:
	
	def __init__(self, shelfID, itemName, expiryDate, barcode):
		self.shelfID = shelfID
		self.itemName = itemName
		self.expiryDate = expiryDate
		self.barcode = barcode
