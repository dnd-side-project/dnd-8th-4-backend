package dnd.diary.domain.group;

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
public class GroupImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_image_id")
	private Long id;

	private String groupImageUrl;

	@Builder
	private GroupImage(String groupImageUrl) {
		this.groupImageUrl = groupImageUrl;
	}

	public static GroupImage toEntity(String groupImageUrl) {
		return new GroupImage(groupImageUrl);
	}
}
