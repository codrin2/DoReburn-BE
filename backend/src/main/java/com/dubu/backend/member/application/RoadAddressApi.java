package com.dubu.backend.member.application;

import com.dubu.backend.member.dto.response.AddressSearchResponse;

import java.util.List;

public interface RoadAddressApi {
    List<AddressSearchResponse> search(String query);
}