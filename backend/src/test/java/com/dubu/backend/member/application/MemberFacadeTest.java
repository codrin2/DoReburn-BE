package com.dubu.backend.member.application;

import com.dubu.backend.member.domain.Address;
import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.MemberCategory;
import com.dubu.backend.member.domain.enums.AddressType;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.presentation.MemberLocationDto;
import com.dubu.backend.member.presentation.MemberStatusChangeDto;
import com.dubu.backend.member.presentation.request.MemberInfoUpdateRequest;
import com.dubu.backend.member.presentation.request.MemberOnboardingRequest;
import com.dubu.backend.member.presentation.response.MemberInfoResponse;
import com.dubu.backend.member.presentation.response.MemberSavedAddressResponse;
import com.dubu.backend.member.presentation.response.MemberStatusResponse;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.exception.MemberSavedAddressNotFoundException;
import com.dubu.backend.member.exception.RedisUnavailableException;
import com.dubu.backend.member.domain.repository.AddressRepository;
import com.dubu.backend.member.infrastructure.redis.LocationRedisRepository;
import com.dubu.backend.member.domain.repository.MemberCategoryRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.exception.PlanNotFoundException;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.exception.CategoryNotFoundException;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.RedisConnectionFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MemberService 단위 테스트")
class MemberFacadeTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private MemberCategoryRepository memberCategoryRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private LocationRedisRepository locationRedisRepository;

    @InjectMocks
    private MemberQueryFacade memberQueryFacade;
    @InjectMocks
    private MemberFacade memberFacade;

    private Long memberId;
    private Member defaultMember;
    private Address homeAddress;
    private Address schoolAddress;
    private Category categoryReading;
    private Category categoryEnglish;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        memberId = 1L;

        // 공통 Member / Address / Category Mock 세팅
        defaultMember = mock(Member.class);
        when(defaultMember.getId()).thenReturn(memberId);

        homeAddress = Address.builder()
                .id(100L)
                .addressType(AddressType.HOME)
                .title("우리집")
                .roadAddress("서울시 강남구")
                .xCoordinate(37.0)
                .yCoordinate(127.0)
                .build();

        schoolAddress = Address.builder()
                .id(101L)
                .addressType(AddressType.SCHOOL)
                .title("학교")
                .roadAddress("서울시 서초구")
                .xCoordinate(36.9)
                .yCoordinate(127.1)
                .build();

        categoryReading = Category.builder().id(10L).name("READING").build();
        categoryEnglish = Category.builder().id(11L).name("ENGLISH").build();
    }

    @Nested
    @DisplayName("[findMemberInfo] 회원 정보 조회")
    class Describe_findMemberInfo {

        @Test
        @DisplayName("존재하는 회원 ID일 때, 회원의 카테고리 목록과 주소 목록이 포함된 DTO를 반환한다.")
        void it_returns_member_info() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(defaultMember));
            when(memberCategoryRepository.findCategoriesByMemberId(memberId))
                    .thenReturn(List.of(categoryEnglish, categoryReading));
            when(addressRepository.findByMemberId(memberId)).thenReturn(List.of(homeAddress, schoolAddress));

            // when
            MemberInfoResponse result = memberQueryFacade.findMemberInfo(memberId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.categories()).containsExactlyInAnyOrder("ENGLISH", "READING");
            assertThat(result.homeTitle()).isEqualTo("우리집");
            assertThat(result.schoolTitle()).isEqualTo("학교");

            verify(memberRepository).findById(memberId);
            verify(memberCategoryRepository).findCategoriesByMemberId(memberId);
            verify(addressRepository).findByMemberId(memberId);
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID라면, MemberNotFoundException이 발생한다.")
        void it_throws_MemberNotFoundException() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberQueryFacade.findMemberInfo(memberId))
                    .isInstanceOf(MemberNotFoundException.class);

            verify(memberRepository).findById(memberId);
        }
    }

    @Nested
    @DisplayName("[findMemberStatus] 회원 상태 조회")
    class Describe_findMemberStatus {

        @Test
        @DisplayName("존재하는 회원 ID이면, 해당 회원의 상태를 반환한다.")
        void it_returns_member_status() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(defaultMember));
            when(defaultMember.getStatus()).thenReturn(Status.ONBOARDING);

            // when
            MemberStatusResponse response = memberQueryFacade.findMemberStatus(memberId);

            // then
            assertThat(response.status()).isEqualTo("ONBOARDING");
            verify(memberRepository).findById(memberId);
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID라면 MemberNotFoundException 발생")
        void it_throws_exception_if_member_not_found() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberQueryFacade.findMemberStatus(memberId))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("[findMemberSavedAddress] 회원의 저장된 주소 조회")
    class Describe_findMemberSavedAddress {

        @Test
        @DisplayName("존재하는 회원 ID이며, 주소가 1개 이상이면 해당 주소 목록을 반환")
        void it_returns_saved_addresses() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(defaultMember));
            when(addressRepository.findByMemberId(memberId)).thenReturn(List.of(homeAddress, schoolAddress));

            // when
            MemberSavedAddressResponse response = memberQueryFacade.findMemberSavedAddress(memberId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.homeTitle()).isEqualTo("우리집");
            assertThat(response.schoolTitle()).isEqualTo("학교");

            verify(memberRepository).findById(memberId);
            verify(addressRepository).findByMemberId(memberId);
        }

        @Test
        @DisplayName("존재하는 회원 ID지만, 저장된 주소가 없으면 예외 발생")
        void it_throws_exception_if_no_address() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(defaultMember));
            when(addressRepository.findByMemberId(memberId)).thenReturn(List.of());

            // when & then
            assertThatThrownBy(() -> memberQueryFacade.findMemberSavedAddress(memberId))
                    .isInstanceOf(MemberSavedAddressNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID이면 MemberNotFoundException 발생")
        void it_throws_exception_if_member_not_found() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberQueryFacade.findMemberSavedAddress(memberId))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("[completeOnboarding] 회원 온보딩 완료")
    class Describe_completeOnboarding {

        @Test
        @DisplayName("회원 상태가 ONBOARDING이면, 요청된 카테고리/주소 저장 후 상태를 STOP으로 변경")
        void it_completes_onboarding() {
            // given
            Member spyMember = spy(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(spyMember));
            doReturn(Status.ONBOARDING).when(spyMember).getStatus();

            MemberOnboardingRequest request = new MemberOnboardingRequest(
                    List.of("READING", "ENGLISH"),
                    "우리집", "서울시 강남구 ...", 37.0, 127.0,
                    "학교", "서울시 서초구 ...", 36.0, 127.1,
                    "새닉네임"
            );

            when(categoryRepository.findByName("READING")).thenReturn(Optional.of(categoryReading));
            when(categoryRepository.findByName("ENGLISH")).thenReturn(Optional.of(categoryEnglish));

            // when
            memberFacade.completeOnboarding(memberId, request);

            // then
            verify(spyMember).updateNickname("새닉네임");
            verify(spyMember).updateStatus(Status.STOP);
            verify(memberCategoryRepository).saveAll(anyList());
            verify(addressRepository, times(2)).save(any(Address.class));
        }

        @Test
        @DisplayName("회원 상태가 ONBOARDING이 아니라면 InvalidMemberStatusException")
        void it_throws_exception_when_member_status_is_not_onboarding() {
            // given
            Member spyMember = spy(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(spyMember));
            doReturn(Status.STOP).when(spyMember).getStatus();

            MemberOnboardingRequest request = new MemberOnboardingRequest(
                    List.of("READING", "ENGLISH"),
                    "우리집", "서울시 강남구 ...", 37.0, 127.0,
                    "학교", "서울시 서초구 ...", 36.0, 127.1,
                    "새닉네임"
            );

            // when & then
            assertThatThrownBy(() -> memberFacade.completeOnboarding(memberId, request))
                    .isInstanceOf(InvalidMemberStatusException.class);
        }

        @Test
        @DisplayName("카테고리 이름이 DB에 없다면 CategoryNotFoundException")
        void it_throws_category_not_found() {
            // given
            Member spyMember = spy(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(spyMember));
            doReturn(Status.ONBOARDING).when(spyMember).getStatus();

            MemberOnboardingRequest request = new MemberOnboardingRequest(
                    List.of("없는카테고리"),
                    "우리집", "서울시 강남구 ...", 37.0, 127.0,
                    "학교", "서울시 서초구 ...", 36.0, 127.1,
                    "새닉네임"
            );

            when(categoryRepository.findByName("없는카테고리")).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberFacade.completeOnboarding(memberId, request))
                    .isInstanceOf(CategoryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("[updateMemberInfo] 회원 정보 수정")
    class Describe_updateMemberInfo {

        @Test
        @DisplayName("존재하는 회원 ID + 유효한 DTO → 카테고리/주소 수정 후 최종정보 반환")
        void it_updates_member_info() {
            // given
            Member mockMemberForUpdate = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMemberForUpdate));
            when(mockMemberForUpdate.getId()).thenReturn(memberId);

            MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                    List.of("READING", "OTHERS"),
                    "집",
                    "서울시 강남구 역삼동 123",
                    127.0,
                    37.0,
                    "학교",
                    "서울시 관악구 관악로 1",
                    126.9528804,
                    37.4784966
            );

            Category categoryOthers = Category.builder().name("OTHERS").build();

            when(categoryRepository.findByName("READING")).thenReturn(Optional.of(categoryReading));
            when(categoryRepository.findByName("OTHERS")).thenReturn(Optional.of(categoryOthers));

            // 동적으로 카테고리 목록 상태를 유지
            List<MemberCategory> savedCats = new ArrayList<>();
            savedCats.add(MemberCategory.builder().category(categoryReading).member(mockMemberForUpdate).build());

            // findByMemberId() 호출 시마다 savedCats 반환
            when(memberCategoryRepository.findByMemberId(memberId)).thenAnswer(inv -> savedCats);

            // save() 시 새 카테고리가 없으면 추가
            when(memberCategoryRepository.save(any(MemberCategory.class))).thenAnswer(inv -> {
                MemberCategory mc = inv.getArgument(0);
                boolean isDuplicated = savedCats.stream()
                        .anyMatch(c -> c.getCategory().getName().equals(mc.getCategory().getName()));
                if (!isDuplicated) savedCats.add(mc);
                return mc;
            });

            // delete() 시 해당 카테고리 삭제
            doAnswer(inv -> {
                MemberCategory mc = inv.getArgument(0);
                savedCats.removeIf(c -> c.getCategory().getName().equals(mc.getCategory().getName()));
                return null;
            }).when(memberCategoryRepository).delete(any(MemberCategory.class));

            Address addrHome = Address.builder()
                    .id(100L)
                    .addressType(AddressType.HOME)
                    .title("집")
                    .roadAddress("서울시 강남구 역삼동 123")
                    .xCoordinate(127.0)
                    .yCoordinate(37.0)
                    .build();
            Address addrSchool = Address.builder()
                    .id(101L)
                    .addressType(AddressType.SCHOOL)
                    .title("학교")
                    .roadAddress("서울시 관악구 관악로 1")
                    .xCoordinate(126.9528804)
                    .yCoordinate(37.4784966)
                    .build();
            when(addressRepository.findByMemberId(memberId)).thenReturn(List.of(addrHome, addrSchool));

            // when
            MemberInfoResponse response = memberFacade.updateMemberInfo(memberId, request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.categories()).containsExactlyInAnyOrder("READING", "OTHERS");

            verify(memberRepository).findById(memberId);
            verify(memberCategoryRepository, atLeastOnce()).findByMemberId(memberId);
            verify(memberCategoryRepository).save(any(MemberCategory.class));
            verify(addressRepository, atLeastOnce()).findByMemberId(memberId);
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID면 MemberNotFoundException 발생")
        void it_throws_member_not_found_exception() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    memberFacade.updateMemberInfo(memberId, mock(MemberInfoUpdateRequest.class)))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("카테고리 중 하나라도 DB에 존재하지 않으면 예외 발생")
        void it_throws_category_not_found_exception() {
            // given
            Member mockMemberForUpdate = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMemberForUpdate));

            MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                    List.of("READING", "UNKNOWN_CAT"),
                    "집", "주소", 127.0, 37.0,
                    "학교", "주소2", 126.9, 37.4
            );

            when(categoryRepository.findByName("READING"))
                    .thenReturn(Optional.of(categoryReading));
            when(categoryRepository.findByName("UNKNOWN_CAT"))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberFacade.updateMemberInfo(memberId, request))
                    .isInstanceOf(CategoryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("[updateMemberStatus] 회원 상태 변경")
    class Describe_updateMemberStatus {

        @Test
        @DisplayName("유효한 회원 ID와 status라면, 회원 상태를 변경한다.")
        void it_updates_member_status() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(defaultMember));

            // when
            memberFacade.updateMemberStatus(memberId, "MOVE");

            // then
            verify(defaultMember).updateStatus(Status.MOVE);
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID면 MemberNotFoundException")
        void it_throws_exception_if_member_not_found() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberFacade.updateMemberStatus(memberId, "MOVE"))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("[updateMemberStatusByPlanChange] 플랜 변경 시 회원 상태 변경")
    class Describe_updateMemberStatusByPlanChange {

        @Test
        @DisplayName("플랜 미완료 시, 회원 상태를 FEEDBACK으로 변경")
        void it_updates_status_to_feedback_when_plan_is_not_completed() {
            // given
            MemberStatusChangeDto dto = new MemberStatusChangeDto(memberId, 10L);
            Plan mockPlan = mock(Plan.class);

            when(memberRepository.findById(dto.memberId())).thenReturn(Optional.of(defaultMember));
            when(planRepository.findById(dto.planId())).thenReturn(Optional.of(mockPlan));
            when(mockPlan.isCompleted()).thenReturn(false);

            // when
            memberFacade.updateMemberStatusByPlanChange(dto);

            // then
            verify(defaultMember).updateStatus(Status.FEEDBACK);
        }

        @Test
        @DisplayName("플랜이 이미 완료 상태면, 회원 상태 변경하지 않음")
        void it_does_not_update_status_when_plan_completed() {
            // given
            MemberStatusChangeDto dto = new MemberStatusChangeDto(memberId, 10L);
            Plan mockPlan = mock(Plan.class);

            when(memberRepository.findById(dto.memberId())).thenReturn(Optional.of(defaultMember));
            when(planRepository.findById(dto.planId())).thenReturn(Optional.of(mockPlan));
            when(mockPlan.isCompleted()).thenReturn(true);

            // when
            memberFacade.updateMemberStatusByPlanChange(dto);

            // then
            verify(defaultMember, never()).updateStatus(any());
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID면 MemberNotFoundException")
        void it_throws_MemberNotFoundException() {
            // given
            MemberStatusChangeDto dto = new MemberStatusChangeDto(memberId, 10L);
            when(memberRepository.findById(dto.memberId())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberFacade.updateMemberStatusByPlanChange(dto))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 플랜 ID면 PlanNotFoundException")
        void it_throws_PlanNotFoundException() {
            // given
            MemberStatusChangeDto dto = new MemberStatusChangeDto(memberId, 10L);
            when(memberRepository.findById(dto.memberId())).thenReturn(Optional.of(defaultMember));
            when(planRepository.findById(dto.planId())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberFacade.updateMemberStatusByPlanChange(dto))
                    .isInstanceOf(PlanNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("[updateMemberLocation] 회원 위치 정보 업데이트")
    class Describe_updateMemberLocation {

        @Test
        @DisplayName("Redis 서버 접근 가능하면, 회원 위치를 Redis에 저장")
        void it_saves_member_location_in_redis() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(defaultMember));
            MemberLocationDto locationDto = new MemberLocationDto(37.1234, 127.5678);

            // when
            memberFacade.updateMemberLocation(memberId, locationDto);

            // then
            verify(locationRedisRepository).saveMemberLocation(memberId, locationDto);
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID면 MemberNotFoundException")
        void it_throws_if_member_not_found() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberFacade.updateMemberLocation(memberId, new MemberLocationDto(37.0, 127.0)))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("Redis 서버 연결 실패시 RedisUnavailableException")
        void it_throws_redis_unavailable_exception() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(defaultMember));
            doThrow(new RedisConnectionFailureException("Redis 연결 실패"))
                    .when(locationRedisRepository).saveMemberLocation(anyLong(), any(MemberLocationDto.class));

            // when & then
            assertThatThrownBy(() -> memberFacade.updateMemberLocation(memberId, new MemberLocationDto(37.0, 127.0)))
                    .isInstanceOf(RedisUnavailableException.class);
        }
    }
}