
def create_pir_block():
    with open(PIR_BLOCK_FILE, "w") as f:
        f.write("a")

def remove_pir_block():
    if os.path.exists(PIR_BLOCK_FILE):
        os.remove(PIR_BLOCK_FILE)
    else:
        logging.warn("Tried to delete block file but {} does not exist".format(PIR_BLOCK_FILE))



class DisablePir:

	def __init__():
		pass

	def __enter__():
		create_pir_block()

	def __exit__():
		remove_pir_block()
