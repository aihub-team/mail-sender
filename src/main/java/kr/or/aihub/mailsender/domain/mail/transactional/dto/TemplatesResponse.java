package kr.or.aihub.mailsender.domain.mail.transactional.dto;

import lombok.Getter;

@Getter
public class TemplatesResponse {
    private final String publishName;

    public TemplatesResponse(String publishName) {
        this.publishName = publishName;
    }
}
