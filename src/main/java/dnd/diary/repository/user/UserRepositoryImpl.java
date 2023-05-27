package dnd.diary.repository.user;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dnd.diary.domain.user.QUser;
import dnd.diary.response.user.UserSearchResponse;

import javax.persistence.EntityManager;
import java.util.List;

import static dnd.diary.domain.user.QUser.*;

public class UserRepositoryImpl implements UserCustomRepository{

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<UserSearchResponse.UserSearchInfo> searchNickname(String keyword) {
        return queryFactory
                .select(Projections.fields(UserSearchResponse.UserSearchInfo.class,
                        user.id.as("userId"),
                        user.email.as("userEmail"),
                        user.nickName.as("userNickName"),
                        user.profileImageUrl.as("profileImageUrl")
                        ))
                .from(user)
                .where(user.nickName.containsIgnoreCase(keyword))
                .fetch();
    }
}
