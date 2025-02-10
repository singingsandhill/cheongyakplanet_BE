package org.cheonyakplanet.be.domain.service;

import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.domain.entity.SubscriptionInfo;
import org.cheonyakplanet.be.domain.repository.SubscriptionInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final SubscriptionInfoRepository subscriptionInfoRepository;

    /**
     * 단일 청약 정보를 조회
     */
    public Optional<SubscriptionInfo> getSubscriptionById(Long id) {
        return subscriptionInfoRepository.findById(id);
    }

    /**
     * 특정 지역의 청약 목록을 조회
     */
    public List<SubscriptionInfo> getSubscriptionsByRegion(String city, String district) {
        return subscriptionInfoRepository.findByCityAndDistrict(city, district);
    }

}
