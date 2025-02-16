package com.dubu.backend.member.infra.repository;

import com.dubu.backend.member.dto.MemberLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String GEO_KEY = "member_location";

    public void saveMemberLocation(Long memberId, MemberLocation memberLocation) {
        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

        Point point = new Point(memberLocation.x_coordinate(), memberLocation.y_coordinate());

        geoOperations.add(GEO_KEY, point, String.valueOf(memberId));
    }
}