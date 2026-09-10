/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.recruitai.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

/** 记录候选人关键决定的岗位证据、人工责任、公平性和申诉闭环。 */
@Service
public class CandidateDecisionAuditService {
    public AuditResult audit(AuditRequest request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        int evidenced = 0;

        if (!request.aiNoticeDelivered()) blockers.add("候选人尚未收到 AI 辅助使用告知");
        if (!request.protectedAttributesExcluded()) blockers.add("决策使用了受保护属性或代理变量");
        if (!request.humanDecisionMakerAssigned()) blockers.add("关键招聘决定未指定人工责任人");
        if (blank(request.decisionRationale())) blockers.add("缺少岗位相关的决定理由");
        if (request.recommenderId().equals(request.reviewerId())) blockers.add("推荐提交人与最终复核人必须分离");

        for (CriterionResult criterion : request.criteria()) {
            if (!criterion.jobRelated()) blockers.add("评价项与岗位无关: " + criterion.name());
            if (blank(criterion.evidenceReference())) blockers.add("评价项缺少证据: " + criterion.name());
            else evidenced++;
        }
        double coverage = round(evidenced * 100d / request.criteria().size());

        if (!blockers.isEmpty()) {
            actions.add("停止候选人状态变更并由招聘负责人复核决定依据");
            return result(Decision.BLOCKED, coverage, request, blockers, actions);
        }

        if (request.accommodationRequested() && !request.accommodationAddressed()) {
            actions.add("保持候选人流程状态，先完成人员便利或合理调整安排");
            return result(Decision.HOLD, coverage, request, blockers, actions);
        }

        if (request.completedScorecards() < request.requiredScorecards()) {
            actions.add("补齐独立面试评分卡后重新提交决定");
        }
        if (request.decision() == HiringDecision.REJECT && !request.appealChannelReady()) {
            actions.add("建立候选人询问和申诉渠道");
        }
        if (!request.retentionPolicyApplied()) actions.add("应用候选人数据保留与删除策略");
        if (!actions.isEmpty()) {
            return result(Decision.PANEL_REVIEW, coverage, request, blockers, actions);
        }

        actions.add("记录人工决定、岗位证据、标准版本和候选人通知");
        return result(Decision.RECORDED, coverage, request, blockers, actions);
    }

    private AuditResult result(Decision decision, double coverage, AuditRequest request,
                               List<String> blockers, List<String> actions) {
        return new AuditResult(decision, coverage, List.copyOf(blockers), List.copyOf(actions),
                auditKey(request));
    }

    private String auditKey(AuditRequest request) {
        String source = String.join("|", request.applicationId(), request.candidateId(),
                request.criteriaVersion(), request.decision().name(), request.reviewerId(),
                request.decisionRationale());
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(source.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private double round(double value) {
        return Math.round(value * 100d) / 100d;
    }

    public record AuditRequest(
            @NotBlank String applicationId,
            @NotBlank String candidateId,
            @NotBlank String criteriaVersion,
            @NotNull HiringDecision decision,
            @NotEmpty List<@Valid CriterionResult> criteria,
            boolean aiNoticeDelivered,
            boolean protectedAttributesExcluded,
            boolean humanDecisionMakerAssigned,
            @NotBlank String decisionRationale,
            @NotBlank String recommenderId,
            @NotBlank String reviewerId,
            @Min(1) int requiredScorecards,
            @Min(0) int completedScorecards,
            boolean accommodationRequested,
            boolean accommodationAddressed,
            boolean appealChannelReady,
            boolean retentionPolicyApplied
    ) {}

    public record CriterionResult(@NotBlank String name, boolean jobRelated,
                                  String evidenceReference, @Min(0) @Max(100) int score) {}
    public record AuditResult(Decision decision, double evidenceCoveragePercent,
                              List<String> blockers, List<String> actions, String auditKey) {}

    public enum HiringDecision { ADVANCE, OFFER, REJECT }
    public enum Decision { RECORDED, PANEL_REVIEW, HOLD, BLOCKED }
}
