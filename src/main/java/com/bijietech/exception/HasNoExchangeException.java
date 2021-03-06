package com.bijietech.exception;

/**
 * Created by Lip on 2016/8/8 0008.
 */
public class HasNoExchangeException extends RuntimeException {
    /**
     * 构造方法
     * @param arg0 信息
     * @param arg1 原因
     */
    public HasNoExchangeException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * 构造方法
     * @param arg0 信息
     */
    public HasNoExchangeException(String arg0) {
        super(arg0);
    }

    /**
     * 构造方法
     * @param arg0 原因
     */
    public HasNoExchangeException(Throwable arg0) {
        super(arg0);
    }
}
