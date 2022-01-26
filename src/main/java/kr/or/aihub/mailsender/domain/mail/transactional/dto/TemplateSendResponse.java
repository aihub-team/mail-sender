package kr.or.aihub.mailsender.domain.mail.transactional.dto;

import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TemplateSendResponse {
    private final String email;
    private final String status;
    private final String rejectReason;
    private final String id;

    @Builder
    public TemplateSendResponse(String email, String status, String rejectReason, String id) {
        this.email = email;
        this.status = status;
        this.rejectReason = rejectReason;
        this.id = id;
    }

    public TemplateSendResponse(MandrillMessageStatus mandrillMessageStatus) {
        this.email = mandrillMessageStatus.getEmail();
        this.status = mandrillMessageStatus.getStatus();
        this.rejectReason = mandrillMessageStatus.getRejectReason();
        this.id = mandrillMessageStatus.getId();
    }
}
