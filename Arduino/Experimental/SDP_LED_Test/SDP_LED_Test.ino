#include "SDPArduino.h"
#include <Wire.h>
int state = 0;

void setup(){
  SDPsetup();
  while(!Serial){;} // Wait until serial is established
  Serial.println("Serial Connected");
}

void loop(){
  while (Serial.available() == 0);
  int inp = Serial.parseInt();

  if(state==0&&inp==1){
      digitalWrite(13, HIGH);
      state = 1;
  } else if(state==1&&inp==0){
      digitalWrite(13, LOW);
      state = 0;
  }
}
