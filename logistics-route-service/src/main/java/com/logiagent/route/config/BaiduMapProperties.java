package com.logiagent.route.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "baidu.map")
public class BaiduMapProperties {

    private boolean enabled;
    private String ak;
    private String drivingUrl = "https://api.map.baidu.com/directionlite/v1/driving";
    private int timeoutMs = 3000;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getDrivingUrl() {
        return drivingUrl;
    }

    public void setDrivingUrl(String drivingUrl) {
        this.drivingUrl = drivingUrl;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}
