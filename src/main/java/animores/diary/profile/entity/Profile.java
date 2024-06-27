package animores.diary.profile.entity;

import animores.diary.account.entity.Account;
import animores.diary.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static animores.diary.common.S3Path.PROFILE_IMAGE_PATH;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 일대다 계정
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private String name;

    private String imageUrl;
    private LocalDateTime deletedAt;

    private static final String DEFAULT_PROFILE_IMAGE_URL = PROFILE_IMAGE_PATH + "default_profile.png";

    public String getImageUrl() {
        return this.imageUrl == null ? DEFAULT_PROFILE_IMAGE_URL : this.imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }
}
