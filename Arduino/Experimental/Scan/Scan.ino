#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>
#include "QueueArray.h"

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6

// angle is the motor's angle.
int angles[6] = {};
int irSensor = 1;
QueueArray<String> orders;
char job = 'o';
int shelf = 0;
int items[4] = {}
int angleShelf [4] = {226, 413, 616, 790};
int verticality[2] = {-2500, -5000};

int VERTICAL_MIN = 100;
int VERTICAL_ORG = 100;
int HORIZTAL_MIN = 70;
int HORIZTAL_ORG = 80;

void setup(){
    SDPsetup();
    //Serial.println("Started");
}

void loop(){
    Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);

    if(Wire.available()){
        //Serial.print("Incoming value: ");
        for(int x = 0; x < ROTARY_NUM; x++){
            angles[x] += (int8_t) Wire.read();
            Serial.print((String) angles[x] + ", ");
        }
    }
    //Serial.print(((String) readDigitalSensorData(3)) + ", ");
    irSensor = readDigitalSensorData(3);
    //Serial.print(((String) digitalRead(3)) + ", ");
    //Serial.print(((String) digitalRead(5)) + ", ");

    if(Serial.available() != 0){
        //Serial.print("GETTING ORDER!");
        String order = Serial.readString();
        orders.push(order);
        //Serial.print("GOT ORDER!");
    }

    delay(30);

   //Serial.print("DOING JOB");
    doJob();


    //if(!orders.isEmpty()) Serial.print(orders.peek());
    Serial.println();
}

void getJob(){
    //Serial.print("GETTING JOB ");
    String order = "0";
    if(!orders.isEmpty()){
        //Serial.print("B U G");
        order = orders.pop();
    }
    //Serial.print((String) orders.count());
    job = order.charAt(0);
    if(order.length() > 1 && isDigit(order.charAt(1))){
        String temp = "" + order.charAt(1);
        shelf = temp.toInt();
    }
    //Serial.print(" END OF GETTING JOB ");
}

void doJob(){
  //Serial.print("Job: " + (String) job);
    switch(job){
        case 'n':
            scan();
            break;
        case 'o':
            origin();
            break;
        case 'u':
            up();
            break;
        case 'r':
            retrieveItem();
            break;
        case 's':
            storeItem();
            break;
        default:
            getJob();
    }
}

void scan(){
    int v = angles[1];
    int h = angles[0];
    int d = -5500;
    if(v > d){
        if(-(v-d) < VERTICAL_MIN){
            motorBackward(1, VERTICAL_MIN);
        }else if((v-d) < 100){
            motorBackward(1, -(v-d));
        }else{
            motorBackward(1, 100);
        }
    }else if(h < 950){
        motorStop(1);
        motorForward(0, HORIZTAL_MIN);
        if(irSensor == 0) detect();
    }else{
        items = {0,0,0,0}
        Serial.print("e");
        motorStop(0);
        job = 'o';
    }
}

void origin(){
    //Serial.print("ORIGINATING");
    //Read from buttons
    int x = digitalRead(3);
    int y = digitalRead(5);
    if(x == 1) motorBackward(0, HORIZTAL_ORG);
    else motorStop(0);
    if(y == 1) motorForward(1, VERTICAL_ORG);
    else motorStop(1);
    if(x == 0 && y == 0){
      job = '0';
      delay(100);
      Serial.print("o");
    }
    angles[0] = 0;
    angles[1] = 0;
}

void goToShelf(int vertical){
    int v = angles[1];
    int h = angles[0];

    int horizontal = angleShelf[shelf];

    //Horizontal slowing down
    if(h < horizontal){
        if((h-horizontal) < HORIZTAL_MIN){
            motorBackward(0, HORIZTAL_MIN);
        }else if((h-horizontal) < 100){
            motorBackward(0, (h-horizontal));
        }else{
            motorBackward(0, 100);
        }
    }
    else motorStop(0);

    //Vertical slowing down
    if(v > vertical){
        if(-(v-vertical) < VERTICAL_MIN){
            motorBackward(1, VERTICAL_MIN);
        }else if((v-vertical) < 100){
            motorBackward(1, -(v-d));
        }else{
            motorBackward(1, 100);
        }
    }
    else motorStop(0);
}

void retrieveItem(){
    int v = angles[1];
    int h = angles[0];
    //Angles to get to
    int toH = angleShelf[shelf];
    int toV = verticality[0];
    if(shelf>3){
        toV = verticality[1];
    }
    if(v <= toV && h >= toH){
        while(angles[1] <= toV+500){
            angles[x] += (int8_t) Wire.read();
            motorForward(1, 100);
        }
        motorStop(1);
        //Once it has arrived to the vertical goal (right underneath the object), take out the fork
        extendArm();

        while(angles[1] >= toV -500){
            angles[x] += (int8_t) Wire.read();
            motorBackward(1, 100);
        }
        retractArm();
        origin();
    }

    //If the angle is bigger than the one we want to get to, go up

    //Go horizontal


}

void storeItem(){

}

int getVangle(){
    if (shelf>3){
        return -4173;
    }
    else{
        return 0;
    }
}

void detect(){
    int pos = angles[0];
    if(pos > 190 && pos < 270) setItem(0);
}

void setItem(int i){
    if (items[i] == 0){
         items[i] = 1;
         Serial.print(toChar(i));
    }
}

void up(){
    //Serial.print("UP");
    if(angles[1] > -1000) motorBackward(1, 100);
    else{
        motorStop(1);
        job = '0';
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

void goVertical(int angle){
    //vertical position of the robot
    int v = angles[1];

    if(v > angle){
        if(-(v-angle) >= VERTICAL_MIN){
            motorBackward(1, VERTICAL_MIN);
        }else if((v-angle) < 100){
            motorBackward(1, -(v-angle));
        }else{
            motorBackward(1, 100);
        }
    }
}

void goHorizontal(int angle){

}
