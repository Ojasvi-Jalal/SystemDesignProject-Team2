#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6

// angle is the motor's angle.
int angle = 0;

void setup(){
  SDPsetup();
  Serial.println("Started");
}

void loop(){

    // Start moving up
    motorBackward(1,30);
    do{
        // Request motor deltas
        Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
        if(Wire.available()){
            Serial.print("Incoming value: ");
            int incoming = (int8_t) Wire.read();
            Serial.println((String) incoming);
            for(int x = 1; x < ROTARY_NUM; x++){
                Wire.read();
            }
            angle = angle + (int) incoming;
        }
        Serial.print("Current angle: ");
        Serial.println((String) angle);
        delay(200);

    }while(angle > -180);

    Serial.println("About to stop");
    motorAllStop();
    delay(3000);

    motorForward(1,30);
    do{
        // Request motor deltas
        Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
        if(Wire.available()){
            Serial.print("Incoming value: ");
            int incoming = (int8_t) Wire.read();
            Serial.println((String) incoming);
            for(int x = 1; x < ROTARY_NUM; x++){
                Wire.read();
            }
            angle = angle + (int) incoming;
        }
        Serial.print("Current angle: ");
        Serial.println((String) angle);
        delay(200);

    }while(angle < 0);
    motorAllStop();
    delay(3000);
}
