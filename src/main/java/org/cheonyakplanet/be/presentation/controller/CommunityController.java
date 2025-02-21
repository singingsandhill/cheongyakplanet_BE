package org.cheonyakplanet.be.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.community.CommentDTO;
import org.cheonyakplanet.be.application.dto.community.PostDTO;
import org.cheonyakplanet.be.domain.entity.Post;
import org.cheonyakplanet.be.domain.service.CommunityService;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/community")
public class CommunityController {
    private final CommunityService communityService;


    @PostMapping("/posts")
    @Operation(summary = "게시글 작성")
    public ResponseEntity<Post> createPost(@RequestBody PostDTO postDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(communityService.createPost(postDTO, userDetails));
    }

    @GetMapping("/posts")
    @Operation(summary = "모든 게시글 조회 (정렬 기준: time, views, likes; 페이징 기능 포함)")
    public ResponseEntity<?> getAllPosts(
            @RequestParam(value = "sort", defaultValue = "time") String sort,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(communityService.getAllPosts(sort, page-1, size));
    }

    @GetMapping("/post/{id}")
    @Operation(summary = "게시글 한 건 조회")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id) {
        return ResponseEntity.ok(communityService.getPostById(id));
    }

    @DeleteMapping("/post/{id}")
    @Operation(summary = "게시글 삭제")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        communityService.deletePost(id, userDetails);
        return ResponseEntity.ok(new ApiResponse<>("success","게시글 삭제 완료"));
    }

    @PostMapping("/post/like/{id}")
    @Operation(summary = "게시글 좋아요")
    public ResponseEntity<?> likePost(@PathVariable("id") Long id) {
        communityService.likePost(id);
        return ResponseEntity.ok(new ApiResponse<>("success","좋아요 +1"));
    }

    @PostMapping("/post/dislike/{id}")
    @Operation(summary = "게시글 싫어요")
    public ResponseEntity<?> dislikePost(@PathVariable("id") Long id) {
        communityService.dislikePost(id);
        return ResponseEntity.ok(new ApiResponse<>("success","싫어요 +1"));
    }

    @PostMapping("/comment/{postId}")
    @Operation(summary = "게시글에 댓글 작성")
    public ResponseEntity<?> addComment(@PathVariable("postId") Long postId, @RequestBody CommentDTO commentDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        communityService.addComment(postId, commentDTO,userDetails);
        return ResponseEntity.ok(new ApiResponse<>("success",commentDTO));
    }

    @PostMapping("/comment/comment/{commentId}")
    @Operation(summary = "게시글의 댓글에 답글 작성")
    public ResponseEntity<?> addReply(@PathVariable("commentId") Long commentId, @RequestBody CommentDTO commentDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new ApiResponse<>("success",communityService.addReply(commentId, commentDTO,userDetails)));
    }
}
