package animores.diary.diary.entity;

import animores.diary.account.entity.Account;
import animores.diary.common.BaseEntity;
import animores.diary.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diary")
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    private List<DiaryMedia> media;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    private List<DiaryComment> comments;

    @Lob
    @Column(nullable = false)
    private String content;

    private LocalDateTime deletedDt;

    public static Diary create(Account account, Profile profile, String content) {
        return Diary.builder()
            .account(account)
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
