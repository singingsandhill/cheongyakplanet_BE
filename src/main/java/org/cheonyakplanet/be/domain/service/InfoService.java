package org.cheonyakplanet.be.domain.service;

import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.application.dto.SubscriptionDTO;
import org.cheonyakplanet.be.application.dto.SubscriptionDetailDTO;
import org.cheonyakplanet.be.domain.entity.SubscriptionInfo;
import org.cheonyakplanet.be.domain.repository.SggCodeRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionInfoRepository;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final SubscriptionInfoRepository subscriptionInfoRepository;
    private final SggCodeRepository sggCodeRepository;

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

    /**
     * 청약 리스트
     * 마김일 순으로 정렬, 간단 정보만 제공
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
}
