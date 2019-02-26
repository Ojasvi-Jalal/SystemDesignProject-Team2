#include "SDPArduino.h"
#include <Wire.h>
#include <stdio.h>

using namespace std;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_NUM 6

class Motor{
public:
    Motor(int port);

private:
    int port;
}
