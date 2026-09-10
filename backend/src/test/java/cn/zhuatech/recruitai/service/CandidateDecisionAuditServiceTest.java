/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.recruitai.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CandidateDecisionAuditServiceTest {
    private final CandidateDecisionAuditService service = new CandidateDecisionAuditService();

    @Test
    void recordsEvidenceBasedHumanDecision() {
        var result = service.audit(request(CandidateDecisionAuditService.HiringDecision.ADVANCE,
                true, true, 2, 2, false, true, true, true));
        assertThat(result.decision()).isEqualTo(CandidateDecisionAuditService.Decision.RECORDED);
        assertThat(result.evidenceCoveragePercent()).isEqualTo(100d);
        assertThat(result.auditKey()).hasSize(64);
    }

    @Test
    void holdsDecisionUntilAccommodationIsAddressed() {
        var result = service.audit(request(CandidateDecisionAuditService.HiringDecision.ADVANCE,
                true, true, 2, 2, true, false, true, true));
        assertThat(result.decision()).isEqualTo(CandidateDecisionAuditService.Decision.HOLD);
    }

    @Test
    void sendsIncompleteRejectionToPanelReview() {
        var result = service.audit(request(CandidateDecisionAuditService.HiringDecision.REJECT,
                true, true, 3, 1, false, true, false, false));
        assertThat(result.decision()).isEqualTo(CandidateDecisionAuditService.Decision.PANEL_REVIEW);
        assertThat(result.actions()).hasSize(3);
    }

    @Test
    void blocksProtectedAttributesAndMissingEvidence() {
        var base = request(CandidateDecisionAuditService.HiringDecision.REJECT,
                false, true, 2, 2, false, true, true, true);
        var criteria = List.of(new CandidateDecisionAuditService.CriterionResult(
                "年龄偏好", false, "", 20));
        var unsafe = new CandidateDecisionAuditService.AuditRequest(base.applicationId(), base.candidateId(),
                base.criteriaVersion(), base.decision(), criteria, true, false, true,
                "不符合岗位要求", "engine", "reviewer", 2, 2, false, true, true, true);
        var result = service.audit(unsafe);
        assertThat(result.decision()).isEqualTo(CandidateDecisionAuditService.Decision.BLOCKED);
        assertThat(result.blockers()).hasSize(3);
    }

    private CandidateDecisionAuditService.AuditRequest request(
            CandidateDecisionAuditService.HiringDecision decision, boolean protectedExcluded,
            boolean humanAssigned, int required, int completed, boolean accommodationRequested,
            boolean accommodationAddressed, boolean appeal, boolean retention) {
        var criteria = List.of(
                new CandidateDecisionAuditService.CriterionResult("Java 经验", true, "scorecard:1", 85),
                new CandidateDecisionAuditService.CriterionResult("系统设计", true, "interview:2", 82));
        return new CandidateDecisionAuditService.AuditRequest("APP-100", "C-100", "backend-v3",
                decision, criteria, true, protectedExcluded, humanAssigned, "岗位能力证据满足阶段要求",
                "recommendation-engine", "reviewer-a", required, completed, accommodationRequested,
                accommodationAddressed, appeal, retention);
    }
}
