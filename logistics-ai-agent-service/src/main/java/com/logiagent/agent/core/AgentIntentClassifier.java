package com.logiagent.agent.core;

import com.logiagent.agent.enums.AgentIntent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AgentIntentClassifier {

    private static final Pattern WAYBILL_PATTERN = Pattern.compile("(WB\\d{8,})", Pattern.CASE_INSENSITIVE);

    public AgentIntent classify(String message) {
        if (!StringUtils.hasText(message)) {
            return AgentIntent.UNKNOWN;
        }
        if (containsAny(message, "为什么", "异常", "没签收", "未签收", "不更新", "分析")) {
            return AgentIntent.WAYBILL_EXCEPTION_DIAGNOSIS;
        }
        if (extractWaybillNo(message) != null) {
            return AgentIntent.WAYBILL_QUERY;
        }
        return AgentIntent.UNKNOWN;
    }

    public String extractWaybillNo(String message) {
        if (!StringUtils.hasText(message)) {
            return null;
        }
        Matcher matcher = WAYBILL_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1).toUpperCase();
        }
        return null;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
