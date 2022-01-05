package kr.or.aihub.mailsender;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class IndexController {
    @GetMapping
    public ResponseEntity<String> index() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
