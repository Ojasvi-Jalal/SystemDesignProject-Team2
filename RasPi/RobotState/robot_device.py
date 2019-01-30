# RobotState/simple_device.py

from robot_states import AtOriginEmpty

# A state machine for the functionality of our robot.

class RobotDevice(object):

    def __init__(self):
        # Start with a default origin state.
        self.state = AtOriginEmpty()

    def on_event(self, event):
        """
        Incoming events are passed to the given states which 
        then handles the event. The result is then assigned 
        as the new state.
        """

        # The next state will be the result of the on_event function.
        self.state = self.state.on_event(event)