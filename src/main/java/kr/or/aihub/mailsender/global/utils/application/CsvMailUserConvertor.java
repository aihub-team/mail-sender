package kr.or.aihub.mailsender.global.utils.application;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import kr.or.aihub.mailsender.domain.mail.transactional.domain.MailUser;
import kr.or.aihub.mailsender.domain.mail.transactional.errors.NotSupportedFileExtensionException;
import kr.or.aihub.mailsender.global.utils.errors.FileParsingException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class CsvMailUserConvertor {

    public List<MailUser> convert(MultipartFile userListFile) throws FileParsingException {
        checkNull(userListFile);
        checkCsvExtension(userListFile);

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

    private void checkNull(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("file은 비어있을 수 없습니다.");
        }
    }

    private void checkCsvExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        checkNullOrBlank(originalFilename);

        String extension = getExtension(originalFilename)
                .orElseThrow(IllegalArgumentException::new);

        if (!"csv".equals(extension)) {
            throw new NotSupportedFileExtensionException(extension);
        }
    }

    /**
     * 비거나 null인지 확인합니다.
     *
     * @param originalFilename 파일이름
     * @throws IllegalArgumentException 비거나 null인 경우
     */
    private void checkNullOrBlank(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("파일이름은 비어있을 수 없습니다.");
        }
    }

    private Optional<String> getExtension(String originalFilename) {
        return Arrays.stream(originalFilename.split("\\."))
                .skip(1)
                .reduce((first, second) -> second);
    }

}
