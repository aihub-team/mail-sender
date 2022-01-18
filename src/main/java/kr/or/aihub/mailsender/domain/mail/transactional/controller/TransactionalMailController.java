package kr.or.aihub.mailsender.domain.mail.transactional.controller;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import kr.or.aihub.mailsender.domain.mail.transactional.application.MandrillService;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplatesResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/mail/transactional")
public class TransactionalMailController {
    private final MandrillService mandrillService;

    public TransactionalMailController(MandrillService mandrillService) {
        this.mandrillService = mandrillService;
    }

    @GetMapping("/templates")
    public String templates(
            Model model
    ) throws MandrillApiError, IOException {
        List<TemplatesResponse> templates = mandrillService.getTemplates();

        model.addAttribute("templates", templates);

        return "mail/transactional/templates";
    }

}
