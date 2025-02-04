package org.cheonyakplanet.be.presentation.controller;

import lombok.RequiredArgsConstructor;
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

    /**
     * 게시글 작성
     * @param payload
     * @return
     */
    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody Map<String, String> payload) {
        String title = payload.get("title");
        String content = payload.get("content");
        return ResponseEntity.ok(communityService.createPost(title, content));
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(communityService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable("id") Long id) {
        return ResponseEntity.ok(communityService.getPostById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        communityService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable("id") Long id) {
        communityService.likePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/dislike")
    public ResponseEntity<Void> dislikePost(@PathVariable("id") Long id) {
        communityService.dislikePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Comment> addComment(@PathVariable("postId") Long postId, @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        return ResponseEntity.ok(communityService.addComment(postId, content));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(communityService.getCommentsByPostId(postId));
    }

    @PostMapping("/{commentId}")
    public ResponseEntity<Reply> addReply(@PathVariable("commentId") Long commentId, @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        return ResponseEntity.ok(communityService.addReply(commentId, content));
    }
}
