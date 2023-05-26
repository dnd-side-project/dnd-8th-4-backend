package dnd.diary.repository.content;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dnd.diary.domain.content.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;

import java.util.List;

import static dnd.diary.domain.comment.QComment.*;
import static dnd.diary.domain.content.QContent.*;

public class ContentRepositoryImpl implements ContentCustomRepository{

    private final JPAQueryFactory queryFactory;

    public ContentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Content> searchMyCommentPosts(Long userId, PageRequest pageRequest) {
        List<Content> contents = queryFactory
                .select(content1)
                .from(comment)
                .leftJoin(content1.comments, comment)
                .where(comment.user.id.eq(userId))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(content1.count())
                .leftJoin(content1.comments, comment)
                .where(comment.user.id.eq(userId))
                .from(content1);

        return PageableExecutionUtils.getPage(contents, pageRequest, countQuery::fetchOne);
    }
}
