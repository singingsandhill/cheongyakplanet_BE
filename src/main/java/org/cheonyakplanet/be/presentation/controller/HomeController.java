package org.cheonyakplanet.be.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.domain.entity.Post;
import org.cheonyakplanet.be.domain.service.CommunityService;
import org.cheonyakplanet.be.domain.service.SubscriptionService;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class HomeController {

    private final SubscriptionService subscriptionService;
    private final CommunityService communityService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        try {
            log.info("HomeController.home() called");
            log.info("UserDetails: {}", userDetails);

            if (userDetails != null) {
                model.addAttribute("username", userDetails.getUsername());
                log.info("Username added to model: {}", userDetails.getUsername());
            } else {
                log.info("No user details available");
            }

            return "index";

        } catch (Exception e) {
            log.error("Error in home controller: ", e);
            throw new CustomException(ErrorCode.UNKNOWN_ERROR, "Error loading home page: " + e.getMessage());
        }
    }

    @GetMapping("/popular-locations")
    @Operation(summary = "가장 인기 있는 지역", description = "")
    public ResponseEntity<?> getPopularLocations() {
        Object result = subscriptionService.getPopularLocationList();
        return ResponseEntity.ok( new ApiResponse<>("sucess",result));
    }

    @GetMapping("/my-locations")
    @Operation(summary = "내 관심 지역")
    public ResponseEntity<?> getMyLocations(@AuthenticationPrincipal UserDetailsImpl userDetails,HttpServletRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("sucess",subscriptionService.getInterestLocalsByEmail(userDetails,request)));
    }

    @GetMapping("/popular-content")
    @Operation(summary = "인기있는 게시글")
    public ResponseEntity<?> getPopularPosts() {
        List<Post> posts = communityService.getPopularPosts();
        return ResponseEntity.ok(new ApiResponse<>("sucess",posts));
    }
}
