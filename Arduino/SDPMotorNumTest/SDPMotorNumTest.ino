#include "SDPArduino.h"
#include <Wire.h>
int i = 0;

void setup(){
  SDPsetup();
  helloWorld();
}

void loop(){
    delay(1000);
    int a = digitalRead(A3);
    int b = digitalRead(3);
    Serial.println("3 = " + (String) a + " A3 = " + (String) b);
}
