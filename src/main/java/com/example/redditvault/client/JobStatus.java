package com.example.redditvault.client;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class JobStatus {
    @Id
    private String jobId;
    private String username;
    private String status;   // PENDING, RUNNING, COMPLETED, FAILED
    private String message;  // error/info message

    private int fetchedCount; // number of posts saved so far

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getFetchedCount() {
        return fetchedCount;
    }

    public void setFetchedCount(int fetchedCount) {
        this.fetchedCount = fetchedCount;
    }
}
