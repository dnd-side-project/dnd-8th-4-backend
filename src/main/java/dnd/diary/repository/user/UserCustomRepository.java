package dnd.diary.repository.user;

import dnd.diary.response.user.UserSearchResponse;

import java.util.List;

public interface UserCustomRepository {
    List<UserSearchResponse.UserSearchInfo> searchNickname(String keyword);
}
