# External API Integration Documentation

## 🌐 Overview

CheonYakPlanet integrates with multiple Korean government and commercial APIs to provide comprehensive real estate information. This document details all external API integrations, their purposes, and implementation details.

## 🏛️ Government APIs

### 1. LH Corporation API (한국토지주택공사)

#### Purpose
Retrieve Korean public housing subscription information directly from the government housing authority.

#### API Details
- **Provider**: Korea Land & Housing Corporation (LH)
- **Base URL**: `https://api.odcloud.kr/api/ApplyhomeInfoDetailSvc/v1`
- **Authentication**: Service Key (API Key)
- **Rate Limits**: 1000 requests/day
- **Data Format**: JSON

#### Endpoints Used

##### Get Apartment Subscription Details
```http
GET /getAPTLttotPblancDetail
```

**Parameters:**
- `serviceKey`: API authentication key
- `page`: Page number (default: 1)
- `perPage`: Records per page (default: 50, max: 100)
- `HOUSE_MANAGE_NO`: House management number (optional)
- `PBLANC_NO`: Publication number (optional)

**Response Fields:**
```json
{
    "currentCount": 1,
    "data": [
        {
            "HOUSE_MANAGE_NO": "2024000001",
            "PBLANC_NO": "2024강남01",
            "HOUSE_NM": "래미안 강남포레스트",
            "HSSPLY_ADRES": "서울특별시 강남구 대치동",
            "TOT_SUPLY_HSHLDCO": "100",
            "RCRIT_PBLANC_DE": "2024-06-19",
            "RCEPT_BGNDE": "2024-07-01",
            "RCEPT_ENDDE": "2024-07-03",
            "SPSPLY_RCEPT_BGNDE": "2024-06-25",
            "SPSPLY_RCEPT_ENDDE": "2024-06-27"
        }
    ],
    "matchCount": 1,
    "page": 1,
    "perPage": 50,
    "totalCount": 1
}
```

#### Implementation Details

**Service Integration:**
```java
@Service
public class SubscriptionService {
    
    @Value("${sub.apt.api.url}")
    private String subAptApiUrl;
    
    @Value("${sub.apt.api.key}")
    private String apiKey;
    
    public String updateSubAPT() {
        String requestUrl = subAptApiUrl + "?page=1&perPage=50&serviceKey=" + apiKey;
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                requestUrl, HttpMethod.GET, null, String.class);
            
            // Process response and save to database
            processSubscriptionData(response.getBody());
            
        } catch (Exception e) {
            log.error("LH API call failed: {}", e.getMessage());
            return "API 호출 실패: " + e.getMessage();
        }
    }
}
```

**Error Handling:**
- Network timeouts: Retry with exponential backoff
- Rate limiting: Respect daily limits with usage tracking
- Invalid responses: Log and skip problematic records
- Authentication failures: Alert administrators

### 2. Ministry of Land API (국토교통부)

#### Purpose
Retrieve real estate transaction price data for market analysis and price comparison.

#### API Details
- **Provider**: Ministry of Land, Infrastructure and Transport
- **Base URL**: `https://apis.data.go.kr/1613000/RTMSDataSvcAptTrade`
- **Authentication**: Service Key
- **Rate Limits**: 1000 requests/day
- **Data Format**: XML/JSON

#### Endpoints Used

##### Get Apartment Transaction Data
```http
GET /getRTMSDataSvcAptTrade
```

**Parameters:**
- `serviceKey`: API authentication key
- `LAWD_CD`: Administrative district code (시군구 코드)
- `DEAL_YMD`: Transaction year and month (YYYYMM)
- `numOfRows`: Number of records (default: 50)
- `pageNo`: Page number

**Response Example:**
```xml
<response>
    <body>
        <items>
            <item>
                <거래금액>120,000</거래금액>
                <건축년도>2020</건축년도>
                <년>2024</년>
                <법정동>대치동</법정동>
                <아파트>래미안</아파트>
                <월>06</월>
                <일>15</일>
                <전용면적>84.93</전용면적>
                <지번>123-1</지번>
                <층>15</층>
            </item>
        </items>
    </body>
</response>
```

#### Implementation Details

**Async Processing:**
```java
@Async
@Retryable(value = Exception.class, maxAttempts = 3)
public CompletableFuture<Void> collectRealPriceData(String yyyyMM) {
    List<SggCode> codes = sggCodeRepository.findAll();
    
    List<CompletableFuture<Void>> futures = codes.stream()
        .map(code -> CompletableFuture.runAsync(() -> 
            processCodeData(code, yyyyMM), taskExecutor))
        .collect(Collectors.toList());
    
    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
}
```

## 🗺️ Commercial APIs

### 3. Kakao Maps API

#### Purpose
Convert Korean addresses to geographic coordinates and provide location-based services.

#### API Details
- **Provider**: Kakao Corp.
- **Base URL**: `https://dapi.kakao.com`
- **Authentication**: REST API Key
- **Rate Limits**: 300,000 requests/day
- **Data Format**: JSON

#### Endpoints Used

##### Address Search (Geocoding)
```http
GET /v2/local/search/address.json
```

**Headers:**
```http
Authorization: KakaoAK {REST_API_KEY}
```

**Parameters:**
- `query`: Address to search (Korean)
- `analyze_type`: Analysis type (similar, exact)
- `page`: Page number
- `size`: Result count per page (1-30)

**Response Example:**
```json
{
    "meta": {
        "total_count": 1,
        "pageable_count": 1,
        "is_end": true
    },
    "documents": [
        {
            "address_name": "서울 강남구 대치동 944-32",
            "y": "37.494870",
            "x": "127.062583",
            "address_type": "REGION_ADDR",
            "address": {
                "address_name": "서울 강남구 대치동 944-32",
                "region_1depth_name": "서울",
                "region_2depth_name": "강남구",
                "region_3depth_name": "대치동",
                "mountain_yn": "N",
                "main_address_no": "944",
                "sub_address_no": "32"
            }
        }
    ]
}
```

#### Implementation Details

Geocoding is handled directly within `SubscriptionService` using `RestTemplate` to call the Kakao address search API. Coordinates are stored in `SubscriptionInfo` entities.

The admin endpoint `PUT /api/data/updateAllCoordinates` triggers bulk coordinate updates for all subscriptions.

### 4. Naver News API

#### Purpose
Aggregate Korean real estate news for market intelligence and user information.

#### API Details
- **Provider**: Naver Corp.
- **Base URL**: `https://openapi.naver.com/v1/search`
- **Authentication**: Client ID & Client Secret
- **Rate Limits**: 25,000 requests/day
- **Data Format**: JSON

#### Endpoints Used

##### News Search
```http
GET /news.json
```

**Headers:**
```http
X-Naver-Client-Id: {CLIENT_ID}
X-Naver-Client-Secret: {CLIENT_SECRET}
```

**Parameters:**
- `query`: Search keyword
- `display`: Number of results (1-100)
- `start`: Start position (1-1000)
- `sort`: Sort order (sim, date)

**Response Example:**
```json
{
    "lastBuildDate": "Mon, 19 Jun 2024 17:41:29 +0900",
    "total": 8420,
    "start": 1,
    "display": 10,
    "items": [
        {
            "title": "[부동산] 강남구 아파트 청약 경쟁률 상승",
            "originallink": "https://example.com/news/123",
            "link": "https://news.naver.com/main/read.nhn?mode=...",
            "description": "서울 강남구 아파트 청약 경쟁률이 급상승하고 있다...",
            "pubDate": "Mon, 19 Jun 2024 16:30:00 +0900"
        }
    ]
}
```

#### Implementation Details

**Crawling Keywords:**
- 부동산 정책, 주택 청약, 청약 당첨, 분양, 아파트 청약
- 주택공급, LH청약, SH청약, 공공분양, 민간분양
- 부동산, 주택, 아파트, LH, 부동산정책, 주택정책
- 임대차, 전월세, 재건축, 국토교통부, 가계대출, 주담대, 시세, 매매

**Automated Schedule:**
- **Cron**: `0 30 0 * * ?` (매일 00:30 KST)
- Defined in `Scheduler.java` (`dailyNewsUpdate()`)

**Post Categorization:**
- 청약/분양 관련 → `SUBSCRIPTION_INFO` category
- 정책/규제/기타 → `INFO_SHARE` category

**Deduplication:**
- 24시간 이내 동일 제목의 포스트가 있으면 생성하지 않음
- 제목의 첫 20자 기준으로 중복 검사

**System User:** 뉴스 포스트는 `jjsus0307@gmail.com` 계정으로 생성됩니다.

**Manual Execution:**
```http
POST /api/news/crawl
Authorization: Bearer {admin_access_token}
```
Requires `ADMIN` role.

**Environment Variables:**
```bash
NAVER_CLIENT_ID=your_naver_client_id
NAVER_CLIENT_SECRET=your_naver_client_secret
```

### 5. Google Gemini API

#### Purpose
Provide AI-powered conversational assistance for Korean real estate questions.

#### API Details
- **Provider**: Google AI
- **Base URL**: `https://generativelanguage.googleapis.com`
- **Authentication**: API Key
- **Rate Limits**: Configurable quotas
- **Data Format**: JSON

#### Endpoints Used

##### Generate Content
```http
POST /v1/models/gemini-pro:generateContent
```

**Headers:**
```http
Content-Type: application/json
Authorization: Bearer {API_KEY}
```

**Request Example:**
```json
{
    "contents": [
        {
            "parts": [
                {
                    "text": "강남구 청약 자격 조건에 대해 설명해주세요."
                }
            ]
        }
    ],
    "generationConfig": {
        "temperature": 0.7,
        "maxOutputTokens": 1000
    }
}
```

**Response Example:**
```json
{
    "candidates": [
        {
            "content": {
                "parts": [
                    {
                        "text": "강남구 청약 자격 조건은 다음과 같습니다:\n\n1. 청약통장 가입 기간\n2. 소득 및 자산 기준\n3. 거주 지역 요건\n4. 주택 보유 현황\n\n각 조건에 대해 자세히 설명드리겠습니다..."
                    }
                ]
            },
            "finishReason": "STOP"
        }
    ]
}
```

#### Implementation Details

**WebSocket Integration:**
```java
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Initialize chat session with Gemini context
        ChatSession chatSession = new ChatSession();
        chatSession.addMessage("system", "You are a Korean real estate expert...");
        sessionManager.addSession(session.getId(), chatSession);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Process user message and get Gemini response
        String response = geminiClient.generateResponse(message.getPayload());
        session.sendMessage(new TextMessage(response));
    }
}
```

## Recommended Integration Patterns (Not Yet Implemented)

The following patterns are recommended for production hardening but are **not currently implemented** in the codebase.

### Circuit Breaker

Use Resilience4j `CircuitBreaker` to handle cascading failures from external APIs.

### Rate Limiting

Use Guava `RateLimiter` or similar to throttle outgoing API calls and respect provider limits.

### Caching

Use Spring `@Cacheable` for frequently accessed external API responses.

### Health Checks & Monitoring

Implement Spring Boot Actuator `HealthIndicator` for external API status monitoring.

## 📋 Configuration

### Application Properties

```properties
# LH Corporation API
sub.apt.api.url=https://api.odcloud.kr/api/ApplyhomeInfoDetailSvc/v1/getAPTLttotPblancDetail
sub.apt.api.key=${LH_API_KEY}

# Ministry of Land API
realestate.api.url=https://apis.data.go.kr/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade
realestate.api.key=${REALESTATE_API_KEY}

# Kakao API
kakao.api.url=https://dapi.kakao.com
kakao.api.key=${KAKAO_API_KEY}

# Naver News API
naver.client.id=${NAVER_CLIENT_ID}
naver.client.secret=${NAVER_CLIENT_SECRET}

# Gemini API
gemini.api.url=https://generativelanguage.googleapis.com
gemini.api.key=${GEMINI_API_KEY}

# API Settings
api.timeout.connect=5000
api.timeout.read=30000
api.retry.max-attempts=3
api.retry.delay=1000
```

### Environment-specific Settings

**Development:**
```properties
api.timeout.read=10000
api.retry.max-attempts=1
logging.level.org.springframework.web.client=DEBUG
```

**Production:**
```properties
api.timeout.read=30000
api.retry.max-attempts=3
api.circuit-breaker.enabled=true
```

---

**Last Updated**: 2026-03-05
**Total Integrations**: 5 APIs