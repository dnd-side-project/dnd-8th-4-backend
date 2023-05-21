package dnd.diary.controller.content;

import dnd.diary.controller.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContentControllerTest extends ControllerTestSupport {

    @DisplayName("피드 작성 API")
    @Test
    void contentCreate() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(HttpMethod.POST, URI.create("/content"))
                                .param("groupId", "1")
                                .param("content", "테스트")
                                .param("latitude", "0.0")
                                .param("longitude", "0.0")
                                .param("location", "테스트 장소")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("피드 조회 API")
    @Test
    void contentDetail() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content")
                                .param("contentId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("피드 수정 API")
    @Test
    void contentUpdate() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/content")
                                .param("contentId", "1")
                                .param("content", "테스트")
                                .param("latitude", "0.0")
                                .param("longitude", "0.0")
                                .param("location", "테스트 장소")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("피드 삭제 API")
    @Test
    void contentDelete() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/content")
                                .param("contentId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("그룹 피드 조회 API")
    @Test
    void contentGroupList() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/group")
                                .param("groupId", "1")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("그룹 전체 피드 조회 API")
    @Test
    void contentGroupAllList() throws Exception {
        // given
        List<String> groupIdList = List.of("1");
        String[] groupIdArray = groupIdList.toArray(new String[0]);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/group/all")
                                .param("groupId", groupIdArray)
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("피드 검색 조회 API")
    @Test
    void searchContent() throws Exception {
        // given
        List<String> groupIdList = List.of("1");
        String[] groupIdArray = groupIdList.toArray(new String[0]);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/group/search")
                                .param("groupId", groupIdArray)
                                .param("word", "a")
                                .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("지도 피드 검색 API")
    @Test
    void myMapList() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/map")
                                .param("startLatitude", "1")
                                .param("startLongitude", "1")
                                .param("endLatitude", "1")
                                .param("endLongitude", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("지도 피드 상세 조회 API")
    @Test
    void myMapListDetail() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/content/map/detail")
                                .param("location", "테스트")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}