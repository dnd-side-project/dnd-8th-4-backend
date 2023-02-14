package dnd.diary.domain.mission;

import com.sun.istack.NotNull;
import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private Double latitude;

    @NotNull
    private Double longitude;

    private Boolean locationCheck = false;

    private Boolean contentCheck = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
}
