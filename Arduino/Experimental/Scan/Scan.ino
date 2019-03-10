#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>
#include "QueueArray.h"

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6

// angle is the motor's angle.
int angles[6] = {};
bool irSensor = 0;
QueueArray<String> orders;
char job = '0';
int shelf = NULL;
int MIN_SPEED = 75;

void setup(){
    SDPsetup();
    Serial.println("Started");
}

void loop(){
    Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);
    if(Wire.available()){
        Serial.print("Incoming value: ");
        for(int x = 0; x < ROTARY_NUM; x++){
            angles[x] += (int8_t) Wire.read();
            Serial.print((String) angles[x] + ", ");
        }
    }
    Serial.print(((String) readDigitalSensorData(3)) + ", ");
    Serial.print(((String) digitalRead(3)) + ", ");
    Serial.print(((String) digitalRead(5)) + ", ");

    if(Serial.available() != 0){
        Serial.print("GETTING ORDER!");
        String order = Serial.readString();
        orders.push(order);
        Serial.print("Order: " + order);
    }

    delay(100);

   Serial.print("DOING JOB");
    doJob();
    

    if(!orders.isEmpty()) Serial.print(orders.peek());
    Serial.println();
}

void getJob(){
    Serial.print("GETTING JOB ");
    String order = "0;";
    if(!orders.isEmpty()){
        Serial.print("B U G");
        order = orders.pop();
    }
    Serial.print((String) orders.count());
    job = order.charAt(0);
    if(order.length() > 1 && isDigit(order.charAt(1))){
        String temp = "" + order.charAt(1);
        shelf = temp.toInt();
    }
    Serial.print(" END OF GETTING JOB ");
}

void doJob(){
  Serial.print("Job: " + (String) job);
    switch(job){
        case 'n':
        scan();
        break;
        case 'o':
        origin();
        break;
        default:
        getJob();
    }
}

void scan(){
    int v = angles[1];
    int h = angles[0];
    int d = -6000;
    if(v > d){
        if((v-d) < MIN_SPEED){
            motorBackward(1, MIN_SPEED);
        }else if((v-d) < 100){
            motorBackward(1, (v-d));
        }else{
            motorBackward(1, 100);
        }
    }else if(h < 950){
        motorForward(0, 50);
        if(irSensor) Serial.print("DETECTED");
    }else{
        job = NULL;
    }
}

void origin(){
    Serial.print("ORIGINATING");
    int x = digitalRead(3);
    int y = digitalRead(5);
    if(x == 1) motorBackward(0, 80);
    else motorStop(0);
    if(y == 1) motorForward(1, 80);
    else motorStop(1);
    if(x == 0 && y == 0) job = NULL;
    angles[0] = 0;
    angles[1] = 0;
}
