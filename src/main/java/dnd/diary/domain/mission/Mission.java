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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.locationtech.jts.geom.Point;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE mission SET deleted = true WHERE mission_id = ?")
@Where(clause = "deleted = false")
public class Mission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    @NotNull
    private String missionName;

    private String missionNote;

    private Boolean existPeriod;

    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime missionStartDate;

    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime missionEndDate;

    @Enumerated(EnumType.STRING)
    private MissionStatus missionStatus;

    @NotNull
    private String missionLocationName;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Integer missionColor;

//    private Point point;

    private boolean deleted = Boolean.FALSE;   // 미션 삭제 여부

    // 미션 생성자
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User missionCreateUser;

    // 하나의 그룹 = 여러 개 미션 보유
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    // 미션 생성으로 -> 그룹 내 여러 명의 유저에게 미션 할당
    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
    private List<UserAssignMission> userAssignMissions = new ArrayList<>();

    @Builder
    private Mission(User user, Group group, String missionName, String missionNote, Boolean existPeriod
        , LocalDateTime missionStartDate, LocalDateTime missionEndDate, String missionLocationName, Double latitude, Double longitude
        , Integer missionColor, MissionStatus missionStatus, Point point) {

        this.missionCreateUser = user;
        this.group = group;
        this.missionName = missionName;
        this.missionNote = missionNote;
        // UTC 로 변환 후 저장
        this.existPeriod = existPeriod;
        this.missionStartDate = convertLocalDateTimeZone(missionStartDate, ZoneId.of("Asia/Seoul"), ZoneOffset.UTC);
        this.missionEndDate = convertLocalDateTimeZone(missionEndDate, ZoneId.of("Asia/Seoul"), ZoneOffset.UTC);
        this.missionLocationName = missionLocationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.missionColor = missionColor;
        this.missionStatus = missionStatus;
//        this.point = point;

        group.getMissions().add(this);
    }

    public static Mission toEntity(User user, Group group, String missionName, String missionNote, Boolean existPeriod
        , LocalDateTime missionStartDate, LocalDateTime missionEndDate, String missionLocationName, Double latitude, Double longitude
        , Integer missionColor, MissionStatus missionStatus, Point point ) {
        return new Mission(user, group, missionName, missionNote
            ,existPeriod, missionStartDate, missionEndDate
            , missionLocationName, latitude, longitude, missionColor, missionStatus, point);
    }

    @PreRemove
    public void deleteMission() {
        this.deleted = false;
    }
}
