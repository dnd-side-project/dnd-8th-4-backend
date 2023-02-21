package dnd.diary.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dnd.diary.domain.group.Invite;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {
}
