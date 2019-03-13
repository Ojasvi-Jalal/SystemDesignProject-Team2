
class Item:

	def __init__(self, barcode, name, expiry):
		self.name = name
		self.barcode =  barcode
		self.expiry = expiry

	def __str__(self):
		return "Item : " + self.name + "\nBarcode : " + str(self.barcode) + "\nExpiry : " + self.expiry

	__repr__ = __str__