package dnd.diary.repository.content;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dnd.diary.domain.bookmark.QBookmark;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.QContentImage;
import dnd.diary.response.content.ContentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;

import java.util.List;

import static dnd.diary.domain.bookmark.QBookmark.*;
import static dnd.diary.domain.comment.QComment.*;
import static dnd.diary.domain.content.QContent.*;
import static dnd.diary.domain.content.QContentImage.*;
import static dnd.diary.domain.group.QGroup.group;

public class ContentRepositoryImpl implements ContentCustomRepository {

    private final JPAQueryFactory queryFactory;

    public ContentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Content> searchMyCommentPosts(Long userId, PageRequest pageRequest) {
        List<Content> contents = queryFactory
                .select(content1)
                .from(content1)
                .innerJoin(content1.comments, comment)
                .where(comment.user.id.eq(userId))
                .orderBy(content1.createdAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(content1.count())
                .from(content1)
                .innerJoin(content1.comments, comment)
                .where(comment.user.id.eq(userId));

        return PageableExecutionUtils.getPage(contents, pageRequest, countQuery::fetchOne);
    }

    @Override
    public List<Long> findContentIdList(Long userId) {
        return queryFactory
                .select(bookmark.content.id)
                .from(bookmark)
                .where(bookmark.user.id.eq(userId))
                .fetch();
    }

    @Override
    public Page<Content> searchMyGroupContent(String word, List<Long> groupId, Pageable pageable) {
        List<Content> content = queryFactory
                .selectFrom(content1)
                .innerJoin(content1.group, group)
                .where(
                        group.id.in(groupId),
                        content1.deletedYn.isFalse(),
                        content1.content.containsIgnoreCase(word))
                .orderBy(content1.content.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(content1.count())
                .from(content1)
                .innerJoin(content1.group, group)
                .where(group.id.in(groupId), content1.deletedYn.isFalse());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
