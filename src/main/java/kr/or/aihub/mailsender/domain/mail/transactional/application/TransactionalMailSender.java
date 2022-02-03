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
import java.util.List;

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
    ) throws MandrillApiError, IOException {
        String publishName = templateSendRequest.getPublishName();
        checkExist(publishName);

        MultipartFile userListFile = templateSendRequest.getUserListFile();
        List<MailUser> mailUsers = csvMailUserConvertor.convert(userListFile);

        return mandrillService.sendWithTemplate(publishName, mailUsers);
    }

    /**
     * 존재하는 발행 이름인지 확인합니다.
     *
     * @param publishName 발행 이름
     * @throws NotExistPublishNameException 존재하지 않는 발행 이름일 경우
     */
    private void checkExist(String publishName) throws MandrillApiError, IOException {
        List<TemplatesResponse> templates = mandrillService.getTemplates();

        boolean existPublishName = templates.stream()
                .map(TemplatesResponse::getPublishName)
                .anyMatch(publishName::equals);

        if (!existPublishName) {
            throw new NotExistPublishNameException(publishName);
        }
    }

}
