package com.revolut.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TransactionStatus {
    @JsonProperty(required = true)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TransactionStatus{" +
                "status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionStatus status1 = (TransactionStatus) o;
        return Objects.equals(status, status1.status);
    }

    @Override
    public int hashCode() {

        return Objects.hash(status);
    }
}
