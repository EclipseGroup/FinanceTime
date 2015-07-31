package com.eclipsegroup.dorel.financetime;


public interface FinanceServiceCallback {

    void serviceSuccess(Quote quote);

    void serviceFailure(Exception exception);
}
