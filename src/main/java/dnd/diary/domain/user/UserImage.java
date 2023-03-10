package dnd.diary.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_image_id")
	private Long id;

	private String userImageUrl;

	@Builder
	private UserImage(String userImageUrl) {
		this.userImageUrl = userImageUrl;
	}

	public static UserImage toEntity(String userImageUrl) {
		return new UserImage(userImageUrl);
	}
}
