package edu.team5.finalproject.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.SQLDelete;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import edu.team5.finalproject.entity.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE users SET user_deleted = true WHERE user_id = ?")
@NoArgsConstructor
@Table(name="users", indexes = {@Index(name = "idx_email", columnList = "user_email")})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name="user_email", nullable = false, unique = true)
    private String email;

    @Column(name="user_password", nullable = false)
    private String password;

    @Column(name="user_deleted")
    private Boolean deleted;

    @Enumerated(STRING)
    @Column(name="user_role")
    private Role role;


}
