package com.bijietech.model;

/**
 * Created by Lip on 2016/8/9 0009.
 * 查询多个产品的详情
 */
public class MultiQueryCmd extends Cmd{
    private String exchangeName;

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }
    @Override
    public String toString()
    {
        StringBuilder sb=new StringBuilder();
        sb.append("查询命令:").append(getContent()).append("\n")
                .append("交易所:").append(getExchangeName()).append("\n");
        return sb.toString();
    }
}
