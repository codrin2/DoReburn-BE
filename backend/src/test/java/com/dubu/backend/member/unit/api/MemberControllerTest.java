package com.dubu.backend.member.unit.api;

import com.dubu.backend.core.config.WebConfig;
import com.dubu.backend.core.interceptor.TokenInterceptor;
import com.dubu.backend.member.api.MemberController;
import com.dubu.backend.member.application.MemberLocationFacade;
import com.dubu.backend.member.application.MemberQueryFacade;
import com.dubu.backend.member.application.MemberCommandFacade;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.api.request.MemberInfoUpdateRequest;
import com.dubu.backend.member.api.response.MemberInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MemberController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        WebConfig.class,
                        TokenInterceptor.class
                })
        })
@ExtendWith(MockitoExtension.class)
@DisplayName("MemberController 테스트 - HTTP 요청/응답 검증")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberQueryFacade memberQueryFacade;
    @MockitoBean
    private MemberCommandFacade memberCommandFacade;
    @MockitoBean
    private MemberLocationFacade memberLocationFacade;

    private static final String MEMBER_INFO_UPDATE_JSON = """
            {
              "categories": ["READING", "OTHERS"],
              "homeTitle": "내집",
              "homeAddress": "서울시 강남구 역삼동 123",
              "homeAddressX": 127.0276,
              "homeAddressY": 37.4979,
              "schoolTitle": "서울대학교",
              "schoolAddress": "서울시 관악구 관악로 1",
              "schoolAddressX": 126.9528804,
              "schoolAddressY": 37.4784966
            }
            """;

    @Nested
    @DisplayName("[GET /members] 회원 기본 정보 조회")
    class Describe_getMemberInfo {

        @Test
        @DisplayName("유효한 memberId가 있다면, 200 OK와 함께 회원 정보를 반환한다.")
        void it_returns_member_info() throws Exception {
            // given
            Long memberId = 1L;
            MemberInfoResponse mockResponse = new MemberInfoResponse(
                    "test@example.com",
                    "홍길동",
                    List.of("READING", "ENGLISH"),
                    "우리집",
                    "서울시 강남구",
                    127.0276,
                    37.4979,
                    "학교",
                    "서울시 관악구",
                    126.9528804,
                    37.4784966
            );

            BDDMockito.given(memberQueryFacade.findMemberInfo(memberId)).willReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/members")
                            .requestAttr("memberId", memberId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.email").value("test@example.com"))
                    .andExpect(jsonPath("$.data.nickname").value("홍길동"))
                    .andExpect(jsonPath("$.data.categories[0]").value("READING"));
        }

        @Test
        @DisplayName("회원이 존재하지 않으면, 404 응답과 함께 에러 메시지를 반환한다.")
        void it_returns_404_when_member_not_found() throws Exception {
            // given
            Long memberId = 9999L;
            BDDMockito.given(memberQueryFacade.findMemberInfo(memberId))
                    .willThrow(new MemberNotFoundException(memberId));

            // when & then
            mockMvc.perform(get("/members")
                            .requestAttr("memberId", memberId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다. memberId : 9999"));
        }
    }

    @Nested
    @DisplayName("[PATCH /members] 회원 정보 수정")
    class Describe_updateMemberInfo {

        @Test
        @DisplayName("정상적인 요청이면, 200 OK와 함께 수정된 회원 정보를 반환한다.")
        void it_updates_member_info() throws Exception {
            // given
            Long memberId = 1L;
            MemberInfoResponse mockResponse = new MemberInfoResponse(
                    "test@example.com",
                    "홍길동",
                    List.of("READING", "OTHERS"),
                    "내집",
                    "서울시 강남구",
                    127.0276,
                    37.4979,
                    "서울대학교",
                    "서울시 관악구",
                    126.9528804,
                    37.4784966
            );

            BDDMockito.given(memberCommandFacade.updateMemberInfo(eq(memberId), any(MemberInfoUpdateRequest.class)))
                    .willReturn(mockResponse);

            // when & then
            mockMvc.perform(patch("/members")
                            .requestAttr("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MEMBER_INFO_UPDATE_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.email").value("test@example.com"))
                    .andExpect(jsonPath("$.data.categories[0]").value("READING"));
        }

        @Test
        @DisplayName("회원이 존재하지 않으면, 404 Not Found를 반환한다.")
        void it_throws_exception_when_member_not_found() throws Exception {
            // given
            Long memberId = 9999L;
            Mockito.doThrow(new MemberNotFoundException(memberId))
                    .when(memberCommandFacade).updateMemberInfo(eq(memberId), any(MemberInfoUpdateRequest.class));

            // when & then
            mockMvc.perform(patch("/members")
                            .requestAttr("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MEMBER_INFO_UPDATE_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"));
        }
    }
}