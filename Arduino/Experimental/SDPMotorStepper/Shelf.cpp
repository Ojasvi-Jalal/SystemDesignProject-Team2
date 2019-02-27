
/*
Right now the shelf is the following:
  |5|6|7|8|
  ---------
 0|1|2|3|4|

 State 0 is the beginning
*/

// Which means, 4 columns
// Change this if shelf changes
#define Columns 4

int* cords(int pos){
    // Returns an array of two integers
    int arr [2]= {0,0};

    // 0 is irregular, so let's fix that first.
    if(pos == 0) return arr;

    arr[0] = ((pos-1) % Columns) + 1;
    arr[1] = (pos-1) / Columns;

    return arr;
}
