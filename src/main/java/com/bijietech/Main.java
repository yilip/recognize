package com.bijietech;

import com.bijietech.model.Cmd;
import com.bijietech.model.NormalCmd;
import com.bijietech.model.OrderCmd;

/**
 * Created by Lip on 2016/8/9 0009.
 */
public class Main {
    public static void main(String[] args) {
        Cmd cmd1=new NormalCmd();
        Cmd cmd2=new OrderCmd();
        if(cmd1 instanceof Cmd)
            System.out.println(1);
        System.out.println(cmd1.getClass());
        System.out.println(cmd2.getClass());
    }
}
