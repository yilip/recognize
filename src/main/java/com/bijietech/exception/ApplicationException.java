package com.bijietech.exception;

/**
 * 
 * Copyright (C) 上海比捷网络科技有限公司.
 * 
 * Description: 异常类
 *
 * @author haolingfeng
 * @version 1.0
 *
 */
public class ApplicationException extends RuntimeException {
	private static final long serialVersionUID = -6635780414655074781L;

	/**
	 * 构造方法
	 * @param arg0 信息
	 * @param arg1 原因
	 */
	public ApplicationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * 构造方法
	 * @param arg0 信息
	 */
	public ApplicationException(String arg0) {
		super(arg0);
	}

	/**
	 * 构造方法
	 * @param arg0 原因
	 */
	public ApplicationException(Throwable arg0) {
		super(arg0);
	}

}
