package dnd.diary.domain.sticker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Sticker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sticker_id")
	private Long id;

	private String stickerImageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sticker_group_id")
	private StickerGroup stickerGroup;

	@Builder
	private Sticker(String stickerImageUrl, StickerGroup stickerGroup) {
		this.stickerImageUrl = stickerImageUrl;

		stickerGroup.getStickers().add(this);
	}

	public static Sticker toEntity(String stickerImageUrl, StickerGroup stickerGroup) {
		return new Sticker(stickerImageUrl, stickerGroup);
	}
}
