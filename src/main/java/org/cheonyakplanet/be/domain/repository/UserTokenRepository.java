package org.cheonyakplanet.be.domain.repository;

import org.cheonyakplanet.be.domain.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByEmail(String token);

    Optional<UserToken> findByRefreshToken(String token);

    void deleteByEmail(String email);
}
