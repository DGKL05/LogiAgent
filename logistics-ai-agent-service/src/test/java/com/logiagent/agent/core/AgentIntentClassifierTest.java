package com.logiagent.agent.core;

import com.logiagent.agent.enums.AgentIntent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgentIntentClassifierTest {

    private final AgentIntentClassifier classifier = new AgentIntentClassifier();

    @Test
    void classifyExceptionDiagnosisQuestion() {
        String message = "analyze waybill WB20260509204042075 why it is not signed";

        AgentIntent intent = classifier.classify(message);

        assertThat(intent).isEqualTo(AgentIntent.WAYBILL_EXCEPTION_DIAGNOSIS);
        assertThat(classifier.extractWaybillNo(message)).isEqualTo("WB20260509204042075");
    }

    @Test
    void classifyWaybillQueryQuestion() {
        String message = "query waybill WB20260509204042075 location";

        AgentIntent intent = classifier.classify(message);

        assertThat(intent).isEqualTo(AgentIntent.WAYBILL_QUERY);
    }

    @Test
    void classifyRoutePlanningQuestion() {
        String message = "use baidu map to plan real route from Guangzhou station to Shenzhen station fastest";

        AgentIntent intent = classifier.classify(message);

        assertThat(intent).isEqualTo(AgentIntent.ROUTE_PLANNING);
    }

    @Test
    void classifyDispatchSuggestionQuestion() {
        AgentIntent intent = classifier.classify("which station has the heaviest load today");

        assertThat(intent).isEqualTo(AgentIntent.DISPATCH_SUGGESTION);
    }

    @Test
    void classifyDailyReportQuestion() {
        AgentIntent intent = classifier.classify("generate today's logistics daily report");

        assertThat(intent).isEqualTo(AgentIntent.DAILY_REPORT);
    }
}
