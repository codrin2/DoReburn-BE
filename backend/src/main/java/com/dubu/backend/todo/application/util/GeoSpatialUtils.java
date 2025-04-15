package com.dubu.backend.todo.application.util;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoSpatialUtils {
    private static final double EARTH_RADIUS = 6371;

    public static Map<String, GeoHash> deriveBoundingBoxCornerGeoHash(double latitude, double longitude, double radius) {
        // Bounding box 구하기
        double latDelta = Math.toDegrees(radius / EARTH_RADIUS);
        double minLat = latitude - latDelta;
        double maxLat = longitude + latDelta;

        double lonDelta = Math.toDegrees(Math.asin(Math.sin(radius/ EARTH_RADIUS) / Math.cos(Math.toRadians(latitude))));
        double minLon = latitude - lonDelta;
        double maxLon = longitude + lonDelta;

        HashMap<String, GeoHash> boundingBoxCornerHashMap = new HashMap<>();
        boundingBoxCornerHashMap.put("NE", GeoHash.withCharacterPrecision(maxLat, maxLon, 7));
        boundingBoxCornerHashMap.put("NW", GeoHash.withCharacterPrecision(maxLat, minLon, 7));
        boundingBoxCornerHashMap.put("SE", GeoHash.withCharacterPrecision(minLat, maxLon, 7));
        boundingBoxCornerHashMap.put("SW", GeoHash.withCharacterPrecision(minLat, minLon, 7));

        return boundingBoxCornerHashMap;
    }

    public static List<GeoHash> deriveGeoHashInBoundingBox(GeoHash nwGeoHash, GeoHash neGeoHash, GeoHash swGeoHash, double latitude, double longitude, double radius) {
        GeoHash curRowHash = GeoHash.fromGeohashString(nwGeoHash.toString());
        GeoHash endRowHash = GeoHash.fromGeohashString(neGeoHash.toString());

        List<GeoHash> boundingBoxGeoHashes = new ArrayList<>();

        while(true){
            GeoHash curColHash = GeoHash.fromGeohashString(curRowHash.toString());
            GeoHash endColHash = GeoHash.fromGeohashString(endRowHash.toString());

            while(true){
                if (withinCircle(curColHash, latitude, longitude, radius)) {
                    boundingBoxGeoHashes.add(curColHash);
                }

                if(curColHash.equals(endColHash)) break;
                curColHash = curColHash.getEasternNeighbour();
            }

            if(curRowHash.equals(swGeoHash)){
                break;
            }
            curRowHash = curRowHash.getSouthernNeighbour();
            endRowHash = endRowHash.getSouthernNeighbour();
        }

        return boundingBoxGeoHashes;
    }

    private static boolean withinCircle(GeoHash geoHash, double latitude, double longitude, double radius){
        WGS84Point geoHashCenterPoint = geoHash.getBoundingBoxCenter();

        double originLatRad = Math.toRadians(latitude);
        double originLonRad = Math.toRadians(longitude);
        double destLatRad = Math.toRadians(geoHashCenterPoint.getLatitude());
        double destLonRad = Math.toRadians(geoHashCenterPoint.getLongitude());

        double dLat = destLatRad - originLatRad;
        double dLon = destLonRad - originLonRad;

        // 하버 사인 공식을 통해 두 점 사이의 거리 구하기
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(originLatRad) * Math.cos(destLatRad) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c <= radius;
    }


}
