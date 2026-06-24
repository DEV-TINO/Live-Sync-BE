# 이메일 초대 만료/중복 정책 및 API 수정 계획

## 배경

현재 워크스페이스 초대는 등록된 회원 기준으로 동작합니다.

- `Invitation`은 `workspaceId`, `memberId`, `status`, `createdAt`만 저장합니다.
- `WorkspaceService.invite(...)`는 `loginId`로 기존 회원을 찾아 초대합니다.
- 만료 시간, 이메일 대상, 초대 토큰, 중복 초대 정책이 없습니다.
- 초대 수락은 `POST /workspace/invite/{inviteId}/accept`에서 로그인된 `memberId`로 처리합니다.

이 구조는 이미 가입된 회원을 초대할 때는 동작하지만, 이메일로 먼저 초대하고 이후 가입/수락으로 이어지는 흐름에는 부족합니다.

## 목표

- 이메일 주소로 워크스페이스 초대를 보낼 수 있게 합니다.
- 같은 워크스페이스와 같은 이메일/회원에 대한 활성 초대 중복을 막습니다.
- 오래된 초대는 만료 처리합니다.
- 초대 수락은 예측 불가능한 토큰 기반으로 처리합니다.
- 기존 `loginId` 기반 초대 API는 프론트 전환 전까지 유지합니다.

## 제외 범위

- 1차 PR에서는 SMTP/API 키가 준비되지 않았다면 실제 이메일 발송 연동은 제외합니다.
- 워크스페이스 멤버십 구조 전체를 다시 설계하지 않습니다.
- 기존 회원 초대 API를 즉시 제거하지 않습니다.

## 추천 정책

### 초대 상태

상태는 다음처럼 확장합니다.

```text
PENDING
ACCEPTED
REJECTED
EXPIRED
CANCELED
```

의미:

- `PENDING`: 아직 유효하며 수락 가능한 초대
- `ACCEPTED`: 수락 완료, 멤버십 생성됨
- `REJECTED`: 초대받은 사용자가 거절함
- `EXPIRED`: 만료 시간이 지나 더 이상 수락 불가
- `CANCELED`: 관리자가 초대를 취소하거나 재발급으로 무효화함

### 만료 정책

기본 만료 시간:

```text
createdAt 기준 7일
```

수락 시 반드시 확인합니다.

```text
status == PENDING
now <= expiresAt
```

`now > expiresAt`이면 상태를 `EXPIRED`로 바꾸고 수락을 거절합니다.

### 중복 방지 정책

중복 기준:

```text
workspaceId + inviteeEmail + status=PENDING
```

초대 이메일이 이미 가입된 회원과 매핑되면 다음 조건도 중복으로 봅니다.

```text
workspaceId + memberId + status=PENDING
```

추천 동작:

1. 이미 워크스페이스 멤버이면 초대하지 않고 충돌 응답을 반환합니다.
2. 아직 만료되지 않은 `PENDING` 초대가 있으면 새로 만들지 않고 기존 초대 요약을 반환합니다.
3. `PENDING` 초대가 있지만 이미 만료됐으면 `EXPIRED` 처리 후 새 초대를 만듭니다.
4. 관리자가 "재전송"을 누르면 기존 pending 초대를 재사용하고 `lastSentAt`만 갱신합니다.
5. 관리자가 "재발급"을 누르면 기존 pending 초대를 `CANCELED` 처리하고 새 토큰으로 초대를 만듭니다.

이렇게 하면 같은 대상에게 동시에 유효한 초대 토큰이 여러 개 생기는 문제를 피할 수 있습니다.

## 엔티티 변경안

`Invitation`에 다음 필드를 추가합니다.

```java
private Long workspaceId;
private Long memberId;       // 이메일이 기존 회원과 매핑되기 전까지 nullable
private String inviteeEmail; // 소문자 정규화 이메일
private String tokenHash;    // 원본 토큰이 아니라 해시만 저장
private InviteStatus status;
private LocalDateTime createdAt;
private LocalDateTime expiresAt;
private LocalDateTime acceptedAt;
private LocalDateTime rejectedAt;
private LocalDateTime canceledAt;
private LocalDateTime lastSentAt;
private Long createdByMemberId;
```

토큰 저장 정책:

- 이메일 링크에는 랜덤 원본 토큰을 넣습니다.
- DB에는 원본 토큰이 아니라 해시만 저장합니다.
- 원본 토큰은 이메일 발송 시 한 번만 사용합니다.

초대 링크 예시:

```text
https://<frontend-domain>/workspace/invite/accept?token=<raw-token>
```

## Repository 변경안

추가할 조회 메서드:

```java
Optional<Invitation> findByTokenHash(String tokenHash);

Optional<Invitation> findFirstByWorkspaceIdAndInviteeEmailAndStatusOrderByCreatedAtDesc(
        Long workspaceId,
        String inviteeEmail,
        Invitation.InviteStatus status
);

Optional<Invitation> findFirstByWorkspaceIdAndMemberIdAndStatusOrderByCreatedAtDesc(
        Long workspaceId,
        Long memberId,
        Invitation.InviteStatus status
);
```

권장 인덱스:

```text
workspace_id, invitee_email, status
workspace_id, member_id, status
token_hash
```

MySQL은 `PENDING` 상태만 대상으로 하는 partial unique index를 직접 쓰기 어렵기 때문에, 중복 방지는 서비스 레벨 검증을 기본으로 둡니다.

## API 변경안

### 이메일 초대 생성

```http
POST /workspace/invite/email
Authorization: Bearer <admin-workspace-token>
Content-Type: application/json

{
  "workspaceId": 1,
  "email": "user@example.com"
}
```

응답:

```json
{
  "inviteId": 10,
  "workspaceId": 1,
  "email": "user@example.com",
  "status": "PENDING",
  "expiresAt": "2026-07-01T18:00:00",
  "duplicate": false
}
```

중복 pending 초대가 있으면 `200 OK`와 `duplicate: true`를 반환하는 방식을 추천합니다.  
이렇게 하면 프론트에서 "이미 초대됨" 상태를 자연스럽게 처리할 수 있습니다.

### 이메일 초대 재전송

```http
POST /workspace/invite/{inviteId}/resend
Authorization: Bearer <admin-workspace-token>
```

규칙:

- 워크스페이스 관리자만 재전송할 수 있습니다.
- `PENDING` 초대만 재전송할 수 있습니다.
- 이미 만료된 초대는 재전송하지 않고 재발급을 요구합니다.

### 이메일 초대 재발급

```http
POST /workspace/invite/{inviteId}/reissue
Authorization: Bearer <admin-workspace-token>
```

규칙:

- 기존 pending 초대는 `CANCELED` 처리합니다.
- 새 토큰과 새 `expiresAt`을 가진 초대를 생성합니다.

### 이메일 초대 수락

```http
POST /workspace/invite/email/accept
Content-Type: application/json

{
  "token": "<raw-token>",
  "loginId": "optional-existing-or-new-login-id",
  "password": "optional-new-user-password",
  "nickname": "optional-new-user-nickname"
}
```

수락 흐름:

1. 원본 토큰을 해시해 초대를 조회합니다.
2. 초대가 없으면 거절합니다.
3. `PENDING` 상태가 아니면 거절합니다.
4. 만료됐으면 `EXPIRED` 처리 후 거절합니다.
5. 회원을 결정합니다.
   - 로그인 상태라면 인증된 회원을 사용합니다.
   - 이메일에 해당하는 계정이 없으면 가입 흐름으로 연결하거나 계정을 생성합니다.
   - 로그인된 사용자의 이메일이 초대 이메일과 다르면 거절합니다.
6. 이미 워크스페이스 멤버이면 거절합니다.
7. `MemberWorkspace`를 `SHOWHOST` 권한으로 생성합니다.
8. 초대 상태를 `ACCEPTED`로 변경합니다.
9. 워크스페이스 관리자에게 수락 알림을 보냅니다.

## Service 변경안

`WorkspaceService`에 다음 메서드를 추가합니다.

```java
EmailInviteResponse inviteByEmail(Long adminId, EmailInviteRequest request);
EmailInviteResponse resendEmailInvite(Long adminId, Long inviteId);
EmailInviteResponse reissueEmailInvite(Long adminId, Long inviteId);
void acceptEmailInvite(EmailInviteAcceptRequest request);
```

검증 helper:

```java
private void validateWorkspaceAdmin(Long memberId, Long workspaceId);
private String normalizeEmail(String email);
private void expireIfNeeded(Invitation invite, LocalDateTime now);
private void validateNotAlreadyMember(Long memberId, Long workspaceId);
```

## 예외 케이스

| 상황 | 기대 동작 |
| --- | --- |
| 같은 이메일을 같은 워크스페이스에 두 번 초대 | 기존 pending 초대 반환 또는 duplicate 응답 |
| 이미 수락한 사용자를 다시 초대 | 이미 멤버이므로 충돌 응답 |
| 만료된 초대 수락 | `EXPIRED` 처리 후 거절 |
| 만료된 초대 재전송 | 거절하고 재발급 요구 |
| pending 초대 재발급 | 기존 초대 `CANCELED`, 새 초대 `PENDING` |
| 수락 완료된 토큰 재사용 | 거절 |
| 다른 사용자가 토큰 수락 | 초대 이메일/회원 불일치면 거절 |
| 이메일 대소문자 차이 | 소문자 정규화 후 비교 |

## 테스트 계획

서비스 테스트:

- 관리자는 이메일 초대를 생성할 수 있습니다.
- 관리자가 아니면 이메일 초대를 생성할 수 없습니다.
- 중복 pending 초대는 새로 생성되지 않습니다.
- 만료된 pending 초대는 `EXPIRED` 처리 후 새 초대를 만들 수 있습니다.
- 재발급 시 기존 초대는 `CANCELED`, 새 초대는 `PENDING`이 됩니다.
- 유효한 초대를 수락하면 `MemberWorkspace`가 생성됩니다.
- 만료된 초대 수락은 실패합니다.
- 이미 수락된 초대 재사용은 실패합니다.
- 이미 워크스페이스 멤버인 사용자는 다시 초대할 수 없습니다.

컨트롤러 테스트:

- `POST /workspace/invite/email`은 Authorization 헤더가 필요합니다.
- `POST /workspace/invite/{inviteId}/resend`는 관리자 권한이 필요합니다.
- `POST /workspace/invite/email/accept`는 토큰 body를 처리합니다.

수동 API 체크:

```text
1. 관리자가 로그인하고 워크스페이스를 생성합니다.
2. 관리자가 email A를 초대합니다.
3. 관리자가 email A를 다시 초대합니다.
4. 기존 초대 재사용 또는 duplicate 응답인지 확인합니다.
5. 토큰으로 초대를 수락합니다.
6. 워크스페이스 멤버가 생성됐는지 확인합니다.
7. 같은 토큰을 다시 사용합니다.
8. 재사용이 거절되는지 확인합니다.
```

## 추천 PR 분리

### PR 1: 데이터 모델과 중복/만료 정책

- `Invitation` 필드 확장
- Repository 조회 추가
- 서비스 레벨 중복/만료 검증 추가
- 테스트 추가

### PR 2: 이메일 초대 API

- 요청/응답 DTO 추가
- 생성/재전송/재발급/수락 API 추가
- 컨트롤러 테스트 추가

### PR 3: 이메일 발송 연동

- 이메일 provider 연동
- 초대 링크 발송
- 필요하면 재전송 rate limit 추가

## 논의 필요 사항

- 초대 수락 시 계정을 자동 생성할지, 프론트 회원가입 화면으로 보낼지
- 프론트의 초대 수락 URL은 무엇인지
- 중복 pending 초대 시 `200 OK + duplicate: true`로 갈지, `409 Conflict`로 갈지
- 만료 처리는 수락/조회 시점의 lazy 처리로 충분한지, 스케줄러가 필요한지
- 워크스페이스별/관리자별 초대 발송 제한이 필요한지
