package com.logiagent.route.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaiduDrivingResponse {

    private Integer status;
    private String message;
    private Result result;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        private List<Route> routes = new ArrayList<>();

        public List<Route> getRoutes() {
            return routes;
        }

        public void setRoutes(List<Route> routes) {
            this.routes = routes;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {

        private BigDecimal distance;
        private BigDecimal duration;
        private BigDecimal toll;
        @JsonProperty("traffic_condition")
        private String trafficCondition;
        private List<Step> steps = new ArrayList<>();

        public BigDecimal getDistance() {
            return distance;
        }

        public void setDistance(BigDecimal distance) {
            this.distance = distance;
        }

        public BigDecimal getDuration() {
            return duration;
        }

        public void setDuration(BigDecimal duration) {
            this.duration = duration;
        }

        public BigDecimal getToll() {
            return toll;
        }

        public void setToll(BigDecimal toll) {
            this.toll = toll;
        }

        public String getTrafficCondition() {
            return trafficCondition;
        }

        public void setTrafficCondition(String trafficCondition) {
            this.trafficCondition = trafficCondition;
        }

        public List<Step> getSteps() {
            return steps;
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Step {

        private String instruction;
        private BigDecimal distance;
        private BigDecimal duration;
        private String path;

        public String getInstruction() {
            return instruction;
        }

        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }

        public BigDecimal getDistance() {
            return distance;
        }

        public void setDistance(BigDecimal distance) {
            this.distance = distance;
        }

        public BigDecimal getDuration() {
            return duration;
        }

        public void setDuration(BigDecimal duration) {
            this.duration = duration;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
