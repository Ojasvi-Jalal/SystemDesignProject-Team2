#include "SDPArduino.h"
#include <Wire.h>

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6

void setup(){
  SDPsetup();
}

void loop(){

    // Start moving up
    motorBackward(1,100);
    int angle = 0;
    do{
        // Request motor deltas
        Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
        if(Wire.available()){
            angle += Wire.read();
        }
        for(int x = 1; x < ROTARY_NUM; x++){
          Wire.read();
        }
        Serial.println((String) angle);
        delay(200);
    }while(angle > -3000);
    motorAllStop();
    delay(3000);

    motorForward(1,100);
    do{
        // Request motor deltas
        Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
        if(Wire.available()){
            angle = angle - Wire.read();
        }
        for(int x = 1; x < ROTARY_NUM; x++){
          Wire.read();
        }
        Serial.println((String) angle);
        delay(200);
    }while(angle < 0);
    motorAllStop();
    delay(3000);
}
