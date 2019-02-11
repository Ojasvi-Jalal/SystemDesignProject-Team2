
/*

 * Master board sample code to be used in conjuction with the rotary encoder

 * slave board and sample code.

 * This sketch will keep track of the rotary encoder positions relative to

 * the origin. The origin is set to the position held when the master board

 * is powered.

 *

 * Rotary encoder positions are printed to serial every 200ms where the

 * first result is that of the encoder attached to the port at 11 o'clock

 * on the slave board (with the I2C ports at at 12 o'clock). The following

 * results are in counter-clockwise sequence.

 *

 * Author: Chris Seaton, SDP Group 7 2015

 */

 

/*#include <Wire.h>
#include "SDPArduino.h"



#define ROTARY_SLAVE_ADDRESS 5

#define ROTARY_COUNT 6

#define PRINT_DELAY 200



// Initial motor position is 0.

int positions[ROTARY_COUNT] = {0};



void setup() {

  digitalWrite(8, HIGH);  // Radio on

  Serial.begin(115200);  // Serial at given baudrate

  Wire.begin();  // Master of the I2C bus
  motorAllStop();

}



void loop() {
  motorForward(1, 35);
  /*Serial.print("reading 1: ");
  Serial.println((int8_t) Wire.read());
  //Serial.print("reading 6: ");
  //Serial.println(analogRead(6));
  delay(1100);
  motorAllStop();
  delay(500);
  motorBackward(1, 80);
  Serial.print("reading 1: ");
  Serial.println((int8_t) Wire.read());
  delay(1000);*/
  

 /* updateMotorPositions();

  printMotorPositions();

}



void updateMotorPositions() {

  // Request motor position deltas from rotary slave board

  Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_COUNT);

  

  // Update the recorded motor positions

  for (int i = 0; i < ROTARY_COUNT; i++) {
    Serial.print("Reading ");
    Serial.print(i);

    positions[i] += (int8_t) Wire.read();  // Must cast to signed 8-bit type
    Serial.print(": ");
    Serial.println(Wire.read());

  }

}



void printMotorPositions() {

  Serial.print("Motor positions: ");

  for (int i = 0; i < ROTARY_COUNT; i++) {

    Serial.print(positions[i]);

    Serial.print(' ');

  }

  Serial.println();

  delay(PRINT_DELAY);  // Delay to avoid flooding serial out

}*/





// we need to read angles instead: wire.read() always returns -1, so it's not going to solve the problem



#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>
#include <iostream>

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6

int angle = 0;

void setup(){
  SDPsetup();
  Serial.println("Started");
}

void loop(){
  //motorAllStop();

    // Start moving up
      motorBackward(1,30);
    
    do{
      motorBackward(1,30);
        // Request motor deltas
        Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
        if(Wire.available()){
          Serial.println("inside wire available");
          Serial.println((int8_t) Wire.read()); 
          for(int x = 0; x < ROTARY_NUM; x++){
            //Wire.read();
            angle = (int8_t) angle + Wire.read();
          }
          //motorAllStop();
          delay(500);
        }
        Serial.println((String) angle);
        delay(200);
        
    }while(angle > -3);
    
    Serial.println("About to stop");
    motorAllStop();
    //delay(3000);

    motorForward(1,30);
    do{
        // Request motor deltas
        Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM, false);
        if(Wire.available()){
            Serial.println("inside wire available 2");
            for(int x = 0; x < ROTARY_NUM; x++){
               Serial.println("inside wire available");
                Serial.println(Wire.read());
              angle = (int8_t) (angle - Wire.read());
            }
        }
        Serial.println((String) angle);
        delay(200);
    }while(angle < 0);
    motorAllStop();
    delay(3000);
}

