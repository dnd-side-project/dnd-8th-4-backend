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

	// 개별 스티커 이미지가, 해당 스티커 그룹의 대표인지 여부
	// -> 대표 스티커라면, 그 이미지를 스티커 상세 보기 썸네일 이미지로 대체
	private boolean mainStickerYn = Boolean.FALSE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sticker_group_id")
	private StickerGroup stickerGroup;

	@Builder
	private Sticker(String stickerImageUrl, StickerGroup stickerGroup, boolean mainStickerYn) {
		this.stickerImageUrl = stickerImageUrl;
		this.stickerGroup = stickerGroup;
		this.mainStickerYn = mainStickerYn;

		stickerGroup.getStickers().add(this);
	}

	public static Sticker toEntity(String stickerImageUrl, StickerGroup stickerGroup, boolean mainStickerYn) {
		return new Sticker(stickerImageUrl, stickerGroup, mainStickerYn);
	}
}
