package dnd.diary.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dnd.diary.domain.user.UserImage;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {
}
