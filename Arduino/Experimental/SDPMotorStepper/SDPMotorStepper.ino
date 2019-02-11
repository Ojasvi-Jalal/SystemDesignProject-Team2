#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6

// angle is the motor's angle.
int angles[ROTARY_NUM] = {0};

void setup(){
    SDPsetup();
    while(!Serial){;} // Wait until serial is established
    Serial.println("Serial Connected");
    Serial.println("Started");
}

void loop(){
    while (Serial.available() == 0);

    int inp = Serial.parseInt();

    if(inp <= 0){
      Serial.println("IM A BUG");
        goOrigin();
    }else{
        goAngle(inp, 0);
        Serial.println("Reached: " + (String) angles[0]);
        //delay(5000);
        //goOrigin();
    }
}

void goAngle(int delta, int motor){
    motorForward(motor, 100);
    do{
        // Request motor deltas
        Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
        if(Wire.available()){
            int incoming[ROTARY_NUM] = {0};
            for(int x = 0; x < ROTARY_NUM; x++){
                incoming[x] = (int8_t) Wire.read();
            }
            angles[motor] = angles[motor] + incoming[motor];
        }
        Serial.println((String) angles[motor]);
        delay(20);

    }while(angles[motor] < delta);
    motorAllStop();
}

void goOrigin(){
    motorBackward(0, 500);
    while(digitalRead(3) == 1){
        delay(10);
    }
    Serial.println("Arrived at origin");
    motorStop(0);
    //If the platform is up, move it down
    angles[0] = 0;
    Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
    int incoming[ROTARY_NUM] = {0};
      for(int x = 0; x < ROTARY_NUM; x++){
        incoming[x] = (int8_t) Wire.read();
      }
}
