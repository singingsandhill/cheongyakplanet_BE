package org.cheonyakplanet.be.domain.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.application.dto.PostDTO;
import org.cheonyakplanet.be.domain.entity.Comment;
import org.cheonyakplanet.be.domain.entity.Post;
import org.cheonyakplanet.be.domain.entity.Reply;
import org.cheonyakplanet.be.domain.entity.User;
import org.cheonyakplanet.be.domain.repository.CommentRepository;
import org.cheonyakplanet.be.domain.repository.PostRepository;
import org.cheonyakplanet.be.domain.repository.ReplyRepository;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public Post createPost(PostDTO postDTO, HttpServletRequest request) {
        // JWT 토큰 추출 및 파싱
        String token = jwtUtil.getTokenFromRequest(request);
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String email = claims.getSubject();

        // User 엔티티에서 username 조회 (User가 존재하지 않으면 예외 발생)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.SIGN005, "사용자를 찾을 수 없습니다."));


        Post post = Post.builder()
                .username(user.getUsername())
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .likes(0)
                .build();
        return postRepository.save(post);
    }

    public void deletePost(Long postId, HttpServletRequest request) {
        // JWT 토큰 추출 및 파싱
        String token = jwtUtil.getTokenFromRequest(request);
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String email = claims.getSubject();

        // User 엔티티에서 username 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.SIGN005, "사용자를 찾을 수 없습니다."));

        // 삭제할 게시글 조회 (존재하지 않으면 예외 발생)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMU001, "해당 게시글이 존재하지 않습니다."));

        // 게시글 작성자와 요청 사용자의 username이 일치하는지 확인
        if (!post.getUsername().equals(user.getUsername())) {
            throw new CustomException(ErrorCode.COMU002, "게시글 삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    /**
     * 게시글 전체 조회 기능 (정렬 기준: time, views, likes, 페이징 포함)
     *
     * @param sort 정렬 기준 (time, views, likes)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 당 항목 수
     * @return 페이징 처리된 PostDTO Page 객체
     */
    public Page<PostDTO> getAllPosts(String sort, int page, int size) {
        Sort sortOrder;
        if ("likes".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by("likes").descending();
        } else if ("views".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "views");
        } else {
            sortOrder = Sort.by(Sort.Direction.ASC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<Post> postPage = postRepository.findAllByDeletedAtIsNull(pageable);

        return postPage.map(post -> {
            PostDTO postDTO = new PostDTO();
            postDTO.setId(post.getId());
            postDTO.setTitle(post.getTitle());
            postDTO.setContent(post.getContent());
            postDTO.setUsername(post.getUsername());
            postDTO.setCreatedAt(post.getCreatedAt());
            return postDTO;
        });

    }

    @Transactional
    public Post getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.COMU001, "해당 게시글 없음"));
        post.countViews();
        return post;
    }

    public List<Post> getPopularPosts() {
        return postRepository.findPostsOrderByLikes(PageRequest.of(0, 5)); // 상위 5개
    }

    /**
     * 게시글 좋아요
     *
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
