package ku.epta.service;

import ku.epta.CryptoTool;
import ku.epta.entity.AppDocument;
import ku.epta.entity.AppPhoto;
import ku.epta.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long fileId, LinkType linkType);
}
