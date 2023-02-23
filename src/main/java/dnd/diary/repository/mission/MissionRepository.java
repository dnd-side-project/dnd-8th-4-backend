package dnd.diary.repository.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dnd.diary.domain.mission.Mission;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
}
