package kr.or.aihub.mailsender.domain.role.controller;

import kr.or.aihub.mailsender.domain.role.dto.RoleAddRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/role")
public class RoleController {

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void roleAdd(
            @RequestParam("userId") Long userId,
            @RequestBody @Valid RoleAddRequest addRoleRequest
    ) {
        // TODO: 2022/01/16 서비스 구현
    }
}
