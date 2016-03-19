package com.github.tmack8001.push;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * A batch request object containing an identification string, a boolean if this should just test
 * interaction with GCM or actually delivery messages to devices, and a list of {@link NotificationJob}s to deliver.
 *
 * @author Trevor Mack (drummer8001@gmail.com)
 * @since 1.0.0
 */
public class BatchRequest {
    private String id;
    private Boolean dryRun;
    private List<NotificationJob> gcm = Collections.emptyList();

    private BatchRequest() {
    }

    public BatchRequest(String id, Boolean dryRun, @Nonnull List<NotificationJob> gcm) {
        this.id = id;
        this.dryRun = dryRun;
        this.gcm = gcm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    public List<NotificationJob> getGcm() {
        return gcm;
    }

    public void setGcm(List<NotificationJob> gcm) {
        this.gcm = gcm;
    }
}
