package com.dubu.backend.member.presentation;

import com.dubu.backend.global.domain.SuccessResponse;
import com.dubu.backend.member.application.AddressFacade;
import com.dubu.backend.member.dto.response.AddressSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class AddressController implements AddressApi {
    private final AddressFacade addressFacade;

    @GetMapping("/search")
    public SuccessResponse<List<AddressSearchResponse>> searchPlaces(
            @RequestParam("query") String query
    ) {
        List<AddressSearchResponse> searchPlaces = addressFacade.searchPlaces(query);

        return new SuccessResponse<>(searchPlaces);
    }
}