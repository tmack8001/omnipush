package com.github.tmack8001.push;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Object representing a notification job, containing a list of device registration ids
 * and a payload to send to the devices.
 *
 * @author Trevor Mack (drummer8001@gmail.com)
 * @since 1.0.0
 */
public class NotificationJob {
    public List<String> devices = Collections.emptyList();
    public Map<String, Object> payload;

    private NotificationJob() {
    }

    public NotificationJob(@Nonnull List<String> devices, Map<String, Object> payload) {
        this.devices = devices;
        this.payload = payload;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
