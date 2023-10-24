package ku.epta.controller;

import ku.epta.dto.MailParams;
import ku.epta.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/mail")
@RestController
public class MailController {
    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams) {
        mailService.send(mailParams);
        return ResponseEntity.ok().build();
    }
}
