package kr.or.aihub.mailsender.domain.mail.transactional.application;

import com.microtripit.mandrillapp.lutung.controller.MandrillMessagesApi;
import com.microtripit.mandrillapp.lutung.controller.MandrillTemplatesApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.microtripit.mandrillapp.lutung.view.MandrillTemplate;
import kr.or.aihub.mailsender.domain.mail.transactional.domain.MailUser;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplateSendResponse;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplatesResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MandrillService {
    private final MandrillTemplatesApi mandrillTemplatesApi;
    private final MandrillMessagesApi mandrillMessagesApi;

    public MandrillService(MandrillTemplatesApi mandrillTemplatesApi, MandrillMessagesApi mandrillMessagesApi) {
        this.mandrillTemplatesApi = mandrillTemplatesApi;
        this.mandrillMessagesApi = mandrillMessagesApi;
    }

    /**
     * 템플릿 목록을 리턴합니다.
     *
     * @return 템플릿 목록
     */
    public List<TemplatesResponse> getTemplates() throws MandrillApiError, IOException {
        MandrillTemplate[] templateArray = mandrillTemplatesApi.list();

        List<MandrillTemplate> templates = toList(templateArray);

        return templates.stream()
                .map(MandrillTemplate::getPublishName)
                .map(TemplatesResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 템플릿 발행이름으로 템플릿을 찾아 메일 유저에게 발송하고, 결과를 리턴합니다.
     *
     * @param publishName 발행 이름
     * @param mailUsers   발송할 유저
     * @return 발송 결과
     * @throws MandrillApiError Mandrill API 에러
     * @throws IOException      I/O 예외
     */
    public List<TemplateSendResponse> sendWithTemplate(String publishName, List<MailUser> mailUsers) throws MandrillApiError, IOException {
        MandrillMessage mandrillMessage = new MandrillMessage();

        List<MandrillMessage.Recipient> recipients = mailUsers.stream()
                .map(mailUser -> createRecipient(mailUser))
                .collect(Collectors.toList());

        mandrillMessage.setTo(recipients);

        MandrillMessageStatus[] mandrillMessageStatuses = mandrillMessagesApi.sendTemplate(
                publishName,
                null,
                mandrillMessage,
                false
        );

        return Arrays.stream(mandrillMessageStatuses)
                .map(TemplateSendResponse::new)
                .collect(Collectors.toList());
    }

    private MandrillMessage.Recipient createRecipient(MailUser mailUser) {
        MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
        recipient.setEmail(mailUser.getEmail());

        return recipient;
    }

    private List<MandrillTemplate> toList(MandrillTemplate[] templateArray) {
        return Arrays.asList(templateArray);
    }
}
