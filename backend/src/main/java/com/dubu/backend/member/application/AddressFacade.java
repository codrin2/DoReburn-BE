package com.dubu.backend.member.application;

import com.dubu.backend.member.api.response.AddressSearchResponse;
import com.dubu.backend.member.application.api.PlaceApi;
import com.dubu.backend.member.application.api.RoadAddressApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressFacade {
    private final PlaceApi placeApi;
    private final RoadAddressApi roadAddressApi;

    public List<AddressSearchResponse> searchPlaces(String query) {
        List<AddressSearchResponse> placeResults = placeApi.search(query);
        List<AddressSearchResponse> roadAddressResults = roadAddressApi.search(query);

        return mergeResultsUsingRoundRobin(placeResults, roadAddressResults);
    }

    private List<AddressSearchResponse> mergeResultsUsingRoundRobin(
            List<AddressSearchResponse> first,
            List<AddressSearchResponse> second
    ) {
        List<AddressSearchResponse> merged = new ArrayList<>();
        Iterator<AddressSearchResponse> firstIterator = first.iterator();
        Iterator<AddressSearchResponse> secondIterator = second.iterator();

        while (firstIterator.hasNext() || secondIterator.hasNext()) {
            if (firstIterator.hasNext()) merged.add(firstIterator.next());
            if (secondIterator.hasNext()) merged.add(secondIterator.next());
        }

        return merged;
    }
}