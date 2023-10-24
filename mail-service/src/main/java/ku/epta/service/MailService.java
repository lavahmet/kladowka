package ku.epta.service;

import ku.epta.dto.MailParams;

public interface MailService {
    void send(MailParams mailParams);
}
