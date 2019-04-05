#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6

// angle is the motor's angle.
int angles[6] = {};
//const int trigPin = 9;
//const int echoPin = A1;
long duration;
int distance;

void setup(){
  SDPsetup();
  //pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
  //pinMode(echoPin, INPUT); // Sets the echoPin as an Input
  Serial.println("Started");
}

void loop(){
  Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
  if(Wire.available()){
      Serial.print("Incoming value: ");
      for(int x = 0; x < ROTARY_NUM; x++){
          angles[x] -= (int8_t) Wire.read();
          Serial.print((String) angles[x] + ", ");
      }
      Serial.print((String) digitalRead(0));
      Serial.print((String) digitalRead(1));
      Serial.print((String) digitalRead(2));
      Serial.print((String) digitalRead(3));
      Serial.print((String) digitalRead(4));
      Serial.print((String) digitalRead(5));
      Serial.print((String) digitalRead(6));
      Serial.print((String) digitalRead(7));
      Serial.print((String) digitalRead(8));
      Serial.println((String) digitalRead(9));
  }
  //digitalWrite(trigPin, LOW);
  //delayMicroseconds(2);
  
  // Sets the trigPin on HIGH state for 10 micro seconds
  //digitalWrite(trigPin, HIGH);
  //delayMicroseconds(10);
  //digitalWrite(trigPin, LOW);
  
  // Reads the echoPin, returns the sound wave travel time in microseconds
  //duration = pulseIn(echoPin, HIGH);
  
  // Calculating the distance
  //distance= duration*0.034/2;
  
  // Prints the distance on the Serial Monitor
  //Serial.print("Distance: ");
  //Serial.println(distance);
  delay(100);
}
