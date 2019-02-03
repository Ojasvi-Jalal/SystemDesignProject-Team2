#include "SDPArduino.h"
#include <Wire.h>
int state = 0;
int height = 0;
int pos [2] = {0,0};
int shelf [9][2] = {{0,0}, {1,0}, {2, 0}, {3, 0}, {4, 0},
                           {1,1}, {2, 1}, {3, 1}, {4, 1}};

void setup(){
  SDPsetup();
  helloWorld();
  while(!Serial){;} // Wait until serial is established
}

/*
Right now the shelf is the following:
  |5|6|7|8|
  ---------
 0|1|2|3|4|

 State 0 is the beginning
*/

void loop(){
  while (Serial.available() == 0);
  int inp = Serial.parseInt(); 
  
  //Decide how much it needs to move horizontally
  int hrz = hrzMove(shelf[state][0], shelf[inp][0]);
  Serial.print("Horizontal ");
  Serial.println(hrz);
  
  //Decide how much it needs to move vertically
  int vrt = vrtMove(shelf[state][1], shelf[inp][1]);
  Serial.print("Vertical ");
  Serial.println(vrt);
  //for (int i =0; i<abs(hrz); i++){
    if(hrz<0){
      goLeft(abs(hrz)*800);
      delay(1000);
      //motorAllStop();
    }
    else if(hrz>0){
      goRight(hrz*800);
      delay(1000);
      //motorAllStop();
    }
  //}
  //motorAllStop();
  if(vrt<0){
    goDown();
    delay(1100);
    //motorAllStop();
  }
  else if(vrt>0){
    goUp();
    delay(1100);
    //motorAllStop();
  }
  //motorAllStop();
  state = inp;
}

/*void loop(){
    //if(readAnalogSensorData(0)>0){
     // Serial.println("Pushed");
    //}
    
    int next = Serial.read();
    switch(state){
        case 0:
            if (next == 'r'){
                state = 1;
                Serial.println("GO RIGHT");
                goRight();
                delay(2500); //Added this delay for it to follow commands of the lrlrl style (only does one otherwise)
            }
            break;
        case 1:
            if (next == 'l'){
                state = 0;
                Serial.println("GO LEFT");
                goLeft();
                delay(2500);
            }
            break;
        default:
            break;
    }
    
    switch(height){
        case 0:
          if(next == 'u'){
              height =1;
              Serial.println("GO UP");
              goUp();
              delay(2500);
          }
          break;
        
        case 1:
           if(next =='d'){
               height = 0;
               Serial.println("GO DOWN");
               goDown();
               delay(2500);
           }
           break;
         default: 
           break;
    }
}*/

int hrzMove(int from, int to){
  return (to-from);
}

int vrtMove(int from, int to){
  return (to-from);
}

void goRight(int i){
  Serial.println("MOVING RIGHT");
    motorBackward(0, 500);
    delay(i);
    motorAllStop();
}
void goLeft(int i){
  Serial.println("MOVING LEFT");
    motorForward(0, 500);
    delay(i);
    motorAllStop();
}

void goUp(){
  Serial.println("MOVING UP");
    motorBackward(1, 200);
    delay(900);
    motorStop(1);
}

void goDown(){
  Serial.println("MOVING DOWN");
    motorForward(1, 200);
    delay(900);
    motorStop(1);
  }
