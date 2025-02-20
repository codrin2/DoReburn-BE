package com.dubu.backend.member.infra.repository;

import com.dubu.backend.member.dto.MemberLocationDto;
import com.dubu.backend.share.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.share.dto.response.MemberLocationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String GEO_KEY = "member_location";

    public void saveMemberLocation(Long memberId, MemberLocationDto memberLocationDto) {
        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();
        Point point = new Point(memberLocationDto.x_coordinate(), memberLocationDto.y_coordinate());
        geoOperations.add(GEO_KEY, point, String.valueOf(memberId));
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


