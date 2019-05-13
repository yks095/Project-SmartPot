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
    - `userID` 값과 `메일`, `화분명` 값을 미리 받아와 `TextView`에 띄울 예정
    - 회원정보 수정 화면에서 `이메일`과 `화분명`을 수정할 수 있도록 할 예정
    - 비밀번호 수정 화면에서 `비밀번호`를 수정할 수 있도록 할 예정
