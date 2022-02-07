package kr.or.aihub.mailsender.domain.role.domain;

public enum RoleType {
    ROLE_DEACTIVATE,
    ROLE_ACTIVATE,
    ROLE_ADMIN;

    public boolean isAdmin() {
        return this.equals(ROLE_ADMIN);
    }
}
