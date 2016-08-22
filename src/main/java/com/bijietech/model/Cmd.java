package com.bijietech.model;

/**
 * Created by Lip on 2016/8/9 0009.
 */
public abstract class Cmd {
    private String content;
    private boolean confirmed;//命令是否被确定

    public boolean isVaild() {
        return vaild;
    }

    public void setVaild(boolean vaild) {
        this.vaild = vaild;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    private boolean vaild;//命令时候还在有效期内

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

   // public abstract String getReply();
}
