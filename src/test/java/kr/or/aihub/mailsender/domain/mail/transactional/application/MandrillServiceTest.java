package kr.or.aihub.mailsender.domain.mail.transactional.application;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplatesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("MandrillService 클래스")
class MandrillServiceTest {

    @Autowired
    private MandrillService mandrillService;

    @Nested
    @DisplayName("getTemplates 메서드는")
    class Describe_getTemplates {

        @Test
        @DisplayName("템플릿 발행 이름 목록을 리턴한다")
        void It_returnsTemplatePublishNames() throws MandrillApiError, IOException {
            List<TemplatesResponse> templates = mandrillService.getTemplates();

            assertThat(templates.size()).isNotEqualTo(0);

            templates.stream()
                    .map(TemplatesResponse::getPublishName)
                    .forEach(System.out::println);
        }
    }
}