package dnd.diary.domain.mission;

import static dnd.diary.domain.mission.DateUtil.*;

import com.sun.istack.NotNull;
import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Mission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    @NotNull
    private String missionName;

    private String missionNote;

    private LocalDateTime missionStartDate;

    private LocalDateTime missionEndDate;

    @Enumerated(EnumType.STRING)
    private MissionStatus missionStatus;

    @NotNull
    private String missionLocationName;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Boolean locationCheck;

    private Boolean contentCheck;
    private Boolean isComplete;   // 미션 전체 완료 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;    // createUser

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    private Mission(User user, Group group, String missionName, String missionNote
        , LocalDateTime missionStartDate, LocalDateTime missionEndDate, String missionLocationName, Double latitude, Double longitude
        , MissionStatus missionStatus) {

        this.user = user;
        this.group = group;
        this.missionName = missionName;
        this.missionNote = missionNote;
        // UTC 로 변환 후 저장
        this.missionStartDate = convertLocalDateTimeZone(missionStartDate, ZoneId.of("Asia/Seoul"), ZoneOffset.UTC);
        this.missionEndDate = convertLocalDateTimeZone(missionEndDate, ZoneId.of("Asia/Seoul"), ZoneOffset.UTC);
        this.missionLocationName = missionLocationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.missionStatus = missionStatus;
        this.locationCheck = false;
        this.contentCheck = false;
        this.isComplete = false;

        user.getMissions().add(this);
    }

    public static Mission toEntity(User user, Group group, String missionName, String missionNote
        , LocalDateTime missionStartDate, LocalDateTime missionEndDate, String missionLocationName, Double latitude, Double longitude
        , MissionStatus missionStatus) {
        return new Mission(user, group, missionName, missionNote, missionStartDate, missionEndDate
            , missionLocationName, latitude, longitude, missionStatus);
    }
}
