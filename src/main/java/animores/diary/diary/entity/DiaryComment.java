package animores.diary.diary.entity;

import animores.diary.common.BaseEntity;
import animores.diary.profile.entity.Profile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diary_comment")
public class DiaryComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    private Profile profile;

    @OneToMany(mappedBy = "diaryComment", cascade = CascadeType.ALL)
    private List<DiaryReply> replies;

    private String content;


    private LocalDateTime deletedDt;

    public static DiaryComment create(Diary diary, Profile profile, String content) {
        return DiaryComment.builder()
            .diary(diary)
            .profile(profile)
            .content(content)
            .build();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void delete() {
        this.deletedDt = LocalDateTime.now();
    }

}
