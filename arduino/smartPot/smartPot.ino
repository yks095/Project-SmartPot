#include <SPI.h>
#include <Phpoc.h>

char server_name[] = "117.16.94.138";
PhpocClient client;

// set all moisture sensors PIN ID
int moisture1 = A0;

// declare moisture values
int moisture1_value = 0;

// set water pump
int pump = 2;
int limited = 100;
int pumpTime;
int ptime;
int crtime;
String data;
String data2;
boolean auto_;
String id;
unsigned long ctime2;
unsigned long ptime2;

void setup() {
    // declare pump as output
    pinMode(pump, OUTPUT);
    // declare the ledPin as an OUTPUT:
    Serial.begin(9600);
    Phpoc.begin(PF_LOG_SPI | PF_LOG_NET);
    data = "";
    ptime = millis();
    ptime2 = millis();
    pumpTime = 5000;
    id = "aaa";
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
      ptime2 = millis();
      digitalWrite(pump, HIGH);
      delay(pumpTime);
  }
  
  if(!auto_ && set_pumptime2()){
    if(limited >= moisture1_value){
      digitalWrite(pump, HIGH);
      Serial.println("펌프하이");
      delay(pumpTime);
      digitalWrite(pump, LOW);
    }
    else{
      Serial.println("셋메뉴얼 직전");
      set_manual();
    }
  }

}

boolean sensor_update(){
  moisture1_value = analogRead(moisture1);

  if(client.connect(server_name, 80)){
    data = "id=" + id + "&&sensor=" + (String)moisture1_value;
    Serial.println("센서업데이트 : " + data);
    client.println("POST /arduino/UpdateSensors.php HTTP/1.1");
    client.println("Host: 117.16.94.138");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.print("Content-Length: ");
    client.println(data.length());
    client.println();
    client.print(data);
    delay(1000);
  }
  else{
    Serial.println("update failed");
    return false;
  }

  if(client.connected()){
    Serial.println("센서업데이트 성공");
    client.stop();
    return true;
  }
} // end sensor_update


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

} // end spring_time_check

// 6, 7, 8 일 경우
boolean summer_time_check(){
  Serial.println("여름 타임체크 진입");
  ctime2 = millis();

  if((ctime2-ptime2)>=1209600000) return true;
  else return false;

} // end summer_time_check

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
} // end winter_time_check

boolean pump_control(){
  Serial.println("펌프컨트롤");
  if(client.connect(server_name, 80)){
    data = "id=" + id;
    client.println("POST /arduino/GetStatus.php HTTP/1.1");
    client.println("Host: 117.16.94.138");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.print("Content-Length: ");
    client.println(data.length());
    client.println();
    client.print(data);
    delay(1000);
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
} // end pump_control

// auto일 경우 pumptime 값 설정, limited는 0이 되어야 하기 때문에 값을 받아오지 않음(화분 표면이 말라있어야 하기 때문).
boolean set_pumptime(){
    if(client.connect(server_name, 80)){
      data = "id=" + id;
      client.println("POST /arduino/GetPumptimeValue.php HTTP/1.1");
      client.println("Host: 117.16.94.138");
      client.println("Content-Type: application/x-www-form-urlencoded");
      client.print("Content-Length: ");
      client.println(data.length());
      client.println();
      client.print(data);
      delay(1000);
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

} // end set_pumptime

//수동일 때 pumptime설정
boolean set_pumptime2(){
    Serial.println("auto가 0일 때 펌프타임");
    if(client.connect(server_name, 80)){
      data = "id=" + id;
      client.println("POST /arduino/GetManual.php HTTP/1.1");
      client.println("Host: 117.16.94.138");
      client.println("Content-Type: application/x-www-form-urlencoded");
      client.print("Content-Length: ");
      client.println(data.length());
      client.println();
      client.print(data);
      delay(1000);
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
          Serial.println("pt : " + pt);

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


} // end set_pumptime2

void set_manual(){
   Serial.println("셋메뉴얼 진입");

    if(client.connect(server_name, 80)){
      data = "id=" + id;
      client.println("POST /arduino/SetManual.php HTTP/1.1");
      client.println("Host: 117.16.94.138");
      client.println("Content-Type: application/x-www-form-urlencoded");
      client.print("Content-Length: ");
      client.println(data.length());
      client.println();
      client.print(data);
      delay(1000);
    }
    else{
      Serial.println("connection failed");
    }

    if(client.connected()){
      Serial.println("셋 메뉴얼 성공");
      client.stop();
    }

} // end set_manual
