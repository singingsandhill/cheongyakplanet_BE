# Error Code Reference

## Overview

This document provides the complete reference for all error codes defined in `ErrorCode.java`. Each error code follows the pattern `[CATEGORY][NUMBER]`.

## Account (SIGN)

| Code | Message | Description |
|------|---------|-------------|
| SIGN000 | 로그인이 필요한 서비스입니다. | Login required to access this service |
| SIGN001 | 일치하는 이메일 없음 | No matching email found |
| SIGN002 | 중복된 이메일 존재 | Duplicate email exists |
| SIGN003 | 관리자 가입 토큰 불일치 | Admin registration token mismatch |
| SIGN004 | 로그인 정보 불일치 | Login credentials mismatch |
| SIGN005 | 사용자를 찾을 수 없음 | User not found |
| SIGN006 | 탈퇴한 회원 | Withdrawn member |

## JWT Authentication (AUTH)

| Code | Message | Description |
|------|---------|-------------|
| AUTH001 | 유효하지 않은 JWT 서명 | Invalid JWT signature |
| AUTH002 | 만료된 토큰 | Expired token |
| AUTH003 | 지원되지 않는 토큰 | Unsupported token |
| AUTH004 | claim is empty | JWT claims are empty |
| AUTH005 | 유효하지 않은 토큰 | Invalid token |
| AUTH006 | 로그아웃된 토큰 | Logged-out (blacklisted) token |
| AUTH007 | 관리자 권한이 필요합니다 | Admin role required |
| AUTH010 | 토큰 없음 | Token not provided |

## Community (COMU)

| Code | Message | Description |
|------|---------|-------------|
| COMU001 | 게시글 없음 | Post not found |
| COMU002 | 게시글에 수정 권한 없음 | No permission to edit post |
| COMU003 | 이미 반응 함 | Already reacted to this content |

## Subscription Info (INFO)

| Code | Message | Description |
|------|---------|-------------|
| INFO001 | 해당 아이디의 청약건 없음 | No subscription found for this ID |
| INFO002 | 해당 지역의 청약건 없음 | No subscription found for this region |
| INFO003 | 위치 정보가 없는 청약건입니다. | Subscription has no location data |
| INFO005 | 지역 테이블 없음, DB확인 | Region table missing, check DB |
| INFO006 | 관심지역 아님 | Not an interest location |
| INFO007 | 이미 추가한 관심 청약 | Already added as interest subscription |

## MyPage / User (USER)

| Code | Message | Description |
|------|---------|-------------|
| USER001 | 사용자를 찾을 수 없습니다. | User not found |
| USER002 | 유효하지 않은 입력값입니다. | Invalid input value |
| USER003 | 이미 탈퇴한 사용자입니다. | Already withdrawn user |
| USER004 | 회원 탈퇴 시 토큰 삭제에 실패했습니다. | Failed to delete tokens during withdrawal |

## Interest Location (LOCATION)

| Code | Message | Description |
|------|---------|-------------|
| LOCATION001 | 이미 등록된 관심 지역입니다. | Already registered interest location |
| LOCATION002 | 최대 5개의 관심 지역만 등록할 수 있습니다. | Maximum 5 interest locations allowed |
| LOCATION003 | 등록되지 않은 관심 지역입니다. | Not a registered interest location |

## Other (OTHER)

| Code | Message | Description |
|------|---------|-------------|
| OTHER001 | 필터 부분 에러 | Filter error |

## Unknown (UNK)

| Code | Message | Description |
|------|---------|-------------|
| UNK000 | 알 수 없는 에러 발생 | Unknown error occurred |

## Error Response Format

```json
{
    "status": "fail",
    "data": {
        "code": "ERROR_CODE",
        "message": "에러 메시지"
    }
}
```

---

**Source**: `src/main/java/org/cheonyakplanet/be/domain/exception/ErrorCode.java`
**Last Updated**: 2026-03-05
**Total Error Codes**: 28
