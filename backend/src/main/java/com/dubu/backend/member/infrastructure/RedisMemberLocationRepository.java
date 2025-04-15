package com.dubu.backend.member.infrastructure;

import com.dubu.backend.member.domain.model.MemberLocation;
import com.dubu.backend.member.domain.repository.MemberLocationRepository;
import com.dubu.backend.member.core.exception.RedisUnavailableException;
import com.dubu.backend.todo.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.todo.dto.response.MemberLocationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisMemberLocationRepository implements MemberLocationRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String GEO_KEY = "member_location";

    @Override
    public void saveMemberLocation(Long memberId, MemberLocation memberLocation) {
        try {
            GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();
            Point point = new Point(memberLocation.x_coordinate(), memberLocation.y_coordinate());
            geoOperations.add(GEO_KEY, point, String.valueOf(memberId));
        } catch (RedisConnectionFailureException e) {
            throw new RedisUnavailableException();
        }
    }

    public List<MemberLocationInfo> findMemberLocations(Long memberId, SurroundingMemberQueryRequest request){
        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();
        GeoResults<RedisGeoCommands.GeoLocation<String>> result = geoOperations.search(
                GEO_KEY,
                GeoReference.fromCoordinate(request.x_coordinate(), request.y_coordinate()),
                new Distance(request.radius(), Metrics.KILOMETERS),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeCoordinates()
        );

        return convertGeoResultToMemberLocationInfo(memberId, result);
    }

    private List<MemberLocationInfo> convertGeoResultToMemberLocationInfo(Long memberId, GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults){
        List<MemberLocationInfo> memberLocationInfos = new ArrayList<>();

        if(geoResults == null){
            return null;
        }

        for (GeoResult<RedisGeoCommands.GeoLocation<String>> geoResult : geoResults.getContent()) {
            RedisGeoCommands.GeoLocation<String> geoLocation = geoResult.getContent();
            long surroundingMemberId = Long.parseLong(geoLocation.getName());

            if(surroundingMemberId == memberId) continue;

            Point point = geoLocation.getPoint();

            memberLocationInfos.add(MemberLocationInfo.of(surroundingMemberId, point.getX(), point.getY()));
        }

        return memberLocationInfos;
    }
}


