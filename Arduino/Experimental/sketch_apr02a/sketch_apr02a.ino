#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>

// Define constants and variables
const int LED = A2;                   // sets the LED on pin 13
const int  STATE = 9;                 // sets pin 2 for sensor reading
int r_state = 0;                      // reset to zero the variable used to read the state of the OUT pin of the sensor
 
// Initialization
void setup(){
  SDPsetup();
Serial.println("HEeeeey");  
  pinMode (LED, OUTPUT);               // sets pin 13 as digital output
  pinMode (STATE, INPUT);              // sets pin 2 as digital input 
}

// main loop
void loop(){
  r_state = digitalRead(STATE); // reads the status of the sensor
  Serial.println(r_state);
  delay(10);
  if(r_state == 0)              // if is there an obstacle (OUT = 0)
    digitalWrite (LED, HIGH);   // turn on the led
  else
    digitalWrite (LED, LOW);    // turn off the led
}
