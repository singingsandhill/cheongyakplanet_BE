package org.cheonyakplanet.be.domain.repository;

import org.cheonyakplanet.be.domain.entity.SubscriptionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionInfoRepository extends JpaRepository<SubscriptionInfo, Long> {

    // 특정 지역의 청약 리스트 조회
    List<SubscriptionInfo> findByRegionAndCity(String region, String city);

}
