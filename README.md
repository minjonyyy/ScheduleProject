# 🗓️일정 관리 앱 만들기

### 🪄 [<일정 관리 앱 만들기> 회고 및 이슈 해결](https://velog.io/@minjonyyy/Spring-%EC%97%90%EB%9F%AC-cannot-resolve-symbol-validation) 보러가기

## 📍필수 기능
### 단계별 요구사항
### LV.0 API 명세 및 ERD 작성
**1. API 명세서**

https://documenter.getpostman.com/view/41327981/2sAYX5Kha8

**2. ERD**

  <img src="https://github.com/user-attachments/assets/13456d1a-2206-4823-b9e3-6942b97794b0" width=65%>


**3. SQL 쿼리 작성하기**

```sql
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255),
    user_email VARCHAR(255) UNIQUE
);

CREATE TABLE schedules (
    schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200),
    task VARCHAR(200),
    username VARCHAR(10),
    user_email VARCHAR(255),
    password VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```


***


### 1️⃣LV.1 일정 생성 및 조회
- **일정 생성 (일정 작성하기)**
    - [ ]  `할일`, `작성자명`, `비밀번호`, `작성/수정일`을 저장

    - [ ]  `작성/수정일`은 날짜와 시간을 모두 포함한 형태
    - [ ]  최초 입력 시, 수정일은 작성일과 동일
    - [ ]  각 일정의 고유 식별자(ID)를 자동으로 생성하여 관리
    - `createSchedule(@Valid @RequestBody ScheduleRequestDto dto)`
  
      <img src="https://github.com/user-attachments/assets/d52c5df6-7e8e-408a-8d30-23f5411ab12d" width=65%>

    
- **전체 일정 조회 (등록된 일정 불러오기)**
    - [ ]  다음 조건을 바탕으로 등록된 일정 목록을 전부 조회
      -  `수정일` (형식 : YYYY-MM-DD)
      -  `작성자명`
    - [ ]  조건 중 한 가지만을 충족하거나, 둘 다 충족을 하지 않을 수도, 두 가지를 모두 충족할 수도 있습니다.
    - [ ]  `수정일` 기준 내림차순으로 정렬하여 조회
    - `findAllSchedules()`
  

- **선택 일정 조회(선택한 일정 정보 불러오기)**
    - [ ]  선택한 일정 단건의 정보를 조회할 수 있습니다.
    - [ ]  일정의 고유 식별자(ID)를 사용하여 조회합니다.
    - `findScheduleById(@PathVariable Long id)`

***
### 2️⃣LV.2 일정 수정 및 삭제
- **선택한 일정 수정**
    - [ ]  선택한 일정 내용 중 `할일`, `작성자명` 만 수정 가능
        - [ ]  서버에 일정 수정을 요청할 때 `비밀번호`를 함께 전달합니다.
        - [ ]  `작성일` 은 변경할 수 없으며, `수정일` 은 수정 완료 시, 수정한 시점으로 변경합니다.
    - `updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto dto)`
      <img src="https://github.com/user-attachments/assets/737fdd3a-7488-4790-aaa9-e3e30f3fbb5f" width=65%>



- **선택한 일정 삭제**
    - [ ]  선택한 일정을 삭제할 수 있습니다.
        - [ ]  서버에 일정 수정을 요청할 때 `비밀번호`를 함께 전달합니다.
    - `deleteScheduleById(@PathVariable Long id, @RequestBody ScheduleRequestDto dto)`
      <img src="https://github.com/user-attachments/assets/3698caf4-2e4b-41da-bfbc-bcd83e174165" width=65%>



***
## 📍도전 기능

### 3️⃣LV.3 연관 관계 설정
-  **작성자와 일정의 연결**
      - [ ]  작성자 테이블을 생성하고 일정 테이블에 FK를 생성해 연관관계를 설정

          - [ ]  작성자는 `이름` 외에 `이메일`, `등록일`, `수정일` 정보를 가지고 있습니다.
          - [ ]  작성자의 고유 식별자를 통해 일정이 검색이 될 수 있도록 전체 일정 조회 코드 수정.

💬 할 일 생성 시, `이메일`을 식별자로 하여 `기존 userId`가 존재하는지 확인함. <br>
   userId가 존재하지 않는다면 user테이블에 새로운 데이터를 추가함. <br>
   작성자의 고유 식별자(userId)를 통해 일정을 검색할 수 있도록 `findSchedulesWithFilters()`의 조건 수정.
   
##### <users 데이터베이스>
<img src="https://github.com/user-attachments/assets/4c2bbcae-28f7-45bb-a644-38995de8c0ad" width=50%>

***

### 4️⃣LV.4 페이지네이션

- `페이지 번호`와 `페이지 크기`를 쿼리 파라미터로 전달하여 요청하는 항목 나타내기
    - [ ]  등록된 일정 목록을 `페이지 번호`와 `크기`를 기준으로 모두 조회
    - [ ]  조회한 일정 목록에는 `작성자 이름`이 포함
    - [ ]  범위를 넘어선 페이지를 요청하는 경우 빈 배열을 반환

💬`localhost:8080/schedules/page?pageNum=0&pageSize=10` 으로 요청 <br>
요청사항 없으면 기본 `pageNum=0, pageSize=5`로 설정함 <br>
<img src="https://github.com/user-attachments/assets/89421868-643d-4fba-a202-34c77ce931d4" width=60%>

***

### 5️⃣LV.5 예외 발생 처리
- 예외 상황에 대한 처리를 위해 [`HTTP 상태 코드(링크)`](https://developer.mozilla.org/ko/docs/Web/HTTP/Status)와 `에러 메시지`를 포함한 정보를 사용하여 예외를 관리할 수 있습니다.
    1. 필요에 따라 사용자 정의 예외 클래스를 생성하여 예외 처리를 수행할 수 있습니다.
    2. `@ExceptionHandler`를 활용하여 공통 예외 처리를 구현할 수도 있습니다.
    3. 예외가 발생할 경우 적절한 HTTP 상태 코드와 함께 사용자에게 메시지를 전달하여 상황을 관리합니다.
    
- [ ]  수정, 삭제 시 요청할 때 보내는 `비밀번호`가 일치하지 않을 때 예외가 발생
- [ ]  선택한 일정 정보를 조회할 수 없을 때 예외가 발생
    1.   잘못된 정보로 조회하려고 할 때
    2. 이미 삭제된 정보를 조회하려고 할 때

##### <일정 삭제 시 비밀번호 불일치>
<img src="https://github.com/user-attachments/assets/8529db49-40ed-462d-b3ea-b4ab7387ee42" width=65%>

***

### 6️⃣ Lv.6 null 체크 및 특정 패턴에 대한 검증 수행
- 유효성 검사
    1. 잘못된 입력이나 요청을 미리 방지할 수 있습니다.
    2. 데이터의 `무결성을 보장`하고 애플리케이션의 예측 가능성을 높여줍니다.
    3. Spring에서 제공하는 `@Valid` 어노테이션을 이용할 수 있습니다.
- 조건
    - [ ]  `할일`은 최대 200자 이내로 제한, 필수값 처리
    - [ ]  `비밀번호`는 필수값 처리
    - [ ]  담당자의 `이메일` 정보가 형식에 맞는지 확인
  
##### <이메일 형식 검증>
<img src="https://github.com/user-attachments/assets/46e24c79-0d4e-4855-bd0f-6795c73e7e84" width=65%>

##### <할일 200자 검증>
<img src="https://github.com/user-attachments/assets/4ff819a3-b8fb-4836-801a-9096371c2735" width=65%>

##### <비밀번호 필수값 처리>
<img src="https://github.com/user-attachments/assets/ba7d0d4d-55b7-4395-8b37-2e42c02a2973" width=65%>


