# API Documentation

## API Overview

CheonYakPlanet provides a RESTful API for Korean real estate subscription management, along with WebSocket support for real-time AI chat.

**Base URL**: `http://localhost:8080/api`
**Authentication**: JWT Bearer Token
**Content-Type**: `application/json`

## Authentication

### Authentication Flow
1. **Register**: `POST /api/member/signup`
2. **Login**: `POST /api/member/login`
3. **Use Token**: Include `Authorization: Bearer {access_token}` in headers
4. **Refresh**: `POST /api/member/auth/refresh` when access token expires
5. **Logout**: `POST /api/member/logout`

### Token Structure
- **Access Token**: 60 minutes lifespan
- **Refresh Token**: 24 hours lifespan
- **Storage**: Database with blacklisting support

## API Endpoints

### User Management (`/api/member`) — UserController

#### Register User
```http
POST /api/member/signup
Content-Type: application/json

{
    "email": "user@cheonyakplanet.com",
    "password": "password123!",
    "username": "청약초보",
    "phoneNumber": "010-1234-5678"
}
```

#### Login
```http
POST /api/member/login
Content-Type: application/json

{
    "email": "user@cheonyakplanet.com",
    "password": "password123!"
}
```

#### Logout
```http
POST /api/member/logout
Authorization: Bearer {access_token}
```

#### Kakao OAuth Callback
```http
GET /api/member/kakao/callback?code={authorization_code}
```

#### Kakao Token Exchange
```http
GET /api/member/kakao/exchange?state={state}
```

#### Refresh Token
```http
POST /api/member/auth/refresh?refreshToken={refreshToken}
```

#### Get My Page
```http
GET /api/member/mypage
Authorization: Bearer {access_token}
```

#### Update Profile
```http
PATCH /api/member/mypage
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "username": "청약전문가",
    "marriageStatus": "기혼",
    "numberOfChildren": 2,
    "monthlyIncome": 600,
    "totalAssets": 2500,
    "hasHouse": false
}
```

Response includes updated `Authorization` header with new token.

#### Delete Account (Withdraw)
```http
DELETE /api/member/mypage
Authorization: Bearer {access_token}
```

#### Find ID
```http
POST /api/member/find-id?email={email}
```

#### Find Password
```http
POST /api/member/find-password?arg0={email}&arg1={username}
```

#### Reset Password
```http
POST /api/member/reset-password?arg0={email}&arg1={username}&arg2={inputCode}&arg3={verificationCode}&arg4={newPassword}&arg5={confirmPassword}
```

#### Add Interest Location
```http
POST /api/member/location
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "locations": ["서울특별시 강남구", "경기도 성남시"]
}
```

#### Remove Interest Location
```http
DELETE /api/member/location?locations=서울특별시 강남구&locations=경기도 성남시
Authorization: Bearer {access_token}
```

---

### Subscription Information (`/api/info`) — InfoController

#### List Subscriptions (Paginated)
```http
GET /api/info/subscription?page=0&size=10&sort=rceptEndde
```

#### Get Subscription Detail
```http
GET /api/info/subscription/{id}
```

#### List Subscriptions by Region
```http
GET /api/info/subscription/list?region=서울특별시&city=강남구
```

#### Get Infrastructure Info
```http
GET /api/info/subscription/{id}/detail/infra
```

#### Get Public Facilities
```http
GET /api/info/subscription/{id}/detail/facilities
```

#### Get Region List
```http
GET /api/info/subscription/regionlist
```

#### Get City List by Region
```http
GET /api/info/subscription/citylist?region=서울특별시
```

#### Get My Subscriptions (by Interest Locations)
```http
GET /api/info/subscription/mysubscriptions
Authorization: Bearer {access_token}
```

#### Like Subscription
```http
POST /api/info/subscription/like/{subscriptionId}
Authorization: Bearer {access_token}
```

#### Unlike Subscription
```http
DELETE /api/info/subscription/like/{subscriptionLikeId}
Authorization: Bearer {access_token}
```

#### Get Liked Subscriptions
```http
GET /api/info/subscription/like
Authorization: Bearer {access_token}
```

#### Check If Subscription Is Liked
```http
GET /api/info/subscription/islike?id={subscriptionId}
Authorization: Bearer {access_token}
```

#### Get Upcoming Liked Subscriptions
```http
GET /api/info/subscription/like/upcoming
Authorization: Bearer {access_token}
```

#### Get Closing Liked Subscriptions
```http
GET /api/info/subscription/like/closing
Authorization: Bearer {access_token}
```

#### Get Subscriptions by Month
```http
GET /api/info/subscription/bymonth?year=2024&month=6
```

#### Get Price Summary (by District)
```http
GET /api/info/subscription/PriceSummary?region=서울특별시&city=강남구&umdNm=대치동
```

#### Get Price Summary (by Region)
```http
GET /api/info/subscription/PriceSummary/Region?region=서울특별시&city=강남구
```

#### Get Popular Subscription
```http
GET /api/info/subscription/popular
```

---

### Community (`/api/community`) — CommunityController

#### Create Post
```http
POST /api/community/posts
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "title": "강남구 청약 질문",
    "content": "강남구 신규 분양 아파트 청약 자격 조건이 궁금합니다.",
    "category": "SUBSCRIPTION_INQUIRY"
}
```

#### Get Posts (Paginated)
```http
GET /api/community/posts?sort=time&page=0&size=10
```

#### Get Post Detail
```http
GET /api/community/post/{id}
Authorization: Bearer {access_token} (optional)
```

#### Update Post
```http
PATCH /api/community/post/{id}
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "title": "수정된 제목",
    "content": "수정된 내용"
}
```

#### Delete Post
```http
DELETE /api/community/post/{id}
Authorization: Bearer {access_token}
```

#### Like Post
```http
POST /api/community/post/like/{id}
Authorization: Bearer {access_token} (optional)
```

#### Dislike Post
```http
POST /api/community/post/dislike/{id}
Authorization: Bearer {access_token} (optional)
```

#### Get My Posts
```http
GET /api/community/post/my?sort=time&page=0&size=10
Authorization: Bearer {access_token}
```

#### Add Comment to Post
```http
POST /api/community/comment/{postId}
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "content": "댓글 내용"
}
```

#### Add Reply to Comment
```http
POST /api/community/comment/comment/{commentId}
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "content": "대댓글 내용"
}
```

---

### Home / Main (`/api/main`) — HomeController

#### Get Popular Locations
```http
GET /api/main/popular-locations
```

#### Get My Interest Locations
```http
GET /api/main/my-locations
Authorization: Bearer {access_token}
```

#### Get Popular Content (Posts)
```http
GET /api/main/popular-content
```

---

### News (`/api/news`) — NewsController

#### Manual News Crawl (Admin Only)
```http
POST /api/news/crawl
Authorization: Bearer {admin_access_token}
```
Requires `ADMIN` role.

---

### Admin Data Management (`/api/data`) — DataController

All endpoints require `ADMIN` role.

#### Fetch Apartment Subscription Data
```http
GET /api/data/subscription/apartment
Authorization: Bearer {admin_access_token}
```

#### Update All Coordinates
```http
PUT /api/data/updateAllCoordinates
Authorization: Bearer {admin_access_token}
```

#### Fetch Mortgage Data
```http
GET /api/data/mortgage
Authorization: Bearer {admin_access_token}
```

#### Fetch House Loan Data
```http
GET /api/data/hosueloan
Authorization: Bearer {admin_access_token}
```

#### Refresh Real Estate Price Data
```http
POST /api/data/refresh?yyyyMM=202406
Authorization: Bearer {admin_access_token}
```

---

## WebSocket API

### Real-time AI Chat

#### Connection
```
ws://localhost:8080/ws/chat
```

JWT token is validated during handshake via `JwtHandshakeInterceptor`.

```javascript
const socket = new WebSocket(`ws://localhost:8080/ws/chat?token=${token}`);
```

#### Message Format
Send a text message directly (plain text, not JSON):
```
강남구 청약 자격 조건이 궁금해요
```

The server responds with AI-generated text via Gemini 2.0.

#### Usage Limits
- **Daily Limit**: 15 messages per user (resets at 00:00 UTC)
- **Concurrent Sessions**: One session per user
- **Auto-summarization**: Conversation is summarized after 5+ messages

---

## Response Format

### Success Response
```json
{
    "status": "success",
    "data": { ... }
}
```

### Error Response
```json
{
    "status": "fail",
    "data": {
        "code": "AUTH001",
        "message": "유효하지 않은 JWT 서명"
    }
}
```

For the complete list of error codes, see [Error Codes](./error-codes.md).

---

**Last Updated**: 2026-03-05
**Base URL**: `http://localhost:8080/api`
