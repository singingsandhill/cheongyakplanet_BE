package org.cheonyakplanet.be.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(catalog = "planet", name = "user_token")
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date accessTokenExpiry;

    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date refreshTokenExpiry;

    public UserToken(String email, String accessToken, String refreshToken, Date accessTokenExpiry, Date refreshTokenExpiry) {
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiry = (accessTokenExpiry != null) ? accessTokenExpiry : new Date(System.currentTimeMillis() + 60 * 60 * 1000L);
        this.refreshTokenExpiry = (refreshTokenExpiry != null) ? refreshTokenExpiry : new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L);
    }

    public void updateTokens(String accessToken, String refreshToken, Date accessTokenExpiry, Date refreshTokenExpiry) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiry = (accessTokenExpiry != null) ? accessTokenExpiry : new Date(System.currentTimeMillis() + 60 * 60 * 1000L);
        this.refreshTokenExpiry = (refreshTokenExpiry != null) ? refreshTokenExpiry : new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L);
    }
}
