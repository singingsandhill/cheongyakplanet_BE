package org.cheonyakplanet.be.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cheonyakplanet.be.application.dto.CoordinateResponseDTO;
import org.cheonyakplanet.be.application.dto.SubscriptionDTO;
import org.cheonyakplanet.be.application.dto.SubscriptionDetailDTO;
import org.cheonyakplanet.be.domain.entity.SubscriptionInfo;
import org.cheonyakplanet.be.domain.entity.User;
import org.cheonyakplanet.be.domain.repository.SggCodeRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionInfoRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoService {

    private final SubscriptionInfoRepository subscriptionInfoRepository;
    private final SggCodeRepository sggCodeRepository;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.latitude.url}")
    private String kakaoLatitudeUrl;

    /**
     * 단일 청약 정보를 조회
     */
    public List<SubscriptionDetailDTO> getSubscriptionById(Long id) {
        Optional<SubscriptionInfo> result = subscriptionInfoRepository.findById(id);

        if (result.isPresent()) {
            return result.stream().map(SubscriptionDetailDTO::fromEntity).collect(Collectors.toList());
        } else {
            throw new CustomException(ErrorCode.INFO001, "해당 아이디의 청약건 없음");
        }
    }

    /**
     * 특정 지역의 청약 목록을 조회
     */
    public Object getSubscriptionsByRegion(String city, String district) {
        List<SubscriptionInfo> result = subscriptionInfoRepository.findByRegionAndCity(city, district);

        if (result.isEmpty()) {
            return new CustomException(ErrorCode.INFO002, "해당 지역의 청약건 없음");
        } else {
            return result;
        }
    }

    public List<String> getReginList() {
        List<String> result = sggCodeRepository.findAllRegions();

        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.INFO005, "지역 테이블 없음 DB 확인");
        }
        return result;
    }

    public List<String> getCityList(String region) {
        List<String> result = sggCodeRepository.findAllCities(region);

        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.INFO005, "지역 테이블 없음 DB 확인");
        }
        return result;
    }

    /**
     * 청약 리스트
     * 마김일 순으로 정렬, 간단 정보만 제공
     *
     * @param page
     * @param size
     * @return
     */
    public Object getSubscriptions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rceptEndde").descending());

        Page<SubscriptionInfo> result = subscriptionInfoRepository.findAll(pageable);
        List<SubscriptionDTO> subscriptionDTOList = result.stream()
                .map(SubscriptionDTO::fromEntity).collect(Collectors.toList());
        return subscriptionDTOList;
    }

    public Object getMySubscriptions(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        List<String> interests = Arrays.asList(
                user.getInterestLocal1(),
                user.getInterestLocal2(),
                user.getInterestLocal3(),
                user.getInterestLocal4(),
                user.getInterestLocal5()
        );

        // 각 관심지역 문자열을 파싱하여 SubscriptionInfo를 조회한 후 합칩니다.
        List<SubscriptionInfo> subscriptions = interests.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .flatMap(interestLocal -> {
                    String[] parts = interestLocal.split(" ");
                    if (parts.length < 2) {
                        throw new IllegalArgumentException("관심지역 형식이 올바르지 않습니다: " + interestLocal);
                    }
                    log.error("지역 : "+parts[0].trim()+"도시 : "+parts[1].trim());
                    return subscriptionInfoRepository
                            .findByRegionAndCity(parts[0].trim(), parts[1].trim())
                            .stream();
                })
                .collect(Collectors.toList());

        return subscriptions.stream()
                .map(SubscriptionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * TODO : 조회할 떄마다 호출하는게 아닌 DB 테이블저장으로 리팩토링
     * @param id
     * @return
     */
    public Object getSubscriptionAddr(Long id) {
        Optional<SubscriptionInfo> result = subscriptionInfoRepository.findById(id);
        String addr = result.get().getHssplyAdres();

        try {
//            // 2) 주소 인코딩
//            String encodedAddr = URLEncoder.encode(addr, StandardCharsets.UTF_8);

            // 3) WebClient 생성
            WebClient webClient = WebClient.builder()
                    .baseUrl("https://dapi.kakao.com")
                    .defaultHeader("Authorization", "KakaoAK " + kakaoRestApiKey)
                    .build();

            Mono<String> responseMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", addr)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(String.class);

            // 5) 응답을 수신(block)하고 JSON 파싱
            String responseBody = responseMono.block();

            if (responseBody != null) {
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONArray documents = jsonObject.getJSONArray("documents");

                if (documents.length() > 0) {
                    JSONObject firstDoc = documents.getJSONObject(0);
                    String x = firstDoc.getString("x");
                    String y = firstDoc.getString("y");

                    return new CoordinateResponseDTO(x, y);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 에러 처리
        }

        return null;
    }

}
