package com.revolut.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class Balance {
    @JsonProperty(required = true)
    private BigDecimal currentBalance;

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "currentBalance=" + currentBalance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Balance balance = (Balance) o;
        return Objects.equals(currentBalance, balance.currentBalance);
    }

    @Override
    public int hashCode() {

        return Objects.hash(currentBalance);
    }
}
