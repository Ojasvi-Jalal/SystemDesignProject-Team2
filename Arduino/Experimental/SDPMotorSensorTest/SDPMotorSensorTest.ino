#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6

// angle is the motor's angle.
int angles[6] = {};

void setup(){
  SDPsetup();
  Serial.println("Started");
}

void loop(){
  Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
  if(Wire.available()){
      Serial.print("Incoming value: ");
      for(int x = 0; x < ROTARY_NUM; x++){
          angles[x] += (int8_t) Wire.read();
          Serial.print((String) angles[x] + ", ");
      }
      Serial.println((String) readDigitalSensorData(3));
  }
  delay(100);
}
