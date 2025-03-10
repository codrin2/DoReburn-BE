package com.dubu.backend.plan.api;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.plan.application.PlanFacade;
import com.dubu.backend.plan.api.request.PlanCreateRequest;
import com.dubu.backend.plan.api.request.PlanFeedbackCreateRequest;
import com.dubu.backend.plan.api.response.FeedbackWritePageInfoResponse;
import com.dubu.backend.plan.api.response.PlanRecentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController implements PlanApi {
    private final PlanFacade planFacade;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SuccessResponse<Map<String, Long>> createPlan(
            @RequestAttribute("memberId") Long memberId,
            @RequestParam("startX") Double startX,
            @RequestParam("startY") Double startY,
            @RequestParam("endX") Double endX,
            @RequestParam("endY") Double endY,
            @RequestBody PlanCreateRequest planCreateRequest
    ) {
        Long planId = planFacade.savePlan(memberId, startX, startY, endX, endY, planCreateRequest);

        return new SuccessResponse<>(Map.of("planId", planId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{planId}/feedbacks")
    public SuccessResponse<Map<String, Long>> createPlanFeedback(
            @RequestAttribute("memberId") Long memberId,
            @PathVariable Long planId,
            @RequestBody PlanFeedbackCreateRequest planFeedbackCreateRequest
    ) {
        Long feedbackId = planFacade.savePlanFeedback(memberId, planId, planFeedbackCreateRequest);

        return new SuccessResponse<>(Map.of("feedbackId", feedbackId));
    }

    @GetMapping("/recent")
    public SuccessResponse<PlanRecentResponse> getRecentPlan(
            @RequestAttribute("memberId") Long memberId
    ) {
        PlanRecentResponse response = planFacade.findRecentPlan(memberId);

        return new SuccessResponse<>(response);
    }

    @GetMapping("/feedbacks")
    public SuccessResponse<FeedbackWritePageInfoResponse> getFeedbackWritePageInfo(
            @RequestAttribute("memberId") Long memberId
    ) {
        FeedbackWritePageInfoResponse response = planFacade.findFeedbackWritePageInfo(memberId);

        return new SuccessResponse<>(response);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/move-complete")
    public void completeMove(
            @RequestAttribute("memberId") Long memberId
    ) {
        planFacade.completeMove(memberId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deletePlan(
            @RequestAttribute("memberId") Long memberId,
            @RequestParam("planId") Long planId
    ){
        planFacade.removePlan(memberId, planId);
    }
}