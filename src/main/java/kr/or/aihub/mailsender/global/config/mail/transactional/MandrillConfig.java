package kr.or.aihub.mailsender.global.config.mail.transactional;

import com.microtripit.mandrillapp.lutung.controller.MandrillTemplatesApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MandrillConfig {
    private final String key;

    public MandrillConfig(@Value("${mandrill.apiKey}") String key) {
        this.key = key;
    }

    @Bean
    public MandrillTemplatesApi mandrillTemplatesApi() {
        return new MandrillTemplatesApi(key);
    }
}
