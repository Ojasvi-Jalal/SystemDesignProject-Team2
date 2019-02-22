import json

class Segment:
	
	def __init__(self, shelfID, itemName, expiryDate, barcode):
		self.shelfID = shelfID
		self.itemName = itemName
		self.expiryDate = expiryDate
		self.barcode = barcode

	def __str__(self):
		return "ShelfID : " + str(self.shelfID)

	__repr__ = __str__