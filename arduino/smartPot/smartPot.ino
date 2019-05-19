
#include <SPI.h>
#include <Phpoc.h>

char server_name[] = "117.16.94.138";
PhpocClient client;

// set all moisture sensors PIN ID
int moisture1 = A0;
int temperature = A1;
int waterLevel = 0;
// declare moisture values
int moisture1_value = 0;
double temperature_value = 0.0;
// set water pump
int pump = 2;

// set water relays
int relay1 = 3;
int relay2 = 4;
int relay3 = 5;
int relay4 = 6;

int limited = 100;
int pumpTime;
int ptime;
int crtime;
String data;
boolean auto_;
String id;
unsigned long ctime2;
unsigned long ptime2;
int monthCheck;

void setup(){    
    Serial.begin(9600);
    // declare pump as output
    pinMode(pump, OUTPUT);
    // declare relay as output
    pinMode(relay1, OUTPUT);
    pinMode(relay2, OUTPUT);
    pinMode(relay3, OUTPUT);
    pinMode(relay4, OUTPUT);
    Phpoc.begin(PF_LOG_SPI | PF_LOG_NET);
    data = "";
    ptime = millis();
    ptime2 = millis();
    pumpTime = 0;
    id = "aaa";
}


void loop(){
  
  crtime = millis();
  
  if(crtime - ptime >= 5000){
    Serial.println("update 진입");
    while(!sensor_update());

    ptime = millis();
  }
  
  //auto애트리뷰트의 값이 1일 경우
  if(pump_control() && set_pumptime()){
    //봄, 가을일경우
    if((3 <= monthCheck && 5 <= monthCheck) || (9 <= monthCheck && monthCheck <= 11)){
      if(spring_time_check()){
        Serial.println("*****SPRING OR AUTUMN*****");
        
        ptime2 = millis();
        
        digitalWrite(relay1, HIGH);
        digitalWrite(relay2, HIGH);
        digitalWrite(relay3, HIGH);
        digitalWrite(relay4, HIGH);
        digitalWrite(pump, HIGH);
        delay(pumpTime);
        digitalWrite(pump, LOW);
        digitalWrite(relay1, LOW);
        digitalWrite(relay2, LOW);
        digitalWrite(relay3, LOW);
        digitalWrite(relay4, LOW);
      }
    }
    //겨울일경우
    if(monthCheck == 12 || monthCheck == 1 || monthCheck == 2){
      if(winter_time_check()){
        Serial.println("*****WINTER*****");
        ptime2 = millis();        
        digitalWrite(relay1, HIGH);
        digitalWrite(relay2, HIGH);
        digitalWrite(relay3, HIGH);
        digitalWrite(relay4, HIGH);
        digitalWrite(pump, HIGH);
        delay(pumpTime);
        digitalWrite(pump, LOW);
        digitalWrite(relay1, LOW);
        digitalWrite(relay2, LOW);
        digitalWrite(relay3, LOW);
        digitalWrite(relay4, LOW);
      }      
    }
    //여름일경우
    if(monthCheck == 6 || monthCheck == 7 || monthCheck == 8){
      if(summer_time_check()){
        Serial.println("*****SUMMER*****");
        ptime2 = millis();        
        digitalWrite(relay1, HIGH);
        digitalWrite(relay2, HIGH);
        digitalWrite(relay3, HIGH);
        digitalWrite(relay4, HIGH);
        digitalWrite(pump, HIGH);
        delay(pumpTime);
        digitalWrite(pump, LOW);
        digitalWrite(relay1, LOW);
        digitalWrite(relay2, LOW);
        digitalWrite(relay3, LOW);
        digitalWrite(relay4, LOW);
      }
    }
  }
  //auto애트리뷰트의 값이 0일 경우
  if(!auto_ && set_pumptime2()){
    if(limited >= moisture1_value){
            
      digitalWrite(relay1, HIGH);
      digitalWrite(relay2, HIGH);
      digitalWrite(relay3, HIGH);
      digitalWrite(relay4, HIGH);
      digitalWrite(pump, HIGH);
      Serial.println("PUMP ON");
      delay(pumpTime);
      digitalWrite(pump, LOW);
      digitalWrite(relay1, LOW);
      digitalWrite(relay2, LOW);
      digitalWrite(relay3, LOW);
      digitalWrite(relay4, LOW);
      Serial.println("PUMP OFF");
    }
    else{      
      set_manual();
    }
  }

  delay(100);
}

boolean sensor_update(){
  analogRead(moisture1);
  moisture1_value = analogRead(moisture1);

  temperature_value = analogRead(temperature);

  temperature_value = temperature_value * 0.48828125;
  delay(50);
  int waterLevelVal = digitalRead(waterLevel);
  
  if(client.connect(server_name, 80)){
    data = "id="+id+"&&moisture_sensor="+String(moisture1_value)+"&&temperature_sensor="+String(temperature_value) + "&&cds_sensor=" + String(waterLevelVal);
    
    Serial.println(data); //SerialMonitor에 출력이 안됨
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

   
   while(true){
     if(client.available()) {
      
       
      //client.read()의 반환형은 character형
      char c = client.read();
      if(c=='?'){
        Serial.println("진입");
        String ti = "";
        for(int i = 0; i < 19; i++) {
          c = client.read();
          if(80 <= c && c <= 89) ti.concat((c - 48));
          else ti.concat(c);
        }
                  
        monthCheck = ti.substring(5,7).toInt();
        Serial.println("monthCheck :" + String(monthCheck));
        client.stop();

        return true; 
       }
     }

     if(!client.connected()){
      Serial.println("disconnected");
      client.stop();
//      t = false;   
     }
   }
} // end sensor_update


//monthCheck의 값이 3,4,5,9,10,11 일 경우
boolean spring_time_check(){
  Serial.println("spring_time_check 진입");
  ctime2 = millis();
  boolean check = false;
  //1814400000
  if((ctime2-ptime2)>=5000) {
    check = true;
  }
  else {
    check = false;
  }

  return check;

} // end spring_time_check

//monthCheck의 값이 6, 7, 8 일 경우
boolean summer_time_check(){
  Serial.println("여름 타임체크 진입");
  ctime2 = millis();

  if((ctime2-ptime2)>=1209600000) return true;
  else return false;

} // end summer_time_check

//monthCheck의 값이 12, 1, 2 일 경우
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
  
  if(client.connect(server_name, 80)){
    data = "id=" + id;
    client.println("POST /arduino/GetStatus.php HTTP/1.1");
    client.println("Host: 117.16.94.138");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.print("Content-Length: ");
    client.println(data.length());
    client.println();
    client.print(data);
    delay(500);
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
      delay(500);
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
    
    if(client.connect(server_name, 80)){
      data = "id=" + id;
      client.println("POST /arduino/GetManual.php HTTP/1.1");
      client.println("Host: 117.16.94.138");
      client.println("Content-Type: application/x-www-form-urlencoded");
      client.print("Content-Length: ");
      client.println(data.length());
      client.println();
      client.print(data);
      delay(500);
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


} // end set_pumptime2

void set_manual(){
    if(client.connect(server_name, 80)){
      data = "id=" + id;
      client.println("POST /arduino/SetManual.php HTTP/1.1");
      client.println("Host: 117.16.94.138");
      client.println("Content-Type: application/x-www-form-urlencoded");
      client.print("Content-Length: ");
      client.println(data.length());
      client.println();
      client.print(data);
      delay(500);
    }
    else{
      Serial.println("connection failed");
    }

    if(client.connected()){
      Serial.println("update manual value to zero");
      client.stop();
    }

} // end set_manual
