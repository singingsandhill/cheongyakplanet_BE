package org.cheonyakplanet.be.domain.service;

import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.application.dto.PostDTO;
import org.cheonyakplanet.be.domain.entity.Comment;
import org.cheonyakplanet.be.domain.entity.Post;
import org.cheonyakplanet.be.domain.entity.Reply;
import org.cheonyakplanet.be.domain.repository.CommentRepository;
import org.cheonyakplanet.be.domain.repository.PostRepository;
import org.cheonyakplanet.be.domain.repository.ReplyRepository;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    public Post createPost(PostDTO postDTO) {
        Post post = Post.builder()
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .likes(0)
                .build();
        return postRepository.save(post);
    }

    public void deletePost(Long postId) {
        postRepository.deletePostById(postId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.COMU001,"해당 게시글 없음"));
    }

    public List<Post> getPopularPosts() {
        return postRepository.findPostsOrderByLikes(PageRequest.of(0, 5)); // 상위 5개
    }

    /**
     * 게시글 좋아요
     * @param id
     */
    public void likePost(Long id) {
        Post post = getPostById(id);
        post.incrementLikes();
        postRepository.save(post);
    }

    public void dislikePost(Long id) {
        Post post = getPostById(id);
        post.decrementLikes();
        postRepository.save(post);
    }

    public Comment addComment(Long postId, String content) {
        Post post = getPostById(postId);
        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .build();
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        Post post = getPostById(postId);
        return post.getComments();
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public Reply addReply(Long commentId, String content) {
        Comment comment = getCommentById(commentId);
        Reply reply = Reply.builder()
                .content(content)
                .comment(comment)
                .build();
        return replyRepository.save(reply);
    }
}
