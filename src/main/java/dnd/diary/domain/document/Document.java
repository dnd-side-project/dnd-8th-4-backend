package dnd.diary.domain.document;

import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    // 모아보기 내 여러 폴더 목록
    @OneToMany(mappedBy = "document")
    private List<DocumentFolder> documentFolders = new ArrayList<>();

    // 해당 모아보기 목록 가진 유저 -> 유저별로 하나씩 보유
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 모아보기에 포함된 게시물
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;
}
