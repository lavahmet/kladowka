package ku.epta.dao;

import ku.epta.entity.AppDocument;
import ku.epta.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
