package ku.epta.service;

import ku.epta.entity.AppDocument;
import ku.epta.entity.AppPhoto;
import ku.epta.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    public AppDocument getDocument(String docId);
    public AppPhoto getPhoto(String photoId);
}
