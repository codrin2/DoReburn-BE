package com.dubu.backend.share.infrastructure;

import com.dubu.backend.member.core.exception.RedisUnavailableException;
import com.dubu.backend.share.domain.SurroundingMember;
import com.dubu.backend.share.domain.repository.MemberLocationRepository;
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

@Component("share.redisMemberLocationRepository")
@RequiredArgsConstructor
public class RedisMemberLocationRepository implements MemberLocationRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String GEO_KEY = "member_location";

    public List<SurroundingMember> findMemberLocations(Long memberId, double x_coordinate, double y_coordinate, double radius){
        GeoOperations<String, String> operations = redisTemplate.opsForGeo();
        try {
            GeoResults<RedisGeoCommands.GeoLocation<String>> result = operations.search(
                    GEO_KEY,
                    GeoReference.fromCoordinate(x_coordinate, y_coordinate),
                    new Distance(radius, Metrics.KILOMETERS),
                    RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeCoordinates().limit(100)
            );
            return convertGeoResultToMemberLocationInfo(memberId, result);
        }catch(RedisConnectionFailureException e){
            throw new RedisUnavailableException();
        }
    }

    private List<SurroundingMember> convertGeoResultToMemberLocationInfo(Long memberId, GeoResults<RedisGeoCommands.GeoLocation<String>> result){
        if(result == null || result.getContent().isEmpty()) return List.of();

        List<SurroundingMember> memberLocations = new ArrayList<>();
        for(GeoResult<RedisGeoCommands.GeoLocation<String>> georesult:result.getContent()){
            RedisGeoCommands.GeoLocation<String> content = georesult.getContent();
            long surroundingMemberId = Long.parseLong(content.getName());

            if(surroundingMemberId == memberId) continue;

            Point point = content.getPoint();
            memberLocations.add(SurroundingMember.of(surroundingMemberId, point.getX(), point.getY()));
        }
        return memberLocations;
    }
}
