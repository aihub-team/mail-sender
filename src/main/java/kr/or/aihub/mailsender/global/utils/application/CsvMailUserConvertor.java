package kr.or.aihub.mailsender.global.utils.application;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import kr.or.aihub.mailsender.domain.mail.transactional.domain.MailUser;
import kr.or.aihub.mailsender.global.utils.errors.FileParsingException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class CsvMailUserConvertor {

    public List<MailUser> convert(MultipartFile userListFile) throws FileParsingException {
        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(userListFile.getInputStream()));
        ) {
            CsvToBean<MailUser> bean = new CsvToBeanBuilder(bufferedReader)
                    .withType(MailUser.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return bean.parse();
        } catch (IOException | RuntimeException e) {
            throw new FileParsingException();
        }
    }
}
