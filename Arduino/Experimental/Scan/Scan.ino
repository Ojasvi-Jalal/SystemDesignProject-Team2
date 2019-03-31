#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>
#include "QueueArray.h"

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6
#define GRAB_MOTOR 2

// angle is the motor's angle.
int angles[6] = {};

//Variables for the sensors data
int irSensor = 1;

// defines pins numbers for the ultrasonic sensor
const int trigPin = 9;
const int echoPin = A1;

// defines variables for the ultrasonic sensor
long duration;
int distance;
bool holding;
int level = 0;

//Job the robot has to do
char job = 'o';

//Shelf the robot has to go to
int shelf = 0;

//Shelf storage
int items[6] = {};
int stats[6] = {};
int counter = 0;

//Coordinates of the shelf, vertically and horizontally
int angleShelf[6] = {380, 575, 783, 987, 502, 865};
int verticality[2] = {235, 3600};
const int up = 1500;
String orders;
int reset = 0;
bool armOut = true;

//Setting the speed of the movement
int VERTICAL_MIN = 100;
int VERTICAL_ORG = 100;
int HORIZTAL_MIN = 80;
int HORIZTAL_ORG = 80;

void setup(){
    SDPsetup();
    //Prepare the pins for the ultrasonic sensor
    pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
    pinMode(echoPin, INPUT); // Sets the echoPin as an Input

    //Serial.println("Started");
}

void loop(){
    Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_NUM);

    if(Wire.available()){
        //Serial.print("Incoming value: ");
        angles[0] -= (int8_t) Wire.read();
        angles[5] -= (int8_t) Wire.read();
        angles[1] -= (int8_t) Wire.read();
        angles[5] -= (int8_t) Wire.read();
        angles[5] -= (int8_t) Wire.read();
        angles[5] -= (int8_t) Wire.read();
        //for(int x = 0; x < ROTARY_NUM; x++){
        //    angles[x] -= (int8_t) Wire.read();
            //Serial.print((String) angles[x] + ", ");
        //}
    }
    //Serial.print(((String) readDigitalSensorData(2)) + ", ");
    irSensor = readDigitalSensorData(3);
    readUltrasound();
    if(distance < 10) holding = true;
    else holding = false;

    //Serial.print(((String) digitalRead(3)) + ", ");
    //Serial.print(((String) digitalRead(5)) + ", ");

    if(Serial.available() != 0){
        //Serial.print("GETTING ORDER!");
        String order = Serial.readString();
        //Serial.print(order);
        orders.concat(order);
        //Serial.print("GOT ORDER!");
    }

    //Serial.println(orders);

    delay(30);

   //Serial.print("DOING JOB");
    doJob();


    //if(!orders.isEmpty()) Serial.print(orders.peek());
    //Serial.println();
}

void getJob(){
    //Serial.print("GETTING JOB ");
    char order = '0';
    if(!orders.equals("")){
        //Serial.print("B U G");
        order = orders.charAt(0);
        orders.remove(0,1);
    }
    //Serial.print((String) orders.count());
    job = order;
    while(!orders.equals("") && isDigit(orders.charAt(0))){
        String temp = "";
        temp.concat(orders.charAt(0));
        Serial.println(orders.charAt(0));
        Serial.println(temp + "ubbh");
        shelf = temp.toInt();
        orders.remove(0,1);
    }
    //Serial.print(" END OF GETTING JOB ");
}

void doJob(){
    //Serial.println("Job: " + (String) job);
    switch(job){
        case 'n':
        scan();
        break;
        case 't':
        test();
        break;
        case 'e':
        endTest();
        break;
        case 'o':
            origin();
            break;
        case 'u':
            goUp();
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
    retractArm();
    if(holding){
        Serial.println("x");
        Serial.println("Can't Scan with hands full!");
        level = 0;
        job = 'o';
    }
    int v = angles[1];
    int h = angles[0];
    if (level == 0) level = 2;
    int vaim = verticality[level-1]+up;
    if(v < vaim && level == 2){
        if((vaim-v) < VERTICAL_MIN){
            motorBackward(1, VERTICAL_MIN);
        }else if((vaim-v) < 100){
            motorBackward(1, (vaim-v));
        }else{
            motorBackward(1, 100);
        }
    }else if(v > vaim && level == 1){
        if((v-vaim) < VERTICAL_MIN){
            motorForward(1, VERTICAL_MIN);
        }else if((v-vaim) < 100){
            motorForward(1, (v-vaim));
        }else{
            motorForward(1, 100);
        }
    }else if(level == 2 && h < 1000){
        motorStop(1);
        motorBackward(0, HORIZTAL_MIN);
        if(irSensor == 0 || (distance>=12 && distance<=20)) detect();
    }else if(level == 1 && h > 200){
        motorStop(1);
        motorForward(0, HORIZTAL_MIN);
        if(irSensor == 0 || (distance>=12 && distance<=20)) detect();
    }else{
        for(int i = 0; i < 6; i++) items[i] = 0;
        motorStop(0);
        if (level == 1){
            level = 0;
            job = 'o';
            Serial.println("e");
        }else{
            level = 1;
        }
    }
}

void origin(){
    //Serial.print("ORIGINATING");
    //Read from buttons
    retractArm();
    int x = digitalRead(3);
    int y = digitalRead(5);
    if(x == 1) motorForward(0, HORIZTAL_ORG);
    else motorStop(0);
    if(y == 1) motorForward(1, VERTICAL_ORG);
    else motorStop(1);
    if(x == 0 && y == 0){
      job = '0';
      delay(200);
      Serial.println("o");
      extendArm();
    }
    angles[0] = 0;
    angles[1] = 0;
}

void goToShelf(int vertical){
    retractArm();
    int v = angles[1];
    int h = angles[0];

    int horizontal = angleShelf[shelf%4];

    //Horizontal slowing down
    if(h < horizontal){
        if((h-horizontal) < HORIZTAL_MIN){
            motorForward(0, HORIZTAL_MIN);
        }else if((h-horizontal) < 100){
            motorForward(0, (h-horizontal));
        }else{
            motorForward(0, 100);
        }
    }
    else motorStop(0);

    //Vertical slowing down
    if(v < vertical){
        if((vertical-v) < VERTICAL_MIN){
            motorBackward(1, VERTICAL_MIN);
        }else if((vertical-v) < 100){
            motorBackward(1, (vertical-v));
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
    //Serial.println((String) shelf + " getting to");
    int toH = angleShelf[shelf%4];
    //Serial.println("toH: " + (String) toH);
    int toV = verticality[0];
    //Serial.println((String) shelf);
    if(shelf>3){
        toV = verticality[1];
    }
    if(v <= toV+400 && h >= toH){
      /*Serial.println("Got to the right place");
      Serial.println((String) v);
      Serial.println((String) toV);
      Serial.println((String) h);
      Serial.println((String) toH);
        /*while(angles[1] <= toV+500){
            //Get the angles at the moment
            for(int x = 0; x < ROTARY_NUM; x++){
                angles[x] += (int8_t) Wire.read();
                //Serial.print((String) angles[x] + ", ");
            }
            motorForward(1, 100);
        }*/
        motorStop(1);
        delay(10);
        //Once it has arrived to the vertical goal (right underneath the object), take out the fork
        extendArm();
        delay(100);

        //Go up to take the item
        //Serial.println((String)angles[1]);
        //Serial.println((String)(toV-200));
        while(angles[1] >= (toV +100)){
          //Serial.println("Went in");
          //Serial.println((String)angles[1]);
            motorBackward(1, 100);
            for(int x = 0; x < ROTARY_NUM; x++){
                angles[x] += (int8_t) Wire.read();
                //Serial.print((String) angles[x] + ", ");
            }
            delay(10);
        }
        motorStop(1);
        delay(20);
        retractArm();
        job = 'o';
    }
    else{
      goToShelf(toV+400);
    }
}

void storeItem(){
    int v = angles[1];
    int h = angles[0];
    //Angles to get to
    int toH = angleShelf[shelf%4];
    int toV = verticality[0];
    if(shelf>3){
        toV = verticality[1];
    }
    if(v <= toV-700 && h >= toH){
        /*while(angles[1] <= toV-500){
            //Get the angles at the moment
            for(int x = 0; x < ROTARY_NUM; x++){
                angles[x] += (int8_t) Wire.read();
            }
            motorForward(1, 100);
        }*/
        motorStop(1);
        //Once it has arrived to the vertical goal, take out the fork
        extendArm();

        //Go down to store the item
        int button = digitalRead(3);
        Serial.println("******" +(String) (toV+50) + "******");
        while(angles[1] <= toV){
          //Serial.println((String) angles[1]);
            button = digitalRead(3);
            motorForward(1, 100);
            for(int x = 0; x < ROTARY_NUM; x++){
                angles[x] -= (int8_t) Wire.read();
                //Serial.print((String) angles[x] + ", ");
            }
            delay(5);
        }
        motorStop(1);
        //Retract the arm
        delay(20);
        retractArm();
        delay(100);
        //Go to the origin
        job = 'o';
    }
    else{
        goToShelf(toV-700);
    }
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
    if(pos > 360 && pos < 400  && level == 2) setItem(0);
    if(pos > 555 && pos < 595  && level == 2) setItem(1);
    if(pos > 763 && pos < 803  && level == 2) setItem(2);
    if(pos > 967 && pos < 1007 && level == 2) setItem(3);
    if(pos > 835 && pos < 935  && level == 1) setItem(4);
    if(pos > 450 && pos < 550  && level == 1) setItem(5);
}

void setItem(int i){
    if (items[i] == 0){
         items[i] = 1;
         stats[i]++;
         Serial.println(i);
    }
}

void goUp(){
    //Serial.print("UP");
    if(angles[1] < 2000) motorBackward(1, 100);
    else{
        motorStop(1);
        job = '0';
    }
}

void test(){
    if(counter < 20){
      orders.concat("n");
      counter++;
    }
    else{
      //job = "0";
      counter = 0;
      orders.concat("e");
    }

}

void endTest(){
    for(int i = 0; i < 6; i++) Serial.println( (String) stats[i] + ", ");
    for(int i = 0; i < 6; i++) stats[i] = 0;
    //job = "0";
}

void extendArm(){
    if(armOut) return;
    motorBackward(GRAB_MOTOR, 80);
    delay(750);
    motorAllStop();
    armOut = true;
}
void retractArm(){
    if(!armOut) return;
    motorForward(GRAB_MOTOR, 80);
    delay(750);
    motorAllStop();
    armOut = false;
}

void readUltrasound(){
    // Clears the trigPin
    digitalWrite(trigPin, LOW);
    delayMicroseconds(2);

    // Sets the trigPin on HIGH state for 10 micro seconds
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);

    // Reads the echoPin, returns the sound wave travel time in microseconds
    duration = pulseIn(echoPin, HIGH);

    // Calculating the distance
    distance= duration*0.034/2;
}

// void goVertical(int angle){
//     //vertical position of the robot
//     int v = angles[1];
//
//     if(v > angle){
//         if(-(v-angle) >= VERTICAL_MIN){
//             motorBackward(1, VERTICAL_MIN);
//         }else if((v-angle) < 100){
//             motorBackward(1, -(v-angle));
//         }else{
//             motorBackward(1, 100);
//         }
//     }
// }
//
// void goHorizontal(int angle){
//
// }
