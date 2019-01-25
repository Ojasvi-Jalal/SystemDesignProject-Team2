#include "SDPArduino.h"
#include <Wire.h>
int i = 0;
int state = 0;

void setup(){
  SDPsetup();
  helloWorld();
  while(!Serial){;} // Wait untill serial is established
}

void loop(){
    int next = Serial.read();
    switch(state){
        case 0:
            if (next == 'r'){
                state = 1;
                Serial.println("GO RIGHT");
                goRight();
            }
            break;
        case 1:
            if (next == 'l'){
                state = 0;
                Serial.println("GO LEFT");
                goLeft();
            }
            break;
        default:
            break;
    }
}

void goRight(){
    motorForward(0, 50);
    delay(2500);
    motorAllStop();
}
void goLeft(){
    motorBackward(0, 50);
    delay(2500);
    motorAllStop();
}
