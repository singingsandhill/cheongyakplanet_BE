package org.cheonyakplanet.be.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(catalog = "planet", name = "Post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long uuid;

    private String username;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Long views; // 조회수

    private int likes; // 추천 수

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Builder.Default
    private boolean isBlind = false;

    public void incrementLikes() {
        this.likes++;
    }
    public void decrementLikes() {
        this.likes--;
    }

    public void countViews() {
        this.views = (this.views == null ? 1L : this.views + 1);
    }

}
