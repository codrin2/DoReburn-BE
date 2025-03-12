package com.dubu.backend.plan.api;

import com.dubu.backend.core.config.WebConfig;
import com.dubu.backend.core.interceptor.TokenInterceptor;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.plan.application.PlanFacade;
import com.dubu.backend.plan.api.request.PlanCreateRequest;
import com.dubu.backend.plan.api.request.PlanFeedbackCreateRequest;
import com.dubu.backend.plan.api.response.FeedbackWritePageInfoResponse;
import com.dubu.backend.plan.api.response.PlanRecentResponse;
import com.dubu.backend.plan.core.exception.PlanNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PlanController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        WebConfig.class,
                        TokenInterceptor.class
                })
        })
@DisplayName("PlanController 테스트 - HTTP 요청/응답 검증")
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanFacade planFacade;

    private static final String PLAN_CREATE_JSON = """
            {
              "totalTime": 50,
              "totalSectionTime": 40,
              "subPaths": [
                {
                  "trafficType": "SUBWAY",
                  "sectionTime": 20,
                  "subwayCode": 1002,
                  "busNumber": null,
                  "busType": null,
                  "startName": "선릉",
                  "endName": "역삼"
                },
                {
                  "trafficType": "BUS",
                  "sectionTime": 20,
                  "subwayCode": null,
                  "busNumber": "143",
                  "busType": 1,
                  "startName": "역삼",
                  "endName": "강남"
                }
              ]
            }
            """;

    private static final String PLAN_FEEDBACK_JSON = """
            {
              "mood": "SATISFIED",
              "memo": "오늘 일정에 만족합니다."
            }
            """;

    @Nested
    @DisplayName("[POST /plans] 계획 생성")
    class Describe_createPlan {

        @Test
        @DisplayName("정상적인 요청이면, 201 Created와 함께 planId를 반환한다.")
        void it_creates_plan() throws Exception {
            // given
            Long memberId = 1L;
            Long planId = 12345L;
            BDDMockito.given(planFacade.savePlan(eq(memberId), anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(PlanCreateRequest.class)))
                    .willReturn(planId);

            // when & then
            mockMvc.perform(post("/plans")
                            .requestAttr("memberId", memberId)
                            .param("startX", "127.0")
                            .param("startY", "37.0")
                            .param("endX", "126.9")
                            .param("endY", "37.1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(PLAN_CREATE_JSON)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.planId").value(12345L));
        }

        @Test
        @DisplayName("존재하지 않는 회원이면, 404 Not Found를 반환한다.")
        void it_returns_404_when_member_not_found() throws Exception {
            // given
            Long memberId = 9999L;
            Mockito.doThrow(new MemberNotFoundException(memberId))
                    .when(planFacade).savePlan(eq(memberId), anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(PlanCreateRequest.class));

            // when & then
            mockMvc.perform(post("/plans")
                            .requestAttr("memberId", memberId)
                            .param("startX", "127.0")
                            .param("startY", "37.0")
                            .param("endX", "126.9")
                            .param("endY", "37.1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(PLAN_CREATE_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("[POST /plans/{planId}/feedbacks] 계획 피드백 생성")
    class Describe_createPlanFeedback {

        @Test
        @DisplayName("정상적인 요청이면, 201 Created와 함께 feedbackId를 반환한다.")
        void it_creates_plan_feedback() throws Exception {
            // given
            Long memberId = 1L;
            Long planId = 10L;
            Long feedbackId = 9876L;
            BDDMockito.given(planFacade.savePlanFeedback(eq(memberId), eq(planId), any(PlanFeedbackCreateRequest.class)))
                    .willReturn(feedbackId);

            // when & then
            mockMvc.perform(post("/plans/{planId}/feedbacks", planId)
                            .requestAttr("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(PLAN_FEEDBACK_JSON)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.feedbackId").value(9876L));
        }

        @Test
        @DisplayName("존재하지 않는 계획이면, 404 Not Found를 반환한다.")
        void it_returns_404_when_plan_not_found() throws Exception {
            Long memberId = 1L;
            Long planId = 9999L;
            Mockito.doThrow(new PlanNotFoundException(planId))
                    .when(planFacade).savePlanFeedback(eq(memberId), eq(planId), any(PlanFeedbackCreateRequest.class));

            mockMvc.perform(post("/plans/{planId}/feedbacks", planId)
                            .requestAttr("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(PLAN_FEEDBACK_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("NOT_FOUND_PLAN"));
        }
    }

    @Nested
    @DisplayName("[GET /plans/recent] 최근 계획 조회")
    class Describe_getRecentPlan {

        @Test
        @DisplayName("정상적으로 조회하면, 200 OK와 함께 PlanRecentResponse를 반환한다.")
        void it_returns_plan_recent_response() throws Exception {
            // given
            Long memberId = 1L;
            PlanRecentResponse mockResponse = new PlanRecentResponse(
                    15L,
                    40,
                    null, // createdAt
                    List.of()
            );
            BDDMockito.given(planFacade.findRecentPlan(memberId))
                    .willReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/plans/recent")
                            .requestAttr("memberId", memberId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.planId").value(15L));
        }

        @Test
        @DisplayName("회원 또는 플랜이 존재하지 않으면 404")
        void it_returns_404_when_not_found() throws Exception {
            Long memberId = 9999L;
            Mockito.doThrow(new MemberNotFoundException(memberId))
                    .when(planFacade).findRecentPlan(memberId);

            mockMvc.perform(get("/plans/recent")
                            .requestAttr("memberId", memberId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("[GET /plans/feedbacks] 피드백 작성 페이지 정보 조회")
    class Describe_getFeedbackWritePageInfo {

        @Test
        @DisplayName("정상적으로 조회하면, 200 OK와 함께 FeedbackWritePageInfoResponse 반환")
        void it_returns_feedback_info() throws Exception {
            // given
            Long memberId = 1L;
            FeedbackWritePageInfoResponse mockResponse = new FeedbackWritePageInfoResponse(
                    15L, 40, 3, List.of()
            );
            BDDMockito.given(planFacade.findFeedbackWritePageInfo(memberId))
                    .willReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/plans/feedbacks")
                            .requestAttr("memberId", memberId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.planId").value(15L));
        }

        @Test
        @DisplayName("회원 또는 플랜이 없으면 404")
        void it_returns_404_when_not_found() throws Exception {
            Long memberId = 9999L;
            Mockito.doThrow(new MemberNotFoundException(memberId))
                    .when(planFacade).findFeedbackWritePageInfo(memberId);

            mockMvc.perform(get("/plans/feedbacks")
                            .requestAttr("memberId", memberId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("[PATCH /plans/move-complete] 이동 완료 업데이트")
    class Describe_completeMove {

        @Test
        @DisplayName("정상적인 요청이면, 204 No Content 반환")
        void it_completes_move() throws Exception {
            // given
            Long memberId = 1L;

            // when & then
            mockMvc.perform(patch("/plans/move-complete")
                            .requestAttr("memberId", memberId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("회원이 존재하지 않으면, 404 반환")
        void it_returns_404_if_member_not_found() throws Exception {
            Long memberId = 9999L;
            Mockito.doThrow(new MemberNotFoundException(memberId))
                    .when(planFacade).completeMove(memberId);

            mockMvc.perform(patch("/plans/move-complete")
                            .requestAttr("memberId", memberId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("[DELETE /plans] 계획 삭제")
    class Describe_deletePlan {

        @Test
        @DisplayName("정상적인 요청이면, 204 No Content 반환")
        void it_deletes_plan() throws Exception {
            // given
            Long memberId = 1L;
            Long planId = 999L;

            // when & then
            mockMvc.perform(delete("/plans")
                            .requestAttr("memberId", memberId)
                            .param("planId", planId.toString()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 회원이면, 404 반환")
        void it_returns_404_if_member_not_found() throws Exception {
            Long memberId = 9999L;
            Long planId = 999L;
            Mockito.doThrow(new MemberNotFoundException(memberId))
                    .when(planFacade).removePlan(memberId, planId);

            mockMvc.perform(delete("/plans")
                            .requestAttr("memberId", memberId)
                            .param("planId", planId.toString()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"));
        }
    }
}