package dnd.diary.request.service.mission;

import lombok.Getter;


@Getter
public class MissionListByMapRequest {

    private Double currLatitude;   // 현재 위치 위도
    private Double currLongitude;   // 현재 위치 경도

    private Double startLatitude;
    private Double startLongitude;

    private Double endLatitude;
    private Double endLongitude;

    public MissionListByMapRequest setStartXY() {
        if (endLatitude < startLatitude) {
            Double temp = startLatitude;
            this.startLatitude = endLatitude;
            this.endLatitude = temp;
        }
        if (endLongitude < startLongitude) {
            Double temp = startLongitude;
            this.startLongitude = endLongitude;
            this.endLongitude = temp;
        }

        return this;
    }

}