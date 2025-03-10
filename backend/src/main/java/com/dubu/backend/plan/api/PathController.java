package com.dubu.backend.plan.api;


import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.plan.application.PathFacade;
import com.dubu.backend.plan.api.response.RouteSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/routes")
public class PathController implements PathApi {
    private final PathFacade pathFacade;

    @GetMapping("/search")
    public SuccessResponse<List<RouteSearchResponse>> pathSearch(
            @RequestAttribute("memberId") Long memberId,
            @RequestParam("startX") Double startX,
            @RequestParam("startY") Double startY,
            @RequestParam("endX") Double endX,
            @RequestParam("endY") Double endY
    ) {
        List<RouteSearchResponse> response = pathFacade.getRoutesByStartAndDestination(memberId, startX, startY, endX, endY);

        return new SuccessResponse<>(response);
    }
}