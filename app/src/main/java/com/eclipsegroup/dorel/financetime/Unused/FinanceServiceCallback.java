package com.eclipsegroup.dorel.financetime.Unused;


public interface FinanceServiceCallback {

    void serviceSuccess(Quote quote);

    void serviceFailure(Exception exception);
}
