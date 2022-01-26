package kr.or.aihub.mailsender.global.config.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static kr.or.aihub.mailsender.domain.role.domain.RoleType.ROLE_ACTIVATE;
import static kr.or.aihub.mailsender.domain.role.domain.RoleType.ROLE_ADMIN;
import static kr.or.aihub.mailsender.domain.role.domain.RoleType.ROLE_DEACTIVATE;

@Retention(RetentionPolicy.RUNTIME)
@WithMockCustomUser(roles = {ROLE_DEACTIVATE, ROLE_ACTIVATE, ROLE_ADMIN})
public @interface WithMockCustomAdminUser {
}