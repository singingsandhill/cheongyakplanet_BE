package org.cheonyakplanet.be.domain.repository;

import org.cheonyakplanet.be.domain.entity.SubscriptionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionInfoRepository extends JpaRepository<SubscriptionInfo, String> {

}
