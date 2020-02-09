/*#include "SDPArduino.h"
#include <Wire.h>
int state = 0;

void setup(){
  SDPsetup();
  while(!Serial){;} // Wait until serial is established
  Serial.println("Serial Connected");
}

void loop(){
  /*while (Serial.available() == 0);
  int inp = Serial.parseInt();

  if(state==0&&inp==1){
      digitalWrite(13, HIGH);
      state = 1;
  } else if(state==1&&inp==0){
      digitalWrite(13, LOW);
      state = 0;
  }
  int i = readDigitalSensorData(9);
  Serial.println(i);
  delay(30);
}*/

char GP2D12;
char a,b;
void setup()
{
 Serial.begin(9600); //
}
void loop()
{
 int val;
 GP2D12=read_gp2d12_range(0);
 a=GP2D12/10;
 b=GP2D12%10;
 val=a*10+b;
 if(val>0&&val<80)
 {
   Serial.print(a,DEC);//
   Serial.print(b,DEC);//
   Serial.println("cm");//
 }
 else Serial.println("over");//
 delay(50);
}
float read_gp2d12_range(byte pin)
{
 int tmp;
 tmp = analogRead(9);
 if (tmp < 3)return -1;
 return (6787.0 /((float)tmp - 3.0)) - 4.0;
}

