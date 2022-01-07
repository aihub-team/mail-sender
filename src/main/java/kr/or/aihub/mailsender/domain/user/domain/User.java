package kr.or.aihub.mailsender.domain.user.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;

    protected User() {
    }

    @Builder
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
