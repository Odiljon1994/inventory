package com.axia.inventorymanagment.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberListResponse {
    private Integer totalMembers;
    private Integer totalPointsGranted;
    private List<MemberSummary> members;
    private Integer total;
    private Integer page;
    private Integer limit;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberSummary {
        private String memberId;
        private String name;
        private Integer points;
        private StageInfo stage;
        private String email;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StageInfo {
        private Integer stageId;
        private String stageName;
    }
}
