package kr.or.aihub.mailsender.domain.mail.transactional.application;

import com.microtripit.mandrillapp.lutung.controller.MandrillTemplatesApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillTemplate;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplatesResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MandrillService {
    private final MandrillTemplatesApi mandrillTemplatesApi;

    public MandrillService(MandrillTemplatesApi mandrillTemplatesApi) {
        this.mandrillTemplatesApi = mandrillTemplatesApi;
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

    private List<MandrillTemplate> toList(MandrillTemplate[] templateArray) {
        return Arrays.asList(templateArray);
    }
}
