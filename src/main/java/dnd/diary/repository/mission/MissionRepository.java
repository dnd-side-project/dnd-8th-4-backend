package dnd.diary.repository.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dnd.diary.domain.mission.Mission;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    @Query(value = "select * from mission\n" +
            "where latitude between ?1 and ?2 and longitude between ?3 and ?4 ;",
            nativeQuery = true)
    List<Mission> findWithinMap(Double startX, Double endX, Double startY, Double endY);

//    List<Mission> findWithinMissionRadius(Double currX, Double currY, Double targetX, Double targetY);

}
