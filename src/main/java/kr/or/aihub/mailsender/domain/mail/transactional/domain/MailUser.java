package kr.or.aihub.mailsender.domain.mail.transactional.domain;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MailUser {

    @CsvBindByName
    private String data;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private String belong;

    @CsvBindByName
    private String division;

    @CsvBindByName
    private String email;

    public MailUser() {
    }

    @Builder
    public MailUser(String data, String name, String belong, String division, String email) {
        this.data = data;
        this.name = name;
        this.belong = belong;
        this.division = division;
        this.email = email;
    }
}
