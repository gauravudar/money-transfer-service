package com.revolut.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    @JsonIgnore
    private long accountId;

    @JsonProperty(required = true)
    private String userName;

    @JsonProperty(required = true)
    private BigDecimal balance;

    @JsonProperty(required = true)
    private String currencyCode;

    public Account() {
    }

    public Account(String userName, BigDecimal balance, String currencyCode) {
        this.userName = userName;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public Account(long accountId, String userName, BigDecimal balance, String currencyCode) {
        this.accountId = accountId;
        this.userName = userName;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId &&
                Objects.equals(userName, account.userName) &&
                Objects.equals(balance, account.balance) &&
                Objects.equals(currencyCode, account.currencyCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(accountId, userName, balance, currencyCode);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userName='" + userName + '\'' +
                ", balance=" + balance +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
