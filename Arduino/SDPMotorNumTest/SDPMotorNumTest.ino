#include "SDPArduino.h"
#include <Wire.h>
int i = 0;

void setup(){
  SDPsetup();
  helloWorld();
}

void loop(){
Serial.println("Motor 0 50%");
motorForward(0, 50);
delay(2500);
motorAllStop();

Serial.println("Motor 1 50%");
motorForward(1, 50);
delay(2500);
motorAllStop();

Serial.println("Motor 2 50%");
motorForward(2, 50);
delay(2500);
motorAllStop();

Serial.println("Motor 3 50%");
motorForward(3, 50);
delay(2500);
motorAllStop();

Serial.println("Motor 4 50%");
motorForward(4, 50);
delay(2500);
motorAllStop();

Serial.println("Motor 5 50%");
motorForward(5, 50);
delay(2500);
motorAllStop();

}
