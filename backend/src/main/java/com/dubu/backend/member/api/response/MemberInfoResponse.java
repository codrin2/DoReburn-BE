package com.dubu.backend.member.api.response;

import com.dubu.backend.member.domain.Address;
import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.AddressType;
import com.dubu.backend.todo.domain.Category;

import java.util.List;

public record MemberInfoResponse(
        String email,
        String nickname,
        List<String> categories,
        String homeTitle,
        String homeAddress,
        double homeAddressX,
        double homeAddressY,
        String schoolTitle,
        String schoolAddress,
        double schoolAddressX,
        double schoolAddressY
) {
    public static MemberInfoResponse of(Member member, List<Category> categories, List<Address> addresses) {
        Address home = addresses.stream()
                .filter(address -> address.getAddressType() == AddressType.HOME)
                .findFirst()
                .orElse(null);

        Address school = addresses.stream()
                .filter(address -> address.getAddressType() == AddressType.SCHOOL)
                .findFirst()
                .orElse(null);

        return new MemberInfoResponse(
                member.getEmail(),
                member.getNickname(),
                categories.stream().map(Category::getName).toList(),
                home != null ? home.getTitle() : null,
                home != null ? home.getRoadAddress() : null,
                home != null ? home.getXCoordinate() : 0.0,
                home != null ? home.getYCoordinate() : 0.0,
                school != null ? school.getTitle() : null,
                school != null ? school.getRoadAddress() : null,
                school != null ? school.getXCoordinate() : 0.0,
                school != null ? school.getYCoordinate() : 0.0
        );
    }
}