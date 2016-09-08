package com.bijietech.util;


import com.bijietech.exception.HasNoCmdException;
import com.bijietech.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Lip on 2016/8/6 0006.
 */
public class CmdFactory {
    private   static  final Logger logger = LoggerFactory.getLogger(CmdFactory.class);
    public static void main(String[] args) throws Exception{
        String content = "帮我下单 100 3。";
        System.out.println(getCmd(content,2));
    }

    public static String LOOK_WORD[] = {"了解", "查看", "查询", "查", "看",};
    public static String ORDER_WORD[] = {"买","卖"};
    public static String EXCHANGE[] = {"上文众申", "金一文化", "沙丘文化", "众申", "上文", "沙丘", "金一"};//, "上", "文", "众", "申", "沙", "丘", "金", "一"};
    public static String REPLAY_CONTENT[] = {"您说什么?", "我没听清楚您的意思...", "小贝刚睡醒，头脑不清..."};

    /**
     * 将语句解析成命令
     *
     * @param content
     * @return
     */
    public static Cmd getCmd(String content,Integer level){
        //解析订单命令
        Cmd cmd = verifyOrderCmd(content,level);
        if(cmd!=null)
            return cmd;
        content = stringFilter(content);
        String lookCmd = verifyCmd(content);
        //非查看命令
        if(lookCmd==null)
        {
            cmd = new NormalCmd();
            cmd.setContent(content);
            return cmd;
        }
        logger.info("the look cmd is:{}",lookCmd);
        content = getRealCmd(content);
        logger.info("get the user real purpose:{}",content);
        String[] exchangeName = getExchange(content, level);
        logger.info("the exchange name is:{}",exchangeName);
        if(exchangeName==null)
        {
            cmd = new NormalCmd();
            cmd.setContent("没有交易所...");
            return cmd;
        }
        int cmdLength = content.indexOf(exchangeName[1]) + exchangeName[0].length();
        if (content.length() == cmdLength) {
            {
                cmd = new MultiQueryCmd();
                cmd.setContent(content);
                ((MultiQueryCmd) cmd).setExchangeName(exchangeName[0]);
            }
        } else if (content.length() > cmdLength)//查询具体产品
        {
            cmd = new QueryCmd();
            cmd.setContent(content);
            ((QueryCmd) cmd).setExchangeName(exchangeName[0]);
            ((QueryCmd) cmd).setProductName(content.substring(cmdLength,
                    content.length()));
        } else {
            cmd = new NormalCmd();
            cmd.setContent("您的命令有误...");
        }
        return cmd;
    }

    /**
     * 订单
     * @param content
     * @param l
     * @return
     */
    public static Cmd verifyOrderCmd(String content,Integer l)
    {
        Cmd cmd=null;
        content=getRealCmd(content);
        if(content==null)
            return  null;
        String []order=content.split(" ");
        if(order.length!=5)
            return null;
        if(order[0].equals(ORDER_WORD[0]))
        {
            cmd=new BuyCmd();
        }else if(order[0].equals(ORDER_WORD[1]))
        {
            cmd=new SellCmd();
        }else {
            return null;
        }
        cmd.setContent(content);
        ((OrderCmd)cmd).setExchangeName(order[1]);
        ((OrderCmd)cmd).setSymbol(order[2]);
        ((OrderCmd)cmd).setNum(Double.parseDouble(order[3]));
        ((OrderCmd)cmd).setPrice(Double.parseDouble(order[4]));
        return cmd;
    }
    /**
     * 验证是不是有命令
     *
     * @param content
     */
    public static String verifyCmd(String content) {
        for (String s : LOOK_WORD) {
            if (content.contains(s)) {
                return s;
            }
        }
        return null;
    }
    /**
     * 验证是不是有命令
     *
     * @param content
     */
    public static String verifyOrderCmd(String content) {
        for (String s : ORDER_WORD) {
            if (content.contains(s)) {
                return s;
            }
        }
        return null;
    }
    public static String getRealCmd(String content) {
        int index = 0;
        for (String s : LOOK_WORD) {
            if (content.contains(s)) {
                index = content.indexOf(s);
                return content.substring(index, content.length());
            }
        }
        for (String s : ORDER_WORD) {
            if (content.contains(s)) {
                index = content.indexOf(s);
                return content.substring(index, content.length());
            }
        }
        return null;
    }

    /**
     * 随机得到回复内容
     *
     * @param content
     * @return
     */
    public static String getReplyContent(String content) {
        Random random = new Random();
        return REPLAY_CONTENT[random.nextInt(REPLAY_CONTENT.length)];
    }

    /**
     * 识别交易所
     *
     * @param content
     * @param level   识别难度 1:完全匹配交易所 2:拼音模糊匹配交易所 3：拼音首字母完全匹配即可
     * @return
     */
    public static String[] getExchange(String content, Integer level) {
        if(level==null)level=2;
        String result[] = new String[2];
        if (level == 1) {
            for (String s : EXCHANGE) {
                if (content.contains(s)) {
                    result[0] = s;
                    result[1] = s;
                    return result;
                }
            }
        } else if (level == 2) {
            logger.info("start convert :{} to pinyin",content);
            String contentPy = Pinyin4jUtil.converterToSpell(content);
            logger.info("success convert to pinyin:{}",contentPy);
            for (String s : EXCHANGE) {
                logger.info("start convert exchange  :{} to pinyin",content);
                String ss[] = Pinyin4jUtil.converterToSpell(s).split("\\|");
                logger.info("success convert exchange to pinyin:{}",ss);
                for (String tempSs : ss) {
                    if (contentPy.contains(tempSs)) {
                        result[0] = s;
                        int l = getDotNum(contentPy.substring(0, contentPy.indexOf(tempSs)));
                        result[1] = content.substring(l, l + tempSs.split(",").length);
                        return result;
                    }
                }
            }
        } else if (level >= 3) {
            logger.info("start convert :{} to pinyin",content);
            String contentPy = Pinyin4jUtil.converterToSpell(content);
            logger.info("success convert to pinyin:{}",contentPy);
            for (String s : EXCHANGE) {
                logger.info("start convert exchange  :{} to pinyin",content);
                String ss[] = Pinyin4jUtil.converterToSpell(s).split("\\|");
                logger.info("success convert exchange to pinyin:{}",ss);
                for (String tempSs : ss) {
                    if (contentPy.contains(tempSs)) {
                        result[0] = s;
                        int l = getDotNum(contentPy.substring(0, contentPy.indexOf(tempSs)));
                        result[1] = content.substring(l, l + tempSs.split(",").length);
                        return result;
                    }
                }
            }
        }
        return null;
    }

    //得到逗号的数量
    public static int getDotNum(String content) {
        int num = 0;
        for (char c : content.toCharArray()) {
            if (c == ',')
                num++;
        }
        return num;
    }

    public static String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[a-zA-Z\\s#\\?&%@$，,.。;；/ + = -、x*]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
