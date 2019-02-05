#include "SDPArduino.h"
#include <Wire.h>
int state = 0;
int height = 0;
int pos [2] = {0,0};
int shelf [9][2] = {{0,0}, {1,0}, {2, 0}, {3, 0}, {4, 0},
                           {1,1}, {2, 1}, {3, 1}, {4, 1}};

//Position of the robot. The first entry of the array corresponds to x, the second to y
int position[2] = {0,0};

void setup(){
  SDPsetup();
  while(!Serial){;} // Wait until serial is established
  Serial.println("Serial Connected")
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

  if(inp == 9){
      goOrigin();
  }
  else{

        //Decide how much it needs to move horizontally
        int hrz = hrzMove(shelf[state][0], shelf[inp][0]);
        Serial.print("Horizontal ");
        Serial.println(hrz);

        //Decide how much it needs to move vertically
        int vrt = vrtMove(shelf[state][1], shelf[inp][1]);
        Serial.print("Vertical ");
        Serial.println(vrt);

        //Move horizontally if necessary
        if(hrz<0){
            goLeft(abs(hrz)*800);
            delay(1000);
        }
        else if(hrz>0){
          goRight(hrz*800);
          delay(1000);
        }
        //Move vertically if necessary
        if(vrt<0){
          goDown();
          delay(1100);
        }
        else if(vrt>0){
          goUp();
          delay(1100);
        }
        //Change the state to the one it just got to
        state = inp;
  }
}

//Return how much it needs to move horizontally. Negative is to the left, positive to the right
int hrzMove(int from, int to){
  return (to-from);
}

//Return how much ot needs to move vertically. Negative down, positive up.
int vrtMove(int from, int to){
  return (to-from);
}

//Make the robot go to the right
void goRight(int i){
  Serial.println("MOVING RIGHT");
  motorBackward(0, 500);
  delay(i);
  motorAllStop();
}

//Make the robot go to the left
void goLeft(int i){
  Serial.println("MOVING LEFT");
  motorForward(0, 500);
  delay(i);
  motorAllStop();
}

//Make the platform go up
void goUp(){
  Serial.println("MOVING UP");
  motorBackward(1, 200);
  delay(5000);
  motorStop(1);
}

//Make the platform go down
void goDown(){
  Serial.println("MOVING DOWN");
  motorForward(1, 200);
  delay(5000);
  motorStop(1);
}

//Go to the origin
void goOrigin(){
    while(digitalRead(3) != 1){
        motorForward(0, 500);
    }
    motorStop(0);
    state = 0;
}
