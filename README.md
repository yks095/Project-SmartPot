# Project-SmartPot

#### 2019-05-10
- 아두이노
  - `smartpot`테이블의 `auto`애트리뷰트의 값 : 수동/자동
  - `auto`의 값을 확인한 후 `1`일 경우 자동, `0`일 경우 수동
    - 자동
      1. `smartpot`테이블의 `flower`애트리뷰트의 값과 `flower`테이블의 `idx`값을 비교
      2. `idx`값에 따라 `flower`테이블에 있는 `pumptime`값을 읽어오는 쿼리 실행
      3. 아두이노의 `pumptime`변수에 값이 들어감

    - 수동
      1. 앱을 통해 `pumptime`값을 읽어와 `smartpot`테이블에 저장
      2. 앱의 **물주기 버튼** 으로 `manual`애트리뷰트를 업데이트
      3. `manual`애트리뷰트의 값을 확인한 후 `1`일 경우 `pumptime`애트리뷰트를 읽어옴
      4. 아두이노의 `pumptime` 변수에 값이 들어감
      5. 습도 센서의 값인 `moisture_value`변수의 값이 `limited`변수의 값을 넘을 때 까지 pump를 작동  

  - 계절에 따라 식물에 물을 줘야 하는 시기가 달라짐
    - `swRTC`라이브러리를 통해 현재 날짜를 set
      - 겨울인 `12`, `1`, `2`월 일 경우 한 달에 한 번씩 pump를 작동
      - 봄, 가을인 `3`, `4`, `5`, `9`, `10`, `11`월 일 경우 3주에 한번씩 pump를 작동
      - 여름인 `6`, `7`, `8`월 일 경우 2주에 한 번씩 pump를 작동


#### 2019-05-13
  - 안드로이드
    - 회원가입
      - 두 개의 중복체크를 하지않을 경우 회원가입 불가능
      - `User` 테이블에 동일한 `userID` 값이 없을 경우 중복체크 성공
      - `userID` 중복 체크를 성공했을 경우 아이디 `EditText` 박스 비활성화
      - `smartpot` 테이블에 동일한 `potCode` 값이 없을 경우 중복체크 성공
    - 날씨
    - 화분 등록
      - `flower` 테이블에 등록된 꽃 정보를 `spinner`를 이용하여 선택 후 등록할 시 현재 로그인 되어 있는 `userID`에 `flower` 값이 들어감
    - 회원 정보
      - `userID` 값을 이용하여 `메일`, `화분명` 값을 미리 받아와 `TextView`에 띄울 예정
      - 회원정보 수정 화면에서 `이메일`과 `화분명`을 수정할 수 있도록 할 예정
      - 비밀번호 수정 화면에서 `비밀번호`를 수정할 수 있도록 할 예정

#### 2019-05-14
  - 안드로이드
    - 회원 정보
      - `userID` 값을 이용하여 `메일`, `화분명` 값을 미리 받아와 `TextView`에 띄움

#### 2019-05-16
  - 안드로이드
    - 회원 정보 수정
      - 회원정보 수정 화면에서 `이메일`과 `화분명`을 수정할 수 있도록 함
      - 비밀번호 수정 화면에서 `비밀번호`를 수정할 수 있도록 함
    - 로그인 후 화면
      - `potCode`가 등록된 회원일 시 메인 화면으로 이동
      - `potCode`가 등록되지 않은 회원일 시 화분 등록 화면으로 이동
    - 화분 등록
      - 화분 등록시 `startday` 가 현재 날짜로 DB에 저장

  - 아두이노
    - 안드로이드의 `php`파일과의 구분을 위해 `htdocs`의 `GetManual.php`, `GetPumptimeValue.php`, `GetStatus.php`, `SetIdCheck.php`, `SetManual.php`, `UpdateSensors.php`를 arduino폴더 안으로 이동

    - `android`에서 `potCode`에 대한 검증을 하기 때문에, `idCheck`애트리뷰트는 필요 없다고 생각하여, 애트리뷰트 제거

    - `arduino`에서 php로 값을 전달할 때, 값이 전달되지 않는 `issue`발생
      - `서버 log`의 `access`에는 `200`이 찍힘
      - `error`에는 `PHP Notice:  Undefined index:`라고 찍힘
      - `PHP Notice:  Undefined index:`는 php에서 초기화되지 않은 변수를 사용하여 생긴 에러라고 search됨, 하지만 php에는 그런 변수가 없었음
      - 해결 : `connect` 후 `POST`방식으로 `php`에 값을 전달한 후, 1초 동안 `delay`를 주었더니 해결됨

#### 2019-05-18
  - 아두이노
    - [온도센서](https://jinkyu.tistory.com/103), [물 수위 센서](https://wiki.dfrobot.com/Non-contact_Liquid_Level_Switch_SKU_FIT0212) 설계
    - 4채널 릴레이를 하나의 화분에서 사용하기로 결정
    - 두 개의 보드가 동시에 센서를 업데이트 하는 것 확인
    - console창에 phpoc request가 뜨며 connect되지 않는 에러 발생
      - phpoc 라이브러리를 열어봤지만 이해가 되지않음
      - 같이 사용한 swRTC 라이브러리도 열어봤지만 역시 이해가 되지않음
      - 메모리 부족으로 안정성에 문제가 생길 수 있다는 메시지 발견
      - 해결 : 메모리를 잡아먹던 불필요한 라이브러리와 전역변수 제거
