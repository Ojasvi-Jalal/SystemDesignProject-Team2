#include "SDPArduino.h"
#include <Wire.h>
int i = 0;
int state = 0;

//Position of the robot. The first entry of the array corresponds to x, the second to y
int position[2] = {0,0};

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

void moveToPlace (int[] moveTo){
    int moveRight = moveTo[0] - position[0];
    int moveUp = moveTo[1] - position[1];
    //Move the appropriate amount towards the right or the left
    if(moveRight <0){
        for(int i = 0, i<abs(moveRight), i++){
            goLeft();
        }
    }
    else{
        for(int i = 0, i<moveRight, i++){
            goRight();
        }
    }

    if(moveUp<0){
        for(int i = 0, i<abs(moveRight), i++){
            //Move the corresponding motor downwards;
        }
    }
    else{
        for(int i = 0, i<moveRight, i++){
            //Move the corresponding motor upwards;
        }
    }

}

void goRight(){
    motorForward(0, 200);
    delay(2500);
    motorAllStop();
}
void goLeft(){
    motorBackward(0, 200);
    delay(2500);
    motorAllStop();
}
