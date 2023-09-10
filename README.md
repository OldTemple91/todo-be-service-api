# Todo List 서비스

안녕하세요!  
아래 서비스 구현에 대한 설명을 작성했으니 참고해주시면 감사하겠습니다!


## 서비스 명세
### 기술스펙
Kotlin, Spring boot, Spring Data JPA, QueryDSL, MySQL

### 프로젝트 및 데이터 세팅 방법
MySQL 테이블 생성 쿼리와 포스트맨 API을 첨부하였습니다. 살펴보시고 생성 및 실행 해주시면 감사하겠습니다.  
- 데이터베이스 생성: **create database todo**
- 테이블 생성 쿼리: sql 폴더 - todo.ddl 참조
- 포스트맨 API: 첨부파일 참조
- 유닛 테스트: test 폴더 참조(회원, Todo List 테스트)

### 서비스 부연 설명
1. Account
- 탈퇴 컬럼인 withdrawYn을 통하여 db 삭제가 아닌 탈퇴 유무로 처리했습니다.
    - 탈퇴 유저를 삭제하게 되면 Todo뿐만 아니라 앞으로 연관될 도메인 데이터 모두 처리여부를 고려해야되기 때문에 컬럼으로 관리해두었습니다.
- 권한 컬럼인 role은 현재 ADMIN, USER밖에 없지만, 추후 유저 등급 세분화를 통해 유저 등급에 따른 쿠폰, 혜택을 부여할 수 있게 했습니다.
2. Todo
- 가장 최근의 TODO 1건 조회 기능
    - 시간의 기준: todoAt(todo 설정 시간)과 createdAt(생성일)과 기준을 어느 것으로 정할지 생각했었고, todoAt 기준으로 설정했습니다.
    - 최근의 기준: 과거의 Todo가 가장 가까워도 Todo의 목적이 할일에 대한 리스트 서비스이기 때문에, 조회 시점 기준 미래의 가장 가까운 Todo를 가져오게 설정했습니다. (ex. 현재 12시 10분 기준, 12시 05분 데이터와 12시 20분 데이터가 있으면 12시 20분 데이터를 불러옵니다.)

### 로직 실행 방법
1. 인증 관련  
인증은 jwt 기반으로 개발했습니다. 로그인과 리프레시 토큰 갱신으로 구성됩니다.
- 로그인:
   - 로그인 시 유저 확인 및 탈퇴 유저인지 검증 후 토큰을 반환합니다.
   - POST /authenticate
   - Request Body:

     | 필드       | 필수여부 | 데이터타입  | 설명   |
     |----------|------|--------|------|
     | email    | O    | String | 이메일  |
     | password | O    | String | 비밀번호 |

    - Response Body:

     | 필드           | 필수여부 | 데이터타입  | 설명            |
     |--------------|------|--------|---------------|
     | accessToken  | O    | String | access token  |
     | refreshToken | O    | String | refresh token |

- 리프레시 토큰 갱신:
    - access token 만료 시 요청하게 되며, 토큰 검증 후 갱신 된 토큰을 반환합니다. refresh token 검증이 실패하면 유저는 재로그인을 합니다.
    - POST /reissue
    - Request Body:

      | 필드           | 필수여부 | 데이터타입  | 설명            |
      |--------------|------|--------|---------------|
      | refreshToken | O    | String | refresh token |

    - Response Body:

      | 필드           | 필수여부 | 데이터타입  | 설명            |
      |--------------|------|--------|---------------|
      | accessToken  | O    | String | access token  |
      | refreshToken | O    | String | refresh token |

####
2. 회원 관련  
유저는 회원가입과 탈퇴를 할 수 있습니다. 
 - 회원가입:
    - 회원가입 시 존재하는 이메일의 경우 가입이 불가합니다.
    - POST /accounts
    - Request Body:
   
      | 필드       | 필수여부 | 데이터타입  | 설명             |
      |----------|------|--------|----------------|
      | email    | O    | String | 이메일            |
      | password | O    | String | 비밀번호           |
      | nickname | O    | String | 닉네임            |

- 회원탈퇴:
    - 탈퇴 시 토큰 회원 정보와 요청 회원 id를 검증 후 탈퇴를 합니다.
    - POST /accounts/:id/withdraw
    - Headers:
  
      | 필드               | 필수여부 | 데이터타입  | 설명                                      |
      |------------------|------|--------|-----------------------------------------|
      | {{access_token}} | O    | String | Authorization : Bearer {{access_token}} |

    - Parameter:
  
      | 필드 | 필수여부 | 데이터타입 | 설명         |
      |----|------|-------|------------|
      | id | O    | Long  | account id |

3. Todo 관련  
Todo 생성, 리스트 조회, 가장 최근의 1건 조회, 상태변경이 있습니다. 모두 토큰 기반으로 해당 유저의 Todo를 CRUD합니다.
- Todo 생성:
   - Todo를 생성합니다.
   - POST /todos
   - Headers:

     | 필드               | 필수여부 | 데이터타입  | 설명                                      |
           |------------------|------|--------|-----------------------------------------|
     | {{access_token}} | O    | String | Authorization : Bearer {{access_token}} |

   - Request Body:

     | 필드      | 필수여부 | 데이터타입         | 설명         |
     |---------|------|---------------|------------|
     | content | O    | String        | todo 내용    |
     | todoAt  | O    | LocalDateTime | todo 설정 시간 |

- Todo list 조회:
    - 유저가 생성한 Todo 리스트를 반환합니다.
    - GET /todos
    - Headers:

      | 필드               | 필수여부 | 데이터타입  | 설명                                      |
                 |------------------|------|--------|-----------------------------------------|
      | {{access_token}} | O    | String | Authorization : Bearer {{access_token}} |

    - Response Body:

      | 필드        | 필수여부 | 데이터타입         | 설명                               |
      |-----------|------|---------------|----------------------------------|
      |           |      | TodoDto[]     |                                  |
      | todoId    | O    | Long          | todo id                          |
      | accountId | O    | Long          | account id                       |
      | nickname  | O    | String        | 닉네임                              |
      | content   | O    | String        | todo 내용                          |
      | status    | O    | TodoStatus    | todo 상태(TODO, ONGOING, COMPLETE) |
      | todoAt    | O    | LocalDateTime | todo 설정 시간                       |
      | createdAt | O    | LocalDateTime | todo 생성 시간                       |

- 가장 최근의 TODO 1건 조회:
    - todoAt 기준으로 조회 시점 시간 이후의 todo 중 가장 가까운 todo 1개를 반환합니다.
    - GET /todos/recent
  - Headers:

    | 필드               | 필수여부 | 데이터타입  | 설명                                      |
                     |------------------|------|--------|-----------------------------------------|
    | {{access_token}} | O    | String | Authorization : Bearer {{access_token}} |

  - Response Body:

    | 필드        | 필수여부 | 데이터타입         | 설명                               |
    |-----------|------|---------------|----------------------------------|
    | todoId    | O    | Long          | todo id                          |
    | accountId | O    | Long          | account id                       |
    | nickname  | O    | String        | 닉네임                              |
    | content   | O    | String        | todo 내용                          |
    | status    | O    | TodoStatus    | todo 상태(TODO, ONGOING, COMPLETE) |
    | todoAt    | O    | LocalDateTime | todo 설정 시간                       |
    | createdAt | O    | LocalDateTime | todo 생성 시간                       |


- Todo 상태 변경:
    - Todo의 상태(할 일, 진행중, 완료됨)를 변경합니다.
    - PUT /todos/:id/status
    - Headers:

      | 필드               | 필수여부 | 데이터타입  | 설명                                      |
      |------------------|------|--------|-----------------------------------------|
      | {{access_token}} | O    | String | Authorization : Bearer {{access_token}} |

    - Parameter:

      | 필드        | 필수여부 | 데이터타입         | 설명                               |
      |-----------|------|---------------|----------------------------------|
      | id        | O    | Long          | todo id                          |

    - Request Body:

      | 필드     | 필수여부 | 데이터타입      | 설명                               |
      |--------|------|------------|----------------------------------|
      | status | O    | TodoStatus | todo 상태(TODO, ONGOING, COMPLETE) |

    

