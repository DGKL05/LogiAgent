package com.logiagent.agent.core;

import com.logiagent.agent.enums.AgentIntent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgentIntentClassifierTest {

    private final AgentIntentClassifier classifier = new AgentIntentClassifier();

    @Test
    void classifyExceptionDiagnosisQuestion() {
        String message = "帮我分析运单 WB20260509204042075 为什么还没签收";

        AgentIntent intent = classifier.classify(message);

        assertThat(intent).isEqualTo(AgentIntent.WAYBILL_EXCEPTION_DIAGNOSIS);
        assertThat(classifier.extractWaybillNo(message)).isEqualTo("WB20260509204042075");
    }

    @Test
    void classifyWaybillQueryQuestion() {
        String message = "查询一下 WB20260509204042075 到哪里了";

        AgentIntent intent = classifier.classify(message);

        assertThat(intent).isEqualTo(AgentIntent.WAYBILL_QUERY);
    }
}
