# RobotState/robot_states.py

from state import State

# list of all robot states

class AtOriginEmpty(State):
    
    # The robot platform is stationary at origin
    
    def on_event(self, event):
        if event == 'item_placed':
            return AtOriginHolding()
        elif event == 'fetch_item':
            return HeadingEmpty()

        return self

class AtOriginHolding(State):
    
    # The robot platform is at origin with known item
    
    def on_event(self, event):
        if event == 'item_taken':
            return AtOriginEmpty()
        elif event == 'store_item':
            return HeadingFull()

        return self

class HeadingEmpty(State):
    
    # The robot platform is heading to shelf empty
    
    def on_event(self, event):
        if event == 'stop':
            return AtShelfEmpty()

        return self

class HeadingFull(State):
    
    # The robot platform is heading to shelf holding item
    
    def on_event(self, event):
        if event == 'stop':
            return AtShelfHolding()

        return self

class AtShelfEmpty(State):
    
    # The robot platform is at shelf space holding nothing
    
    def on_event(self, event):
        if event == 'return_empty':
            return ReturningEmpty()
        elif event == 'collect_item':
            return AtShelfRetrieving()

        return self

class AtShelfHolding(State): 
    
    # The robot platform is at shelf holding item
    
    def on_event(self, event):
        if event == 'return_item':
            return ReturningHolding()
        elif event == 'store_item':
            return AtShelfStoring()

        return self

class AtShelfStoring(State):
    
    # The robot platform is at shelf storing item currently
    
    def on_event(self, event):
        if event == 'stored_item':
            return AtShelfEmpty()

        return self

class AtShelfRetrieving(State):
    
    # The robot platform is at shelf retrieving item
    
    def on_event(self, event):
        if event == 'retrieved_item':
            return AtShelfHolding()

        return self

class ReturningEmpty(State):
    
    # The robot platform is returning to origin platform empty
    
    def on_event(self, event):
        if event == 'at_origin':
            return AtOriginEmpty()

        return self

class ReturningHolding(State):
    
    # The robot platform is returning to origin platform carrying an item
    
    def on_event(self, event):
        if event == 'at_origin':
            return AtOriginHolding()

        return self
