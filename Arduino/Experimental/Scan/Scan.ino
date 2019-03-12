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
int irSensor = 1;
char job = 'o';
int shelf = 0;
int items[4] = {};
int stats[4] = {};
int counter = 0;
int angleShelf[4] = {250, 440, 635, 825};
int verticality[2] = {-1500, -4700};
String orders;
int reset = 0;
bool armOut = 0;

int VERTICAL_MIN = 100;
int VERTICAL_ORG = 100;
int HORIZTAL_MIN = 80;
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
            //Serial.print((String) angles[x] + ", ");
        }
    }
    //Serial.print(((String) readDigitalSensorData(2)) + ", ");
    irSensor = readDigitalSensorData(3);
    if (readDigitalSensorData(2) == 0){
        reset++;
    }else{
        reset = 0;
    }if (reset > 50){
        reset = 0;
        if (orders.charAt(orders.length() - 1) != 'n');  //orders.concat('n');
    }

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
      for(int i = 0; i < 4; i++) items[i] = 0;
        Serial.println("e");
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
      delay(200);
      Serial.println("o");
      //extendArm();
    }
    angles[0] = 0;
    angles[1] = 0;
}

void goToShelf(int vertical){
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
    if(v > vertical){
        if(-(v-vertical) < VERTICAL_MIN){
            motorBackward(1, VERTICAL_MIN);
        }else if((v-vertical) < 100){
            motorBackward(1, -(v-vertical));
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
        Serial.println((String)angles[1]);
        Serial.println((String)(toV-200));
        while(angles[1] >= (toV +100)){
          //Serial.println("Went in");
          Serial.println((String)angles[1]);
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
    if(pos > 214 && pos < 285) setItem(0);
    if(pos > 416 && pos < 479) setItem(1);
    if(pos > 609 && pos < 679) setItem(2);
    if(pos > 816 && pos < 884) setItem(3);
}

void setItem(int i){
    if (items[i] == 0){
         items[i] = 1;
         stats[i]++;
         Serial.println(i);
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

void test(){
    if(counter < 20){
      orders.concat("n");
      counter++;
    }
    else{
      job = "0";
      counter = 0;
      orders.concat("e");
    }

}

void endTest(){
    for(int i = 0; i < 4; i++) Serial.println( (String) stats[i] + ", ");
    for(int i = 0; i < 4; i++) stats[i] = 0;
    job = "0";
}

void extendArm(){
    motorBackward(GRAB_MOTOR, 80);
    delay(750);
    motorAllStop();
}
void retractArm(){
    motorForward(GRAB_MOTOR, 80);
    delay(750);
    motorAllStop();
    //armOut = 1;
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
