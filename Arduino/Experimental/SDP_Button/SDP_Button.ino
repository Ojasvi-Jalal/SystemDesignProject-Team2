#include "SDPArduino.h"
#include <Arduino.h>
#include <Wire.h>
void setup(){
  SDPsetup();
  Serial.begin(115200);
  while(!Serial){;} // Wait until serial is established
  Serial.println("Serial Connected");
}
void loop(){
  for (int i =0; i<4; i++){
    int a = readDigitalSensorData(i);
    Serial.print("Reading number ");
    Serial.println (a);
    Serial.print(" from ");
    Serial.println(i);
    delay(1000);
  }
}
