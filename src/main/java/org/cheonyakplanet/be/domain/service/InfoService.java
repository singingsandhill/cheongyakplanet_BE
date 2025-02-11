package org.cheonyakplanet.be.domain.service;

import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.domain.entity.SubscriptionInfo;
import org.cheonyakplanet.be.domain.repository.SggCodeRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionInfoRepository;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final SubscriptionInfoRepository subscriptionInfoRepository;
    private final SggCodeRepository sggCodeRepository;

    /**
     * 단일 청약 정보를 조회
     */
    public Optional<SubscriptionInfo> getSubscriptionById(Long id) {
        Optional<SubscriptionInfo> result = subscriptionInfoRepository.findById(id);

        if (result.isPresent()) {
            return result;
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
            return new CustomException(ErrorCode.INFO002,"해당 지역의 청약건 없음");
        } else{
            return result;
        }
    }

    public List<String> getReginList(){
        List<String> result = sggCodeRepository.findAllRegions();

        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.INFO005,"지역 테이블 없음 DB 확인");
        }
        return result;
    }

    public List<String> getCityList(String region){
        List<String> result = sggCodeRepository.findAllCities(region);

        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.INFO005,"지역 테이블 없음 DB 확인");
        }
        return result;
    }

}
