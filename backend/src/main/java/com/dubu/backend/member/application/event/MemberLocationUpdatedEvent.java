package com.dubu.backend.member.application.event;

import ch.hsr.geohash.GeoHash;

public record MemberLocationUpdatedEvent(
        Long memberId,
        GeoHash oldGeoHash,
        GeoHash newGeoHash
) {
}
