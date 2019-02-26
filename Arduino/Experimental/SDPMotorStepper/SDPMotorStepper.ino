#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6
#define MIN_SPEED 70
#define HORIZONTAL_MOTOR 0
#define VERTICAL_MOTOR 1

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

    Serial.
    int inp = Serial.parseInt();

    if(inp == -1){
      Serial.println("inp: " + inp);
        goOrigin();
<<<<<<< HEAD
    }else if(inp > 0){
        goAngle(inp, 0, 70);
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
=======
    }if(inp > 0){
        int dist = goAngle(inp, 0);
        Serial.println("Reached: " + (String) dist);
        //delay(5000);
        //goOrigin();
>>>>>>> c2e3b62e3f295081807a9f2278db763848089783
    }
}

int goAngle(int delta, int motor){
    if(motor == HORIZONTAL_MOTOR){
        if(delta > 0)   return moveF(HORIZONTAL_MOTOR, delta);
        else            return moveB(VERTICAL_MOTOR, -delta);
    }if(motor == VERTICAL_MOTOR){
        if(delta > 0)   return moveB(HORIZONTAL_MOTOR, delta);
        else            return moveF(VERTICAL_MOTOR, -delta);
    }
    return -1;
}

int moveF(int motor, int d){
    int s = 0;
    if(d < 0){
        Serial.printf("Trying to move negative forwards, don't do this!");
        return -1;
    }
    do{
        s += read(motor);
        if((d-s) < MIN_SPEED){
            motorForward(motor, MIN_SPEED)
        }else if((d-s) < 100){
            motorForward(motor, (d-s))
        }else{
            motorForward(motor, 100);
        }
    }while(d > s);
    motorAllStop();
    delay(100);
    s += read(motor);
    return s;

}
int moveB(int motor, int d){
    int s = 0;
    if(d < 0){
        Serial.printf("Trying to move negative backwards, don't do this!");
        return -1;
    }
    do{
        s += read(motor);
        if((d+s) < MIN_SPEED){
            motorBackward(motor, MIN_SPEED)
        }else if((d+s) < 100){
            motorBackward(motor, (d+s))
        }else{
            motorBackward(motor, 100);
        }
    }while(d > -s);
    motorAllStop();
    delay(100);
    s += read(motor);
    return s;
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

int read(int motor){
    Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
    if(Wire.available()){
        int incoming[ROTARY_NUM] = {0};
        for(int x = 0; x < ROTARY_NUM; x++){
            incoming[x] = (int8_t) Wire.read();
        }
        return incoming[motor];
    }
    else{
        Serial.printf("Couldn't read motor sensors");
        return 0;
    }
}
