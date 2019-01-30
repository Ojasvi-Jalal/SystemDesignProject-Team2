# RobotState/state.py

# A state object which provides functions for induvidual robot states
class State(object): 

    def __init__(self):
        print 'Current state:', str(self)

    # Handle events that are given to the state.
    def on_event(self, event):
        pass

    # decribe state
    def __repr__(self):
        return self.__str__()

    # Returns name of the state
    def __str__(self):
        return self.__class__.__name__