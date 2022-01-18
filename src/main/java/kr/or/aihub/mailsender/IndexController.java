package kr.or.aihub.mailsender;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void index() {
        // TODO: 2022/01/16 메인 페이지 구현
    }
}
