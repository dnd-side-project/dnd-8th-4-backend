package dnd.diary.repository.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dnd.diary.domain.mission.UserAssignMission;

@Repository
public interface UserAssignMissionRepository extends JpaRepository<UserAssignMission, Long> {

    UserAssignMission findByUserIdAndMissionId(Long userId, Long missionId);
}
