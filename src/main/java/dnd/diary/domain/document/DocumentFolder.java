package dnd.diary.domain.document;

import com.sun.istack.NotNull;
import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DocumentFolder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_folder_id")
    private Long id;

    @NotNull
    private String documentFolderName;

    // 모아보기 폴더를 가진 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 모아보기 폴더가 속한 사용자의 모아보기 페이지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    // 모아보기 폴더에 속한 게시물 정보
    @OneToMany(mappedBy = "documentFolder")
    private List<ContentJoinDocumentFolder> contentJoinDocumentFolders;

}
