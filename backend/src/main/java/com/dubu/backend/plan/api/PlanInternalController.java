package com.dubu.backend.plan.api;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.plan.application.PlanFacade;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/plans")
@RequiredArgsConstructor
@Hidden
public class PlanInternalController {
    private final PlanFacade planFacade;

    @GetMapping("/recent/sub-paths")
    public SuccessResponse<?> getSubPathIdOfRecentPlans(
            @RequestParam("memberIds") List<Long> memberIds
    ){
        List<Long> subPathIds = planFacade.findSubPathIdOfRecentPlans(memberIds);
        return SuccessResponse.of(subPathIds);
    }
}
