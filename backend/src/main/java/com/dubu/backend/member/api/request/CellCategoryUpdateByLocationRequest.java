package com.dubu.backend.member.api.request;

import ch.hsr.geohash.GeoHash;

public record CellCategoryUpdateByLocationRequest(GeoHash oldGeoHash, GeoHash newGeoHash) {
}
