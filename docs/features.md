# Feature Documentation

## 🎯 Product Overview

CheonYakPlanet (청약플래닛) is a comprehensive Korean real estate subscription platform that helps users navigate the complex Korean public housing subscription system (청약).

## 🏠 Core Features

### 1. User Management & Authentication

#### 1.1 User Registration & Login
- **Native Registration**: Email-based account creation with password encryption
- **Kakao OAuth Integration**: Social login with Kakao account
- **JWT Authentication**: Stateless authentication with access/refresh token pairs
- **Password Recovery**: Email-based password reset with verification codes

For authentication architecture details, see [Architecture Decisions](./architecture-decisions.md) ADR-002.

#### 1.2 User Profile Management
- **Financial Profile**: Income, assets, debt, property ownership tracking
- **Demographics**: Marriage status, number of children for subscription eligibility
- **Interest Locations**: Up to 5 Korean administrative regions for personalized content
- **Account Status**: Active/Withdrawn status with soft delete support

**Business Rules:**
- Financial data used for subscription eligibility matching
- Interest locations filter subscription opportunities
- Profile completeness affects recommendation accuracy

### 2. Korean Housing Subscription System (청약)

#### 2.1 Subscription Discovery
- **Government Data Integration**: Real-time sync with LH Corporation API
- **Comprehensive Coverage**: All Korean public housing announcements
- **Geographic Organization**: Organized by 시/도, 시/군/구, 읍/면/동 hierarchy
- **Timeline Tracking**: Subscription periods, winner announcements, contract dates

#### 2.2 Subscription Eligibility Matching
- User financial profile vs. subscription requirements
- Geographic preference matching
- Special supply category qualification
- Priority ranking calculation

For Korean business domain terminology and rules (순위제, 특별공급, etc.), see [Korean Business Rules](./korean-business-rules.md).

#### 2.3 Subscription Details
- **Price Information**: Supply prices, payment schedules, deposit requirements
- **Supply Targets**: Unit types, floor plans, household counts
- **Timeline Information**: All important dates in subscription process
- **Developer Information**: Construction companies, contact details

### 3. Location Intelligence

#### 3.1 Infrastructure Analysis
- **Educational Facilities**: Schools (elementary, middle, high school) within 1km
- **Transportation**: Subway stations, bus stops, accessibility analysis
- **Public Facilities**: Government offices, hospitals, parks, cultural centers
- **Distance Calculations**: Precise Haversine formula for geographic distance

**Data Sources:**
- Korean school information database
- Public transportation network data
- Government facility location data
- Kakao Maps API for geocoding

#### 3.2 Geographic Services
- **Address Parsing**: Korean administrative hierarchy parsing
- **Coordinate Mapping**: Lat/Long conversion for all locations
- **Proximity Search**: 1km radius searches with bounding box optimization
- **Regional Analytics**: Popular locations and infrastructure density

### 4. Financial Product Integration

#### 4.1 House Loan Products
- **Bank Integration**: Major Korean bank loan products
- **Rate Comparison**: Min/max/average lending rates
- **Eligibility Matching**: User income vs. loan requirements
- **Product Categories**: Mortgage loans vs. rental house loans

**Data Sources:**
- Financial Supervisory Service (FSS) API
- Major Korean banks (국민은행, 신한은행, etc.)
- Real-time interest rate updates

#### 4.2 Financial Analysis
- **Affordability Calculator**: Income-based loan capacity
- **Rate Trends**: Historical interest rate analysis
- **Recommendation Engine**: Best loan products for user profile

### 5. Market Intelligence & News

#### 5.1 Automated News Aggregation
- **Multi-source Crawling**: Naver News API with 20+ real estate keywords
- **Content Filtering**: Advanced anti-spam and quality filtering
- **Daily Summaries**: Automated daily reports posted to community
- **Schedule**: Daily at 00:30 KST

#### 5.2 Content Categorization
- **SUBSCRIPTION_INFO**: Subscription and pre-sale news
- **INFO_SHARE**: Policy, regulation, and other real estate news

For detailed news setup (keywords, API keys, system user), see [External APIs — Naver News API](./external-apis.md).

### 6. Community Platform

#### 6.1 Discussion System
- **Hierarchical Structure**: Posts → Comments → Replies (3-level)
- **Categories**: Review, Subscription Info, Free Talk, Q&A
- **Reaction System**: Like/Dislike with duplicate prevention
- **View Tracking**: Post popularity and engagement metrics

#### 6.2 Content Management
- **Rich Text Support**: Markdown-style content formatting
- **Moderation**: Content flagging and blind post system
- **Search**: Full-text search across posts and comments
- **Pagination**: Performance-optimized list views

#### 6.3 User Engagement
- **Gamification**: Like counts, view counts, popular posts
- **User Profiles**: Username-based identity system
- **Activity Tracking**: User contribution history

### 7. AI-Powered Chat Assistant

#### 7.1 Real-time Consultation
- **Gemini 2.0 Integration**: Google's latest conversational AI
- **WebSocket Connection**: Real-time bidirectional communication
- **Usage Limits**: 15 messages per user per day
- **Session Management**: Conversation context and memory

#### 7.2 Domain Expertise
- **Korean Real Estate Knowledge**: Trained on subscription system rules
- **Personalized Advice**: Based on user financial profile and preferences
- **Multi-language**: Korean and English support
- **Context Awareness**: Integration with user subscription interests

#### 7.3 Technical Implementation
- **JWT Authentication**: Secure WebSocket handshake
- **Memory Management**: Efficient conversation history handling
- **Rate Limiting**: Anti-abuse mechanisms
- **Fallback Handling**: Graceful degradation for API failures

### 8. Data Processing & Automation

#### 8.1 Scheduled Data Updates
- **Daily Updates**: Government subscription data (1:00 AM KST)
- **Coordinate Sync**: Address geocoding updates (1:15 AM KST)
- **News Processing**: Daily aggregation and summarization (12:30 AM KST)
- **Monthly Updates**: Real estate transaction data (last day, 2:00 AM KST)

#### 8.2 External API Integration
- **Government APIs**: Resilient integration with retry mechanisms
- **Rate Limiting**: Respectful API usage with configurable delays
- **Incremental Processing**: Duplicate prevention and delta updates
- **Error Handling**: Comprehensive logging and fallback mechanisms

#### 8.3 Performance Optimization
- **Async Processing**: Parallel data processing with CompletableFuture
- **Batch Operations**: Bulk database operations for efficiency
- **Caching**: Strategic caching for frequently accessed data
- **Database Optimization**: Proper indexing and query optimization

## 🎮 User Journey

### 1. New User Onboarding
1. **Registration**: Email or Kakao social login
2. **Profile Setup**: Financial information and demographics
3. **Interest Selection**: Choose up to 5 regions of interest
4. **First Recommendations**: Personalized subscription suggestions

### 2. Daily Usage Pattern
1. **Dashboard View**: Latest subscriptions in interest areas
2. **News Consumption**: Daily real estate news summaries
3. **Community Engagement**: Browse posts, ask questions
4. **AI Consultation**: Get personalized subscription advice

### 3. Subscription Discovery
1. **Browse Listings**: Filter by region, date, price
2. **Detailed Analysis**: View subscription details and requirements
3. **Eligibility Check**: Match personal profile against requirements
4. **Infrastructure Review**: Check nearby schools, stations, facilities
5. **Save Favorites**: Like interesting subscriptions for later

### 4. Financial Planning
1. **Loan Exploration**: Browse available loan products
2. **Rate Comparison**: Compare interest rates across banks
3. **Affordability Analysis**: Calculate loan capacity based on income
4. **Application Guidance**: Get directed to bank websites

## Security & Architecture

For security implementation details (JWT, soft delete, CORS, role-based access), see [Architecture Decisions](./architecture-decisions.md).

---

**Last Updated**: 2026-03-05