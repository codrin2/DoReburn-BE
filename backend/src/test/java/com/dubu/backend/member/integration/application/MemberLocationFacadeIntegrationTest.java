package com.dubu.backend.member.integration.application;


import ch.hsr.geohash.GeoHash;
import com.dubu.backend.member.application.MemberLocationFacade;
import com.dubu.backend.member.application.api.PlanQueryApi;
import com.dubu.backend.member.domain.model.*;
import com.dubu.backend.member.domain.enums.OauthProvider;
import com.dubu.backend.global.container.TestContainerConfig;

import com.dubu.backend.member.domain.enums.Role;
import com.dubu.backend.member.domain.repository.TempMemberRepository;
import com.dubu.backend.member.infrastructure.MysqlCellCategoryMemberCountRepository;

import org.junit.jupiter.api.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@Tag("integration")
@ActiveProfiles("test")
@SpringBootTest
public class MemberLocationFacadeIntegrationTest extends TestContainerConfig {
    @Autowired
    private MemberLocationFacade memberLocationFacade;
    @Autowired
    private MysqlCellCategoryMemberCountRepository cellCategoryMemberCountRepository;
    @Autowired
    private TempMemberRepository memberRepository;

    @MockitoBean
    private PlanQueryApi planQueryApi;


    @Nested
    @DisplayName("[사용자 위치 정보 수정] 동시성 테스트")
    class Describe_updateMemberLocation{
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        List<TempMember> members = new ArrayList<>();
        List<MemberLocation> memberLocations = new ArrayList<>();
        List<Category> categories = new ArrayList<>();

        @BeforeEach
        void setUp(){
            categories.addAll(Arrays.asList(new Category(4L), new Category(5L), new Category(1L)));

            double minX = 126.97461004922717;
            double maxX = 126.98273075091804;
            double minY = 37.57067290620766;
            double maxY = 37.575607320632024;

            int data = 100;

            for(int i = 1; i <= data; i++) {
                // 사용자 초기화
                Point point = geometryFactory.createPoint(new Coordinate(minX + (maxX - minX) * Math.random(), minY + (maxY - minY) * Math.random()));

                TempMember member = TempMember.builder()
                        .nickname(String.format("member_%d", i))
                        .email(String.format("member_%d@email.com", i))
                        .oauthProvider(OauthProvider.KAKAO)
                        .oauthProviderId(String.format("oauth_%d", i))
                        .role(Role.USER)
                        .location(point)
                        .build();
                members.add(member);

                for(int j = 0; j < categories.size(); j++) {
                    cellCategoryMemberCountRepository.adjustCellCategoryMemberCount(GeoHash.withCharacterPrecision(point.getY(), point.getX(), 7).toBase32(), (long)j, 1);
                }
                // 이동 위치
                memberLocations.add(new MemberLocation(minX + (maxX - minX) * Math.random(), minY + (maxY - minY) * Math.random()));
            }

            memberRepository.saveAll(members);
        }

        @Test
        @DisplayName("여러 쓰레드가 동시 회원 위치 업데이트 요청 시, 데이터 일관성을 유지한다.")
        void it_handles_concurrent_updates() throws InterruptedException {
            when(planQueryApi.getRecentPlanTodoCategories(any(TempMember.class))).thenReturn(categories);

            int numOfRequests = 100;
            int numOfThreads = 50;

            ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);

            CountDownLatch doneSignal = new CountDownLatch(numOfRequests);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            for(int i = 1; i <= numOfRequests; i++){
                final int index = i;
                executorService.submit(() -> {
                    try{
                        memberLocationFacade.updateTempMemberLocation((long) index, memberLocations.get(index - 1));
                        successCount.getAndIncrement();
                    }catch (PessimisticLockingFailureException e) {
                        failCount.getAndIncrement();
                    }finally{
                        doneSignal.countDown();
                    }
                });
            }

            doneSignal.await();
            executorService.shutdown();
            assertThat(successCount.get()).isEqualTo(numOfRequests);
            assertThat(failCount.get()).isEqualTo(0);
        }
    }
}
