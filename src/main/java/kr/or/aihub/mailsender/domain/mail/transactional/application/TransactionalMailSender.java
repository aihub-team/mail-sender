package kr.or.aihub.mailsender.domain.mail.transactional.application;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import kr.or.aihub.mailsender.domain.mail.transactional.domain.MailUser;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplateSendRequest;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplateSendResponse;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplatesResponse;
import kr.or.aihub.mailsender.domain.mail.transactional.errors.NotExistPublishNameException;
import kr.or.aihub.mailsender.domain.mail.transactional.errors.NotSupportedFileExtensionException;
import kr.or.aihub.mailsender.global.utils.application.CsvMailUserConvertor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Transactional 메일 발송 담당.
 */
@Service
public class TransactionalMailSender {
    private final MandrillService mandrillService;
    private final CsvMailUserConvertor csvMailUserConvertor;

    public TransactionalMailSender(MandrillService mandrillService, CsvMailUserConvertor csvMailUserConvertor) {
        this.mandrillService = mandrillService;
        this.csvMailUserConvertor = csvMailUserConvertor;
    }

    /**
     * 템플릿 메일을 발송 후 결과를 리턴합니다.
     *
     * @param templateSendRequest 템플릿 발송 시 요청
     * @return 템플릿 메일 발송 결과
     * @throws NotSupportedFileExtensionException 지원되지 않는 확장자일 경우
     * @throws NotExistPublishNameException       존재하지 않는 발행 이름일 경우
     */
    public List<TemplateSendResponse> sendTemplate(
            TemplateSendRequest templateSendRequest
    ) throws IOException, MandrillApiError {
        String publishName = templateSendRequest.getPublishName();
        List<TemplatesResponse> templates = mandrillService.getTemplates();

        checkExist(publishName, templates);

        MultipartFile userListFile = templateSendRequest.getUserListFile();
        checkNull(userListFile);
        checkSupportedExtension(userListFile);

        List<MailUser> mailUsers = csvMailUserConvertor.convert(userListFile);

        return mandrillService.sendWithTemplate(publishName, mailUsers);
    }

    /**
     * 존재하는 발행 이름인지 확인합니다.
     *
     * @param publishName 발행 이름
     * @param templates   전체 템플릿
     * @throws NotExistPublishNameException 존재하지 않는 발행 이름일 경우
     */
    private void checkExist(String publishName, List<TemplatesResponse> templates) {
        boolean existPublishName = templates.stream()
                .map(TemplatesResponse::getPublishName)
                .anyMatch(publishName::equals);

        if (!existPublishName) {
            throw new NotExistPublishNameException(publishName);
        }
    }

    private void checkNull(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("file은 비어있을 수 없습니다.");
        }
    }

    /**
     * 지원되는 확장자인지 확인합니다.
     *
     * @param file 파일
     * @throws NotSupportedFileExtensionException 지원되지 않는 확장자일 경우
     */
    private void checkSupportedExtension(MultipartFile file) {
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
