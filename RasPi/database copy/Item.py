
class Item:

	def __init__(self, barcode, name):
		self.name = name
		self.barcode =  barcode

	def __str__(self):
		return "Item : " + self.name + "\nBarcode : " + str(self.barcode)

	__repr__ = __str__