#include <core_build_options.h>
#include <swRTC.h>
#include <SPI.h>
#include <Phpoc.h>

char server_name[] = "117.16.94.138";
PhpocClient client;
swRTC rtc;

// set water relays
int relay1 = 3;
int relay2 = 4;
int relay3 = 5;
int relay4 = 6;

// set all moisture sensors PIN ID
int moisture1 = A0;
int moisture2 = A1;
int moisture3 = A2;
int moisture4 = A3;

// declare moisture values
int moisture1_value = 0;
int moisture2_value = 0;
int moisture3_value = 0;
int moisture4_value = 0;

// set water pump
int pump = 2;
int limited = 100;
int pumpTime;
int ptime;
int crtime; 
String data;
boolean auto_;

unsigned long ctime2;
unsigned long ptime2;

void setup() {
    rtc.stopRTC();           //정지
    rtc.setTime(23,59,40);   //시간, 분, 초 초기화
    rtc.setDate(7,12,2019);  //일, 월, 년도 초기화 
    rtc.startRTC();          //시작
    Serial.begin(9600);      //시리얼 포트 초기화
    
     // declare relay as output
    pinMode(relay1, OUTPUT);
    pinMode(relay2, OUTPUT);
    pinMode(relay3, OUTPUT);
    pinMode(relay4, OUTPUT);
    // declare pump as output
    pinMode(pump, OUTPUT);
    // declare the ledPin as an OUTPUT:
    Serial.begin(9600);
    Phpoc.begin(PF_LOG_SPI | PF_LOG_NET);
  
    data = "";
    ptime = millis();
    ptime2 = millis();
    pumpTime = 5000;

}

void loop() {
  crtime = millis();
  
  if(crtime - ptime >= 5000){
    Serial.println("update 진입");
    while(!sensor_update());
    ptime = millis();
  }
  //auto일 경우
  if(pump_control() && set_pumptime()){
    int ii = rtc.getMonth();
    if(ii == 12 || ii== 1 || ii == 2){
      ptime2 = millis();
//      if(winter_time_check()){
//        if(limited >= moisture1_value || limited >= moisture2_value || limited >= moisture3_value || limited >= moisture4_value){
          digitalWrite(pump, HIGH);
          
          if(limited >= moisture1_value)digitalWrite(relay1, HIGH);
          if(limited >= moisture2_value)digitalWrite(relay2, HIGH);
          if(limited >= moisture3_value)digitalWrite(relay3, HIGH);
          if(limited >= moisture4_value)digitalWrite(relay4, HIGH);
          delay(pumpTime);
          digitalWrite(pump, LOW);
          digitalWrite(relay1, LOW);
          digitalWrite(relay2, LOW);
          digitalWrite(relay3, LOW);
          digitalWrite(relay4, LOW);    
//       }
//     } 
    }

   if(ii == 3 || ii == 4 || ii == 5 || ii == 9 || ii == 10 || ii == 11){
    if(spring_time_check()){
//      if(limited >= moisture1_value || limited >= moisture2_value || limited >= moisture3_value || limited >= moisture4_value){
          digitalWrite(pump, HIGH);
  
          if(limited >= moisture1_value)digitalWrite(relay1, HIGH);
          if(limited >= moisture2_value)digitalWrite(relay2, HIGH);
          if(limited >= moisture3_value)digitalWrite(relay3, HIGH);
          if(limited >= moisture4_value)digitalWrite(relay4, HIGH);
          delay(pumpTime);
          digitalWrite(pump, LOW);
          digitalWrite(relay1, LOW);
          digitalWrite(relay2, LOW);
          digitalWrite(relay3, LOW);
          digitalWrite(relay4, LOW); 
//     }
    } 
   }

  if(ii == 6 || ii == 7 || ii == 8){
      
      if(summer_time_check()){
//        if(limited >= moisture1_value || limited >= moisture2_value || limited >= moisture3_value || limited >= moisture4_value){
            digitalWrite(pump, HIGH);
    
            if(limited >= moisture1_value)digitalWrite(relay1, HIGH);
            if(limited >= moisture2_value)digitalWrite(relay2, HIGH);
            if(limited >= moisture3_value)digitalWrite(relay3, HIGH);
            if(limited >= moisture4_value)digitalWrite(relay4, HIGH);
            delay(pumpTime);
            digitalWrite(pump, LOW);
            digitalWrite(relay1, LOW);
            digitalWrite(relay2, LOW);
            digitalWrite(relay3, LOW);
            digitalWrite(relay4, LOW); 
//       }
    }
  }
    
  }

  if(!auto_ && set_pumptime2()){
    if(limited >= moisture1_value){
      
      digitalWrite(pump, HIGH);
      Serial.println("펌프하이");
//      if(limited >= moisture1_value)digitalWrite(relay1, HIGH);
//      if(limited >= moisture2_value)digitalWrite(relay2, HIGH);
//      if(limited >= moisture3_value)digitalWrite(relay3, HIGH);
//      if(limited >= moisture4_value)digitalWrite(relay4, HIGH);
      
      delay(pumpTime);
      
      digitalWrite(pump, LOW);
      digitalWrite(relay1, LOW);
      digitalWrite(relay2, LOW);
      digitalWrite(relay3, LOW);
      digitalWrite(relay4, LOW);     
    }

    else{
      Serial.println("셋메뉴얼 직전");
      set_manual();
    }
  }
}

boolean sensor_update(){   
  moisture1_value = analogRead(moisture1);
//  moisture2_value = analogRead(moisture2);
//  moisture3_value = analogRead(moisture3);
//  moisture4_value = analogRead(moisture4);

  data = "sensor1=" + (String)moisture1_value + "&&sensor2=" + (String)moisture2_value + "&&sensor3=" + (String)moisture3_value +"&&sensor4=" + (String)moisture4_value;

  if(client.connect(server_name, 80)){
    client.println("POST /UpdateSensors.php HTTP/1.1");
    client.println("Host: 117.16.94.138");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.print("Content-Length: ");
    client.println(data.length());
    client.println();
    client.print(data);
  }
  else{
    Serial.println("update failed");
    return false;
  }

  if(client.connected()){
    client.stop();
    return true;
  }
}


// 3,4,5,9,10,11 일 경우
boolean spring_time_check(){
  Serial.println("봄 타임체크 진입");
  ctime2 = millis();
  boolean check = false;
   
  if((ctime2-ptime2)>=1814400000) {
    check = true;
  }
  
  else {
    check = false;
  }

  return check;
  
}

// 6, 7, 8 일 경우
boolean summer_time_check(){
  Serial.println("여름 타임체크 진입");
  ctime2 = millis();
  
  if((ctime2-ptime2)>=1209600000) return true;
  

   else return false;
  
}

// 12, 1, 2 일 경우
boolean winter_time_check(){
  Serial.println("겨울 타임체크 진입");
  ctime2 = millis();
  
  if((ctime2-ptime2)>=2592000000){
    return true;
  }
  else{
    return false;
  }
}

boolean pump_control(){
    Serial.println("펌프컨트롤");
    if(client.connect(server_name, 80)){
      Serial.println("Connected to server");
      client.println("GET /GetStatus.php HTTP/1.0");
      client.println("Host: 117.16.94.138");
      client.println("Connection: close");
      client.println();
    }
    else{    
      Serial.println("connection failed");
      return false;
    }
         
    while(true){
      if(client.available()) {
        
        //client.read()의 반환형은 character형
        char c = client.read();
        if(c=='?'){
          c = client.read();
          Serial.println("auto 값 : " + c);
          auto_ = ((c-48) == 1); //auto
        
          Serial.println("auto :" + String(auto_));
          
          client.stop();
          return auto_;
        }
    }

    if(!client.connected()){
      Serial.println("disconnected");
      client.stop();
      return false;
    }
    
  }
}

void update_value(){
      
    if(client.connect(server_name, 80)){
      Serial.println("Connected to server");
      client.println("GET /GetStatus.php HTTP/1.0");
      client.println("Host: 117.16.94.138");
      client.println("Connection: close");
      client.println();
    }
    else{
      Serial.println("connection failed");
      return false;
    }
         
    while(true){
      if(client.available()) {
        
        
    }

    if(!client.connected()){
      Serial.println("disconnected");
      client.stop();
      return false;
    }
    
  }
  
}

// auto일 경우 pumptime 값 설정, limited는 0이 되어야 하기 때문에 값을 받아오지 않음(화분 표면이 말라있어야 하기 때문).
boolean set_pumptime(){
    if(client.connect(server_name, 80)){
      Serial.println("Connected to server");
      client.println("GET /GetPumptimeValue.php HTTP/1.0");
      client.println("Host: 117.16.94.138");
      client.println("Connection: close");
      client.println();
    }
    else{
      Serial.println("connection failed");
      return false;
    }

    while(true){
      if(client.available()) {
        //client.read()의 반환형은 character형
        char c = client.read();
        if(c=='?'){
          
          String pt = "";
          
          for(int i = 0; i < 4; i++) pt.concat(client.read() - 48);

          pumpTime = pt.toInt();
          limited = 0;
          Serial.println("pumpTime :" + String(pumpTime));
          
          client.stop();
          return true;
        }
      }

      if(!client.connected()){
        Serial.println("disconnected");
        client.stop();
        return false;
      }
    
    }
    
}

boolean set_pumptime2(){
    Serial.println("셋펌프타임2진입");
    if(client.connect(server_name, 80)){
      Serial.println("Connected to server");
      client.println("GET /GetManual.php HTTP/1.0");
      client.println("Host: 117.16.94.138");
      client.println("Connection: close");
      client.println();
    }
    else{
      Serial.println("connection failed");
      return false;
    }

    while(true){
      if(client.available()) {
        //client.read()의 반환형은 character형
        char c = client.read();
        if(c=='?'){
          
          String pt = "";
          
          for(int i = 0; i < 4; i++) pt.concat(client.read() - 48);

          pumpTime = pt.toInt();
          limited = 0;
          Serial.println("pumpTime :" + String(pumpTime));
          
          client.stop();
          return true;
        }
      }

      if(!client.connected()){
        Serial.println("disconnected");
        client.stop();
        return false;
      }
    
    }
    

}

void set_manual(){
   Serial.println("셋메뉴얼 진입");
  
    if(client.connect(server_name, 80)){
      Serial.println("Connected to server");
      client.println("GET /SetManual.php HTTP/1.0");
      client.println("Host: 117.16.94.138");
      client.println("Connection: close");
      client.println();
    }
    else{
      Serial.println("connection failed");
      return;
    }

    while(true){
      if(client.available()) {
        //client.read()의 반환형은 character형
        char c = client.read();
        if(c=='?'){
          if((client.read() - 48) == 1){
            client.stop();
            return;
          }
        }
      }

      if(!client.connected()){
        Serial.println("disconnected");
        client.stop();
        return ;
      }
    
    }
  
}
