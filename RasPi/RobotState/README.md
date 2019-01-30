# Basic Robot Device States

 needs to be expanded, based on:  
https://ifile.inf.ed.ac.uk/index.php?path=%2Fafs%2Finf.ed.ac.uk%2Fuser%2Fs16%2Fs1658173%2FSDP%2FPI&finishid=59e0f3945eb1dc69c31e8bd4e9ab38bd

## Instructions

At the moment, if terminal dir is in same directory

 $ python
 
 \>\>\> from robot_device import RobotDevice()
 
 \>\>\> robot = RobotDevice()
 
 // Outputs origin state
 
 \>\>\> robot.on_event('item_placed')
 
 // Outputs new state
