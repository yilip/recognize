package com.bijietech.model;

/**
 * Created by Lip on 2016/8/9 0009.
 * 基本的客户语句
 */
public class NormalCmd extends Cmd {
    @Override
    public String toString()
    {
        return "普通命令:"+getContent();
    }
}
