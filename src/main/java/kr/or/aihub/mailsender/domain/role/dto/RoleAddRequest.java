package kr.or.aihub.mailsender.domain.role.dto;

import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import lombok.Getter;

@Getter
public class RoleAddRequest {

    private RoleType roleType;

    protected RoleAddRequest() {
    }

    public RoleAddRequest(RoleType roleType) {
        this.roleType = roleType;
    }
}
