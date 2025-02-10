package org.cheonyakplanet.be.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.domain.entity.SubscriptionInfo;
import org.cheonyakplanet.be.domain.service.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;

    /**
     * 1건의 청약 정보를 불러오기
     * @return
     */
    @GetMapping("/subscription/{id}")
    public String getSubscription(@PathVariable("id") Long id) {
        Optional<SubscriptionInfo> subscriptionInfo = infoService.getSubscriptionById(id);
        return ResponseEntiy.ok(new ApiResponse( "success",subscriptionInfo));
    }

    /**
     * 청약 물건의 상세 정보를 불러오기
     * @return
     */
    @GetMapping("/subscription/{id}/detail")
    public String getSubscriptionDetail(@PathVariable("id") Long id) {
        return null;
    }

    /**
     * 청약 물건의 주변 인프라를 불러오는 메서드
     * @param id
     * @return
     */
    @GetMapping("/subscription/{id}/detail/infra")
    public String getSubscriptionDetailInfra(@PathVariable("id") Long id) {
        return null;
    }
}
