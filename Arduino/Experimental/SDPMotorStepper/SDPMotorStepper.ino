#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>
#include <string.h>
#include "Shelf.h"

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6
int MIN_SPEED = 75;
#define HORIZONTAL_MOTOR 0
#define VERTICAL_MOTOR 1
#define GRAB_MOTOR 2

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

    String order = Serial.readStringUntil(';');
    char ord = order[0];
    int delta = (order.substring(1,order.indexOf(';')-1)).toInt();

    switch (ord) {
        case 'R':
        case 'r':
            // Retrieve item from section
            retrieveItem(delta);
            break;
        case 'S':
        case 's':
            // Store item to section
            storeItem(delta);
            break;
        case 'V':
        case 'v':
            MIN_SPEED = 80;
            goAngle(delta, VERTICAL_MOTOR);
            break;
        case 'H':
        case 'h':
            MIN_SPEED = 75;
            if(delta == 0) goOrigin();
            else goAngle(delta, HORIZONTAL_MOTOR);
            break;
        case 'G':
        case 'g':
            if(delta > 0){
                motorBackward(GRAB_MOTOR, 60);
                delay(600);
            }else{
                motorForward(GRAB_MOTOR, 60);
                while(digitalRead(5) == 1){
                    delay(10);
                }
            }
            motorAllStop();
            break;
    }
}

int retrieveItem(int shelf) {
  goToShelf(shelf);
  extendArm();
  liftArm();
  retractArm();
  goOrigin();
}

void liftArm() {
  goAngle(1500, VERTICAL_MOTOR);
}

void lowerArm() {
  goAngle(-1500, VERTICAL_MOTOR); // lower arm
}

int storeItem(int shelf) {
  goToShelf(shelf);
  extendArm();
  lowerArm();
  retractArm();
  goOrigin();
}

int goAngle(int delta, int motor){
    if(motor == HORIZONTAL_MOTOR){
        if(delta > 0)   return moveF(HORIZONTAL_MOTOR, delta);
        else            return moveB(HORIZONTAL_MOTOR, -delta);
    }if(motor == VERTICAL_MOTOR){
        if(delta > 0)   return moveB(VERTICAL_MOTOR, delta);
        else            return moveF(VERTICAL_MOTOR, -delta);
    }
    return -1;
}

int moveF(int motor, int d){
    int s = 0;
    if(d < 0){
        Serial.println("Trying to move negative forwards, don't do this!");
        return -1;
    }
    do{
        s += read(motor);
        if((d-s) < MIN_SPEED){
            motorForward(motor, MIN_SPEED);
        }else if((d-s) < 100){
            motorForward(motor, (d-s));
        }else{
            motorForward(motor, 100);
        }
    }while(d > s);
    motorAllStop();
    delay(100);
    s += read(motor);
    Serial.println("Arrived to: "+(String) s);
    return s;

}
int moveB(int motor, int d){
    int s = 0;
    if(d < 0){
        Serial.println("Trying to move negative backwards, don't do this!");
        return -1;
    }
    do{
        s += read(motor);
        if((d+s) < MIN_SPEED){
            motorBackward(motor, MIN_SPEED);
        }else if((d+s) < 100){
            motorBackward(motor, (d+s));
        }else{
            motorBackward(motor, 100);
        }
    }while(d > -s);
    motorAllStop();
    delay(1000);
    s += read(motor);
    Serial.println("Arrived to: "+(String) s);
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
    read(0);
}

void goToShelf(int shelf){
  Serial.println("Going to shelf");
    switch (shelf){
        case 1:
        moveF(HORIZONTAL_MOTOR, 131);
        break;
        case 2:
        moveF(HORIZONTAL_MOTOR, 318);
        break;
        case 3:
        moveF(HORIZONTAL_MOTOR, 528);
        break;
        case 4:
        moveF(HORIZONTAL_MOTOR, 709);
        break;
    }
}
void extendArm(){
    motorBackward(GRAB_MOTOR, 60);
    delay(600);
    motorAllStop();
}
void retractArm(){
    motorForward(GRAB_MOTOR, 60);
    while(digitalRead(5) == 1){
        delay(10);
    }
    motorAllStop();
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
        Serial.println("Couldn't read motor sensors");
        return 0;
    }
}
