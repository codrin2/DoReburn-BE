package com.dubu.backend.member.domain;

import com.dubu.backend.member.dto.response.AddressSearchResponse;

import java.util.List;

public interface AddressSearchService {
    List<AddressSearchResponse> search(String query);
}