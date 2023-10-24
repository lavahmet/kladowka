package ku.epta.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_photo")
public class AppPhoto {
    @Id
    @GeneratedValue
    private Long Id;
    private String telegramFileId;
    @OneToOne
    private BinaryContent binaryContent;
    private Integer fileSize;
}
