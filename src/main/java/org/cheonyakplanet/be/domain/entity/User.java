package org.cheonyakplanet.be.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cheonyakplanet.be.domain.Stamped;

@Entity
@Getter @Setter
@Table(catalog = "planet",name = "user_info")
@NoArgsConstructor
public class User extends Stamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false)
    private String username;

    @Column(name = "interest_local_1")
    private String interestLocal1;
    @Column(name = "interest_local_2")
    private String interestLocal2;
    @Column(name = "interest_local_3")
    private String interestLocal3;
    @Column(name = "interest_local_4")
    private String interestLocal4;
    @Column(name = "interest_local_5")
    private String interestLocal5;

    @Column
    private Double property;

    @Column
    private Integer income;

    @Column(name = "is_married")
    private Boolean isMarried;

    @Column(name = "num_child")
    private Integer numChild;

    @Column(name = "num_House")
    private Integer numHouse;

    private String status;

    public User(String email, String password, UserRoleEnum role, String username) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.username = username;
    }

}
