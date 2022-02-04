package kr.or.aihub.mailsender.domain.mail.transactional.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TemplateSendRequest {

    private MultipartFile userListFile;

    private String publishName;

    public TemplateSendRequest(MultipartFile userListFile, String publishName) {
        this.userListFile = userListFile;
        this.publishName = publishName;
    }
}
