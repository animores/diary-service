package animores.diary.profile.repository;

import animores.diary.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findAllByAccountIdAndDeletedAtIsNull(Long id);
}
