package com.logiagent.route.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logiagent.route.config.BaiduMapProperties;
import com.logiagent.route.dto.BaiduDrivingResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaiduMapRouteClientTest {

    @Test
    void parseResponseReadsBaiduDrivingJsonBody() {
        BaiduMapRouteClient client = new BaiduMapRouteClient(new BaiduMapProperties(), new ObjectMapper());
        String body = """
                {"status":0,"message":"ok","result":{"routes":[{"distance":135200,"duration":7800,"toll":58,"traffic_condition":"smooth","steps":[{"instruction":"start","distance":1000,"duration":60,"path":"1,2;3,4"}]}]}}
                """;

        BaiduDrivingResponse response = client.parseResponse(body);

        assertThat(response.getStatus()).isZero();
        assertThat(response.getResult().getRoutes()).hasSize(1);
        assertThat(response.getResult().getRoutes().get(0).getDistance()).isEqualByComparingTo("135200");
        assertThat(response.getResult().getRoutes().get(0).getSteps().get(0).getInstruction()).isEqualTo("start");
    }
}
