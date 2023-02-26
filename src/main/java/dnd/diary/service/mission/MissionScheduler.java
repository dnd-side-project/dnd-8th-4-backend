package dnd.diary.service.mission;

import dnd.diary.domain.mission.Mission;
import dnd.diary.domain.mission.MissionStatus;
import dnd.diary.domain.mission.UserAssignMission;
import dnd.diary.repository.mission.MissionRepository;
import dnd.diary.repository.mission.UserAssignMissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionScheduler {

    private final UserAssignMissionRepository userAssignMissionRepository;
    private final MissionRepository missionRepository;

    @Scheduled(cron = "0 10 15 * * ?")   // UTC 기준으로 동작
    @Transactional
    public void updateMissionStatus() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 모든 미션에 대해
        List<Mission> missions = missionRepository.findAll();
        for (Mission mission : missions) {
            log.info("[배치 전 미션 상태] : {}", mission.getMissionStatus());
            LocalDate missionStartDate = mission.getMissionStartDate().toLocalDate();
            LocalDate missionEndDate = mission.getMissionStartDate().toLocalDate();

            int compareStatus = missionStartDate.compareTo(today);

            if (missionEndDate.isBefore(today)) {
                mission.updateMissionStatus(MissionStatus.FINISH);
            } else if (compareStatus > 0) {
                mission.updateMissionStatus(MissionStatus.READY);
            } else {
                mission.updateMissionStatus(MissionStatus.ACTIVE);
            }
            log.info("[배치 후 미션 상태] : {}", mission.getMissionStatus());
        }
        missionRepository.saveAll(missions);
    }
}
