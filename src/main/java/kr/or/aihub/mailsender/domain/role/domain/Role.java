package kr.or.aihub.mailsender.domain.role.domain;

import kr.or.aihub.mailsender.domain.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Entity
public class Role {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    private RoleType type;

    protected Role() {
    }

    @Builder
    public Role(User user, RoleType type) {
        this.user = user;
        this.type = type;
    }

}
