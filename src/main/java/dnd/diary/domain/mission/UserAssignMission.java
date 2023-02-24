package dnd.diary.domain.mission;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;

import lombok.Builder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import dnd.diary.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE user_assign_mission SET deleted = true WHERE user_assign_mission_id = ?")
@Where(clause = "deleted = false")
public class UserAssignMission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_assign_mission_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mission_id")
	private Mission mission;

	private Boolean locationCheck;

	private Boolean contentCheck;

	private Boolean isComplete;   // 미션 전체 완료 여부

	private boolean deleted = Boolean.FALSE;   // row 삭제 여부

	@Builder
	private UserAssignMission(User user, Mission mission) {
		this.user = user;
		this.mission = mission;
		this.locationCheck = false;
		this.contentCheck = false;
		this.isComplete = false;

		user.getUserAssignMissions().add(this);
	}

	public static UserAssignMission toEntity(User user, Mission mission) {
		return new UserAssignMission(user, mission);
	}

	@PreRemove
	public void deleteMission() {
		this.deleted = false;
	}

	public void completeLocationCheck() {
		this.locationCheck = true;
	}
	public void completeContentCheck() {
		this.contentCheck = true;
		this.isComplete = true;
	}

}
