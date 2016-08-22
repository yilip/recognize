package com.bijietech.model;

/**
 * Created by Lip on 2016/8/9 0009.
 * 查询市场行情的cmd
 */
public class QueryCmd extends Cmd{
    private String exchangeName;
    private String productName;
    private String symbol;

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    @Override
    public String toString()
    {
        StringBuilder sb=new StringBuilder();
        sb.append("查询命令:").append(getContent()).append("\n")
                .append("交易所:").append(getExchangeName()).append("\n")
                .append("产品名称:").append(getProductName()).append("\n")
                .append("产品代码:").append(getSymbol());
        return sb.toString();
    }
}
