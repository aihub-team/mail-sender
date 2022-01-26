package kr.or.aihub.mailsender.domain.mail.transactional.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class TemplateSendRequest {

    @NotEmpty
    private MultipartFile userListFile;

    @NotEmpty
    private String publishName;

    public TemplateSendRequest(MultipartFile userListFile, String publishName) {
        this.userListFile = userListFile;
        this.publishName = publishName;
    }
}