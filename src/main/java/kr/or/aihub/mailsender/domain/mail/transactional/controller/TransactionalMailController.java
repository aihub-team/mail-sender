package kr.or.aihub.mailsender.domain.mail.transactional.controller;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import kr.or.aihub.mailsender.domain.mail.transactional.application.MandrillService;
import kr.or.aihub.mailsender.domain.mail.transactional.application.TransactionalMailSender;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.CsvFilename;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplateSendRequest;
import kr.or.aihub.mailsender.domain.mail.transactional.dto.TemplatesResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/mail/transactional")
@Validated
public class TransactionalMailController {
    private final MandrillService mandrillService;
    private final TransactionalMailSender transactionalMailSender;

    public TransactionalMailController(MandrillService mandrillService, TransactionalMailSender transactionalMailSender) {
        this.mandrillService = mandrillService;
        this.transactionalMailSender = transactionalMailSender;
    }

    @GetMapping("/templates/send")
    public String getTemplatesToSend(Model model) throws MandrillApiError, IOException {
        List<TemplatesResponse> templates = mandrillService.getTemplates();

        model.addAttribute("templates", templates);

        return "mail/transactional/templates/send";
    }

    @PostMapping(value = "/templates/send")
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public String sendTemplates(
            @RequestParam @CsvFilename MultipartFile file,
            @RequestParam @NotBlank String publishName
    ) throws IOException, MandrillApiError {
        TemplateSendRequest templateSendRequest = new TemplateSendRequest(file, publishName);

        transactionalMailSender.sendTemplate(templateSendRequest);

        return "redirect:/mail/transactional/templates/send";
    }

}
