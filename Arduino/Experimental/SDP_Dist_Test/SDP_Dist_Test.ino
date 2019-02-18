#include "SDPArduino.h"
#include <Wire.h>
int state = 0;

/*void setup(){
  SDPsetup();
  while(!Serial){;} // Wait until serial is established
  Serial.println("Serial Connected");
}

void loop(){
  //while (Serial.available() == 0);
  //for (int i=0; i<12; i++){
    //if(readAnalogSensorData(i)>0){
      int a = (long)readDigitalSensorData(0);
      delay(500);
      Serial.println("Read from sensor");
      delay(500);
      Serial.println(a);
    //}
    delay(2000);
  //}
}*/
// defines pins numbers
const int trigPin = 3;
const int echoPin = A3;
// defines variables
long duration;
int distance;
void setup() {
pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
pinMode(echoPin, INPUT); // Sets the echoPin as an Input
Serial.begin(9600); // Starts the serial communication
}

void loop() {
// Clears the trigPin
digitalWrite(trigPin, LOW);
delayMicroseconds(2);

// Sets the trigPin on HIGH state for 10 micro seconds
digitalWrite(trigPin, HIGH);
delayMicroseconds(10);
digitalWrite(trigPin, LOW);

// Reads the echoPin, returns the sound wave travel time in microseconds
duration = pulseIn(echoPin, HIGH);

// Calculating the distance
distance= duration*0.034/2;

long b= digitalRead(A1);
// Prints the distance on the Serial Monitor
Serial.print("Distance: ");
Serial.println(distance);
}

