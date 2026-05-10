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
        String text = message.toLowerCase();
        if (containsAny(text, "日报", "物流日报", "生成报告", "今天统计", "运营报告", "今日概况",
                "daily report", "logistics report", "operation report")) {
            return AgentIntent.DAILY_REPORT;
        }
        if (containsAny(text, "调度", "压力最大", "网点压力", "负载", "派件压力", "哪个网点忙", "任务最多", "异常件最多",
                "dispatch", "station load", "heaviest load", "busiest station", "most tasks")) {
            return AgentIntent.DISPATCH_SUGGESTION;
        }
        if (containsAny(text, "路线", "规划", "怎么走", "最快", "最短", "百度地图", "真实道路",
                "route", "plan", "baidu", "fastest", "shortest")) {
            return AgentIntent.ROUTE_PLANNING;
        }
        if (containsAny(text, "为什么", "异常", "没签收", "未签收", "不更新", "分析",
                "why", "exception", "not signed", "not update", "analyze")) {
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
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
