package com.dubu.backend.member.application.api;

import com.dubu.backend.member.api.response.AddressSearchResponse;

import java.util.List;

public interface PlaceApi {
    List<AddressSearchResponse> search(String query);
}