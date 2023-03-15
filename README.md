![Community jwj](https://user-images.githubusercontent.com/123234152/225210038-aea25e18-6fa4-4db0-bfde-c700b56ec4a3.png)

[Community 프로젝트 체험하기](http://ec2-3-36-246-194.ap-northeast-2.compute.amazonaws.com/boards?boardType=DEV&desc=%EA%B0%9C%EB%B0%9C+%EA%B2%8C%EC%8B%9C%ED%8C%90)

## 1. Community 프로젝트란?
Community 프로젝트는 일상생활에서 쉽게 접할수 있는 게시판을 SpringBoot를 이용하여 구현한 프로젝트입니다. <br><br>

## 2. Community 프로젝트 시작 동기
SpringBoot를 학습하면서 그동안 여러가지의 토이프로젝트를 만들었었지만 결국에는 완성을 하지 못하고 중도포기 한 적이 많았습니다.<br><br>
그 이유는 1개의 웹사이트를 만들면서 아주 간단한 기능을 넘어선 이메일 인증, 비밀번호 변경 및 주기관리 등의 부가기능을 넣으려고 하면<br>
점차 프로젝트가 거대해지고 개인적으로 흥미도 점점 떨어졌기 때문입니다.<br><br>
그래서 이번에는 간단한 기능이라도 제대로 요구사항부터 정리하여 기획 -> 개발 -> 화면구성 -> 배포까지 일련의 과정을 완료하는 Community 프로젝트를 시작하였습니다.<br><br>

## 3. 기술스택
1. SpringBoot & Java & Hibernate
2. Spring Security
5. Querydsl & MySQL
4. Junit<br><br>

## 4. Community 프로젝트 ERD (이미지를 클릭하여 크게 보실 수 있습니다.)
<img src="https://user-images.githubusercontent.com/123234152/225208959-519367ad-fc66-4d19-97a4-bf9b383e454d.png" width="300px " height="300px">

## 5. 주요기능
1. Spring Security를 사용한 로그인 기능
2. 게시글 작성 및 대댓글까지 작성할 수 있는 기능

## 6. 추가 및 개선해야할 기능
1. 로그인 시 사용자 이메일 저장기능 추가
2. 비밀번호 변경주기 알림 및 비밀번호 변경기능 추가
3. 회원가입 시 이메일 인증기능 추가
4. 게시글 좋아요, 싫어요 기능 추가
5. 관리자 페이지 및 권한 CRUD 기능 추가
6. 마이 페이지 CRUD 기능 추가
7. 게시판 및 댓글 조회 시 N+1 이슈 개선
8. SpringBoot의 OSIV 기능을 사용하지 않고 게시판 기능을 사용할 수 있도록 개선
