package org.cheonyakplanet.be.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.application.dto.PostDTO;
import org.cheonyakplanet.be.domain.entity.Comment;
import org.cheonyakplanet.be.domain.entity.Post;
import org.cheonyakplanet.be.domain.entity.Reply;
import org.cheonyakplanet.be.domain.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/community")
public class CommunityController {
    private final CommunityService communityService;


    @PostMapping("/posts")
    @Operation(summary = "게시글 작성")
    public ResponseEntity<Post> createPost(@RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(communityService.createPost(postDTO));
    }

    @GetMapping("/posts")
    @Operation(summary = "모든 게시글 조회")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(communityService.getAllPosts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 한 건 조회")
    public ResponseEntity<Post> getPost(@PathVariable("id") Long id) {
        return ResponseEntity.ok(communityService.getPostById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        communityService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "게시글 좋아요")
    public ResponseEntity<Void> likePost(@PathVariable("id") Long id) {
        communityService.likePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/dislike")
    @Operation(summary = "게시글 싫어요")
    public ResponseEntity<Void> dislikePost(@PathVariable("id") Long id) {
        communityService.dislikePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}")
    @Operation(summary = "게시글에 댓글 작성")
    public ResponseEntity<Comment> addComment(@PathVariable("postId") Long postId, @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        return ResponseEntity.ok(communityService.addComment(postId, content));
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "게시글의 댓글 조회")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(communityService.getCommentsByPostId(postId));
    }

    @PostMapping("/{commentId}")
    @Operation(summary = "게시글의 댓글에 답글 작성")
    public ResponseEntity<Reply> addReply(@PathVariable("commentId") Long commentId, @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        return ResponseEntity.ok(communityService.addReply(commentId, content));
    }
}
