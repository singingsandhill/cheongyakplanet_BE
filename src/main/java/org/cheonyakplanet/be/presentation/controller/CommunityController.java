package org.cheonyakplanet.be.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.CommentDTO;
import org.cheonyakplanet.be.application.dto.PostDTO;
import org.cheonyakplanet.be.domain.entity.Comment;
import org.cheonyakplanet.be.domain.entity.Post;
import org.cheonyakplanet.be.domain.entity.Reply;
import org.cheonyakplanet.be.domain.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/community")
public class CommunityController {
    private final CommunityService communityService;


    @PostMapping("/posts")
    @Operation(summary = "게시글 작성")
    public ResponseEntity<Post> createPost(@RequestBody PostDTO postDTO, HttpServletRequest request) {
        return ResponseEntity.ok(communityService.createPost(postDTO, request));
    }

    @GetMapping("/posts")
    @Operation(summary = "모든 게시글 조회 (정렬 기준: time, views, likes; 페이징 기능 포함)")
    public ResponseEntity<?> getAllPosts(
            @RequestParam(value = "sort", defaultValue = "time") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(communityService.getAllPosts(sort, page, size));
    }

    @GetMapping("/post/{id}")
    @Operation(summary = "게시글 한 건 조회")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id) {
        return ResponseEntity.ok(communityService.getPostById(id));
    }

    @DeleteMapping("/post/{id}")
    @Operation(summary = "게시글 삭제")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id, HttpServletRequest request) {
        communityService.deletePost(id, request);
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
    public ResponseEntity<?> addComment(@PathVariable("postId") Long postId, @RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        communityService.addComment(postId, commentDTO,request);
        return ResponseEntity.ok(new ApiResponse<>("success",commentDTO));
    }

    @PostMapping("/comment/comment/{commentId}")
    @Operation(summary = "게시글의 댓글에 답글 작성")
    public ResponseEntity<?> addReply(@PathVariable("commentId") Long commentId, @RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("success",communityService.addReply(commentId, commentDTO,request)));
    }
}
