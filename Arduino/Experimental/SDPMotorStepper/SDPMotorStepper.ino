#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>
#include <stdlib.h>

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

    if(inp == 0){
      Serial.println("inp: " + String(inp));
        goOrigin();
    }else if(inp > 0){
        goAngle(inp, 0, 40);
        delay(1000);
        goAngle(100, 0, 100);
        Serial.println("Reached: " + (String) angles[0]);
        int diff = angles[0] - inp;
        Serial.println("Difference" + (String) diff);
        delay(1000);
        while(abs(diff) >7){
          if(diff > 5){
            goAngleBack(angles[0]-diff, 0, 80);
          }
          else if(diff < -7){
            goAngle(angles[0]-diff, 0, 80);
          }
          diff = angles[0] - inp;
          delay(1000);
          Serial.println("New difference: " + (String) diff);
        }
    }
}

void goAngleBack(int delta, int motor, int speed){
    motorBackward(motor, speed);
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
        delay(5);

    }while(angles[motor] > delta);
    motorAllStop();
    delay(1000);
    Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
    if(Wire.available()){
        int incoming[ROTARY_NUM] = {0};
        for(int x = 0; x < ROTARY_NUM; x++){
            incoming[x] = (int8_t) Wire.read();
        }
        angles[motor] = angles[motor] + incoming[motor];
    }
    Serial.println((String) angles[motor]);
}

void goAngle(int delta, int motor, int speed){
    motorForward(motor, speed);
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
        delay(5);

    }while(angles[motor] < delta);
    motorAllStop();
    delay(1000);
    Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
    if(Wire.available()){
        int incoming[ROTARY_NUM] = {0};
        for(int x = 0; x < ROTARY_NUM; x++){
            incoming[x] = (int8_t) Wire.read();
        }
        angles[motor] = angles[motor] + incoming[motor];
    }
    Serial.println((String) angles[motor]);
}

void goOrigin(){
  Serial.println("Going to origin");
    motorBackward(0, 80);
    Serial.println("After backward");
    while(digitalRead(3) == 1){
      //Serial.println("Delaying");
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
