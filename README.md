# CLUVR Notifications Service

Spring Boot 기반의 실시간 알림 서비스입니다. AWS API Gateway + Cognito 인증을 사용하며, MongoDB와 RabbitMQ를 통한 확장 가능한 알림 시스템을 제공합니다.

## 🏗️ 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Server    │───▶│  RabbitMQ       │───▶│ Notification    │
│ (cluvr-api)     │    │ (Message Queue) │    │ Service         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client        │◀───│     SSE         │◀───│   MongoDB       │
│ (Web/Mobile)    │    │ (Real-time)     │    │ (Notification   │
└─────────────────┘    └─────────────────┘    │  Storage)       │
                                              └─────────────────┘
```

## 🚀 주요 기능

### 1. 실시간 알림 전송
- **SSE (Server-Sent Events)** 를 통한 실시간 알림 푸시
- 사용자별 동적 큐 생성 및 관리
- 연결 끊김 시 자동 재연결 및 미전송 알림 처리

### 2. MongoDB 기반 알림 저장
- 모든 알림을 MongoDB에 영구 저장
- 읽음/안읽음 상태 관리
- 페이징을 통한 알림 목록 조회

### 3. 알림 설정 관리
- 사용자별 알림 타입 on/off 설정
- 좋아요, 댓글, 채팅 등 세분화된 알림 제어

### 4. JWT 기반 인증
- AWS Cognito JWT 토큰 검증
- **JWT 구조 변경**: `custom:userId` 제거 → `sub`를 통한 userId 조회
- API 서버 연동을 통한 사용자 정보 조회 (`/api/users/sub/{sub}/user-id`)

## 🛠️ 기술 스택

- **Backend**: Spring Boot 3.2, Java 17
- **Database**: MongoDB (알림 저장), MySQL (설정 저장)
- **Message Queue**: RabbitMQ
- **Authentication**: AWS Cognito, JWT
- **Real-time**: SSE (Server-Sent Events)
- **HTTP Client**: WebClient (API 서버 연동)

## 🔧 JWT 처리 플로우

```
JWT 토큰 수신 → sub 추출 → API 서버 호출 → userId 반환 → 알림 처리
```

### JWT 구조 변경사항
- **기존**: `jwt.getClaim("custom:userId")` 직접 사용
- **현재**: `jwt.getSubject()` → API 서버 `/api/users/sub/{sub}/user-id` 호출 → userId 획득

```java
// JwtUserExtractor 사용 예시
@RestController
public class NotificationController {
    
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwtUserExtractor.extractUserId(jwt); // sub → userId 변환
        // ... 알림 조회 로직
    }
}
```

## 📡 API 엔드포인트

### 알림 조회
```http
GET /notifications?page=0&size=10&isRead=false
Authorization: Bearer {jwt_token}
```

### SSE 연결
```http
GET /notifications/connect
Authorization: Bearer {jwt_token}
```

### 알림 설정 변경
```http
PATCH /notifications/settings
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "COMMENT": true,
  "LIKE": false,
  "CHAT": true
}
```

## 🎯 주요 개선사항

### JWT 처리 변경
1. **기존**: JWT에 `custom:userId` 포함되어 있어서 직접 사용
2. **현재**: JWT에 `custom:userId` **제거됨** → `sub`만 존재
3. **해결**: `sub`를 통해 API 서버에서 userId 조회하는 방식으로 변경

### 알림 시스템 완전 리팩토링
1. **MySQL → MongoDB 이관**: 모든 알림 데이터를 MongoDB에 저장
2. **실시간 처리 최적화**: isRead 상태 관리 개선
3. **확장성 향상**: 사용자별 동적 큐 생성

## 📝 참고사항

- **JWT 변경**: `custom:userId` 없음 → `sub` 기반 userId 조회 필수
- **API 서버 의존성**: userId 조회를 위해 API 서버와 통신 필요
- **포트**: 8081 (기본값)
- **데이터베이스**: MongoDB (알림), MySQL (설정)
