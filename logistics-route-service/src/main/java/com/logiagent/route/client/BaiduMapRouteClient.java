package com.logiagent.route.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logiagent.route.config.BaiduMapProperties;
import com.logiagent.route.dto.BaiduDrivingResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class BaiduMapRouteClient {

    private final BaiduMapProperties properties;
    private final ObjectMapper objectMapper;

    public BaiduMapRouteClient(BaiduMapProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public BaiduDrivingResponse driving(String origin,
                                        String destination,
                                        String waypoints,
                                        int tactics,
                                        String coordType,
                                        String retCoordType) {
        URI uri = UriComponentsBuilder.fromUriString(properties.getDrivingUrl())
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("ak", properties.getAk())
                .queryParam("tactics", tactics)
                .queryParam("waypoints", waypoints)
                .queryParam("coord_type", coordType)
                .queryParam("ret_coordtype", retCoordType)
                .queryParam("steps_info", 1)
                .build()
                .encode()
                .toUri();
        String body = restClient().get().uri(uri).retrieve().body(String.class);
        return parseResponse(body);
    }

    BaiduDrivingResponse parseResponse(String body) {
        try {
            return objectMapper.readValue(body, BaiduDrivingResponse.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to parse Baidu Map response", ex);
        }
    }

    private RestClient restClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getTimeoutMs());
        factory.setReadTimeout(properties.getTimeoutMs());
        return RestClient.builder().requestFactory(factory).build();
    }
}
