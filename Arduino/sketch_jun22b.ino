#include <Servo.h>
#include <SoftwareSerial.h>

int count=0;

/*
 * DC motor
 */
//right wheel
int motor1Pin1 = 2; // pin 2 on L293D IC
int motor1Pin2 = 3; // pin 7 on L293D IC
int enable1Pin = 4; // pin 1 on L293D IC
//output black wire -> 28 / red wire -> 25

//left wheel
int motor2Pin1 = 5; // pin 15 on L293D IC 
int motor2Pin2 = 6; // pin 10 on L293D IC
int enable2Pin = 7; // pin 9 on L293D IC
//output black wire -> 25 / red wire -> 28

/*
 * Sonar sensor
 */
int sonar_trig_pin = 10;
int sonar_echo_pin = 11;
unsigned long u_start;
unsigned long u_stop;
float cm;

/*
 *  Servo motor
 */
int servo_pin = 12;
Servo my_servo;


//flag for bluetooth
SoftwareSerial btSerial(8, 9);
int btVal;
int input;


int mod=0;// mode
int state=0;


//minimum speed is 130
int speed = 130;

/*
 * auto driving mode
 */
unsigned long safe_dist=300;    //distance from car to obstacle 1 = 1mm, 10 = 1cm
unsigned long safe_side_dist=200;
//unsigned long safe_time=1000;   //turning time 1 = 1millisecond, 1000 = 1sec
unsigned long sonar_time=500;   //turning servo time
unsigned long turning_time=1000; //turning car time
//unsigned long going_time=2000;  //going time

int going_time;
int left_danger;                //  check obstacle and
int right_danger;               //  setting flag 
int front_danger;               //  when dist < safe_dist

int left_dist;                  // dist from car to obstacle by
int right_dist;                 // left, right, front sied
int front_dist;                 //
int shortest_side_flag;
int shortest_side_dist;


int isComplete=0;
int left_count=0;
int front_count=0;
int right_count=0;

void setup() {
  // put your setup code here, to run once:

 /*
  *  DC motors
  */
 //right wheel is motor1
 pinMode(motor1Pin1, OUTPUT);
 pinMode(motor1Pin2, OUTPUT);
 pinMode(enable1Pin, OUTPUT);

 //left wheel is motor2
 pinMode(motor2Pin1, OUTPUT);
 pinMode(motor2Pin2, OUTPUT);
 pinMode(enable2Pin, OUTPUT);
 
 // sets enablePin high so that motor can turn on:
 digitalWrite(enable1Pin, HIGH);
 digitalWrite(enable2Pin, HIGH);


 /*
  *  sonar sensor
  */
  pinMode(sonar_trig_pin, OUTPUT);
  pinMode(sonar_echo_pin, INPUT);


  /*
   * servo motor
   */
   my_servo.attach(servo_pin);
 
 
   // initialize serial communication at 9600 bits per second:
   Serial.begin(9600);
   btSerial.begin(9600);
}


void loop() {
   
  
  
  if(Serial.available()>0){
    input = Serial.read();
  }
  
  if(btSerial.available()>0){
    btVal = btSerial.read();
    Serial.print("btval : ");
    Serial.println(btVal);
  }
  else{
    btVal=-1;
  }
  
  //Serial.println(btVal);
 /*
 Serial.print(mod);
    Serial.print(" / ");
    Serial.println(state);
*/
  
/*     
 *    state
 *    1 : forward 
 *    2 : backward
 *    3 : turn left
 *    4 : trun right
 *    0 : stop
 */

/*
 *  fpga data (=btVal)
 *  1 : forward
 *  2 : backward
 *  3 : turn left
 *  4 : turn right
 *  0 : stop 
 */
 
 /*
  *    <MOD>
  *    a : stop mode
  *    b : driving mode
  *    c : auto driving mode
  */

  /*
   *  "btVal" data from bluetooth 
   */
   if(btVal==0){    //stop state
    state=0;    
   }
   else if(btVal==1){ //forward state
    state=1;
   }
   else if(btVal==2){ //backward state
    state=2;
   }
   else if(btVal==3){ //turn left state
    state=3;
   }
   else if(btVal==4){ //turn right state
    state=4;
   }
   else if(btVal=='a'){
    stop();
    mod=0;
   }
   else if(btVal=='b'){
    stop();
    mod=1;
   }
   else if(btVal=='c'){
    stop();
    mod=2;
   }

   
   /*
    * "input" data from serial port
    */    
    if(input=='0'){
      state=0;      
    }
    else if(input=='1'){
      state=1;      
    }
    else if(input=='2'){  
      state=2;      
    }
    else if(input=='3'){  
      state=3;
         }
    else if(input=='4'){  
      state=4;
    }
    else if(input=='a'){
      stop();
      mod=0;
    }
    else if(input=='b'){
      stop();
      mod=1;      
    }
    else if(input=='c'){
      stop();
      mod=2;      
    }
    
   
    if(mod==0){                             // *** stop mode ***
      
      if(isComplete==1){    //end mode 2
        isComplete=2;     
      }
      
      stop();
    }    
    else if(mod==1){                        // *** drive mode *** 
      
      if(isComplete==1){    //end mode 2
        isComplete=2;        
      }
      
      if(state==0){
        stop();
      }
      else if(state==1){
        go_straight(); 
      }
      else if(state==2){
        back_straight();
      }
      else if(state==3){
        turn_left();
      }
      else if(state==4){
        turn_right();
      }      
    }
    else if(mod==2){                      // *** auto drive mode ***
      
      if(isComplete==0){      //start mode 2
        isComplete=1;
        right_count=0;
        front_count=0;
        left_count=0;        
      }
      
      state=0;
      auto_drive();
    }

    if(isComplete==2){
      isComplete=0;
      int num = 0;
      num = num|(right_count<<8);
      num = num|(front_count<<4);
      num = num|(left_count);
      transmit_data(num,4);
      Serial.print("count : ");
      Serial.print(right_count);
      Serial.print(" / ");
      Serial.print(front_count);
      Serial.print(" / ");
      Serial.println(left_count);
      
    }

    
    delay(200);  
  
}


// total delay = 3*sonar_time + 2 * 1000 + 2*turning_time + straight_time = 8sec
void auto_drive(){
  int angle;                //angle for servo motor;
  unsigned long dist;       //distance from car to 
  unsigned long start_time;
  left_danger=0;
  right_danger=0;
  front_danger=0;

  shortest_side_dist = 3000;
  shortest_side_flag = -1;

/*
 * check dist using sonar sensor
 */
 
 /*
  *  angle
  *  0   : sonar sensor watch the right side
  *  90  : sonar sensor watch the front side
  *  180 : sonar sensor watch the left side
  */

  //check right side
  angle=0;  
  my_servo.write(angle);  
  delay(sonar_time);

  dist = cal_dist();
  if(dist<safe_side_dist) right_danger=1;   //check safe_side_dist
  right_dist=dist;
  if(dist <= shortest_side_dist){
    shortest_side_dist = dist;
    shortest_side_flag = 0;
  }
  
  
  Serial.print("rd : ");
  Serial.print(dist);

  transmit_data(right_dist, 1);

  angle=45;  
  my_servo.write(angle);  
  delay(sonar_time);
  dist = cal_dist();
 if(dist <= shortest_side_dist){
    shortest_side_dist = dist;
    shortest_side_flag = 0;
  }

  
  if(dist<safe_side_dist) right_danger=1;   //check safe_side_dist
  Serial.print(" / ");
  Serial.print(dist);

  //check front
  angle=90;
  my_servo.write(angle);  
  delay(sonar_time);
  
  dist = cal_dist();
  //if(dist>1000) dist=1000;
  if(dist<safe_dist) front_danger=1;
  front_dist=dist;

  transmit_data(front_dist, 2);
  
  Serial.print(" / fd : ");
  Serial.print(dist);

  //check left side
  angle=135;
  my_servo.write(angle);
  delay(sonar_time);
  
  dist = cal_dist();
  if(dist<safe_side_dist) left_danger=1;
  if(dist <= shortest_side_dist){
    shortest_side_dist = dist;
    shortest_side_flag = 1;
  }

  
  Serial.print(" / ld : ");
  Serial.print(dist);

  angle=180;
  my_servo.write(angle); 
  delay(sonar_time);
  
  dist = cal_dist();
 // if(dist>1000) dist=1000;
  if(dist<safe_side_dist) left_danger=1;
  left_dist=dist;
  
  if(dist <= shortest_side_dist){
    shortest_side_dist = dist;
    shortest_side_flag = 1;
  }
  transmit_data(left_dist, 3);
  

  Serial.print(" / ");
  Serial.println(dist);
  angle=90;
  my_servo.write(angle);

  //push distance from arduino to bluetooth
  //transmit_dist(left_dist, front_dist, right_dist);
  
  delay(500);
  /*
   * move
   * if something exists forward, then turn left or turn right
   * and keep going
   * 
   */
  if(front_danger==0 && left_danger==0 && right_danger==0){    //move forward
    //Serial.println("go!!!");
    
    if(front_dist>1000){
      going_time=1500;
    }
    else if(front_dist>300){
      going_time=1000;
    }
    else{
      going_time=500;
    }
    front_count++;
    go_straight();      
    start_time = millis();
    while( (millis() - start_time) < going_time ){ //go straight during going_time
      if( (millis()%300) ==0 ){
        dist=cal_dist();
        //if(dist>1000) dist=1000;
      //  Serial.print("dist : ");
      //  Serial.println(dist);
      
        transmit_data(dist,2);    
      }  
    } 
    stop();      
  }
  else{
    if(shortest_side_flag==0){
      left_count++;
      turn_left();      
      start_time = millis();
      while( (millis() - start_time) <= turning_time ){ //turn left during turning_time
      //Serial.println("lefting-");
      }
      stop();
    }
    else if(shortest_side_flag==1){
       right_count++;
       turn_right();      
       start_time = millis();
       while( (millis() - start_time) <= turning_time ){ //turn right during turning_time
         //Serial.println("righting-");
       }
       stop();
    }
  }


  
  delay(500);
}

void transmit_data(int num, int flag){
  
  unsigned char high = (((num>>7)&0x7f) );
  unsigned char low  = (num&0x7f);

 // Serial.print("high : ");
//  Serial.print(high);
//  Serial.print(" / ");
  if(flag==1){
    high = high|0x10;
  }
  else if(flag==2){
    high= high|0x20;    
  }
  else if(flag==3){
    high = high|0x30;    
  }
  else if(flag==4){
    high = high|0x00;
  }
 // Serial.println(high);
  btSerial.write(high);  
  btSerial.write(low);  
}

void stop(){
  digitalWrite(motor1Pin1, LOW); 
  digitalWrite(motor1Pin2, LOW); 
  digitalWrite(motor2Pin1, LOW); 
  digitalWrite(motor2Pin2, LOW);
}

void go_straight(){
  digitalWrite(motor1Pin1, LOW);
  digitalWrite(motor1Pin2, HIGH);
  digitalWrite(motor2Pin1, LOW);
  digitalWrite(motor2Pin2, HIGH);
}

void turn_left(){
  digitalWrite(motor1Pin1, LOW);
  digitalWrite(motor1Pin2, HIGH);  
  digitalWrite(motor2Pin1, LOW);
  digitalWrite(motor2Pin2, LOW);
}

void turn_right(){
  digitalWrite(motor1Pin1, LOW);
  digitalWrite(motor1Pin2, LOW);  
  digitalWrite(motor2Pin1, LOW);
  digitalWrite(motor2Pin2, HIGH);
}

void back_straight(){
  digitalWrite(motor1Pin1, HIGH);
  digitalWrite(motor1Pin2, LOW);
  digitalWrite(motor2Pin1, HIGH);
  digitalWrite(motor2Pin2, LOW);
}


/*
 * sonar sensor
 */

void echo(void)
{
  digitalWrite(sonar_trig_pin, LOW);
  delayMicroseconds(2);
  digitalWrite(sonar_trig_pin, HIGH );
  delayMicroseconds(10);
  digitalWrite( sonar_trig_pin, LOW );

  cm = pulseIn(sonar_echo_pin, HIGH)/58.0;
  cm = (int(cm*100.0))/100.0;
  
}

unsigned long cal_dist(void){
  
  echo();
  
  return cm*10;
}
