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
        String content = "十八哈大双联";
        //System.out.println(getCmd(content,2));
        String name="十八大双联";
        System.out.println(match(name,content));
        System.out.println(getPrice("十八块三毛二"));
    }

    public static String LOOK_WORD[] = {"了解", "查看", "查询", "查", "看",};
    public static String ORDER_WORD[] = {"买","卖"};
    public static String EXCHANGE[] = {"上文众申", "金一文化", "沙丘文化", "众申", "上文", "沙丘", "金一"};//, "上", "文", "众", "申", "沙", "丘", "金", "一"};
    public static String REPLAY_CONTENT[] = {"您说什么?", "我没听清楚您的意思...", "小贝刚睡醒，头脑不清..."};

    public static String NUMBER[]={"零","一","二","三","四","五","六","七","八","九","十","百","千","万"};

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
        if(content==null)
            return  null;
        //验证是不是订单
        String orderS=verifyOrderCmd(content);
        if(orderS==null)
            return  null;
        else if(orderS.equals(ORDER_WORD[0]))
        {
            cmd=new BuyCmd();
        }else  if(orderS.equals(ORDER_WORD[1]))
        {
            cmd=new SellCmd();
        }
        cmd.setContent(content);
        String []order=content.split(" ");
        //语音命令
        if(order.length==1)
        {
            double price=getPrice(content);
            ((OrderCmd)cmd).setPrice(price);
            String []exchanges=getExchange(content,l);
            ((OrderCmd)cmd).setExchangeName(exchanges[0]);
            int i=getNumber(content);
            String productName=content.substring(content.indexOf(exchanges[1])+exchanges[1].length(),i);
            ((OrderCmd)cmd).setProductName(productName);
            ((OrderCmd)cmd).setNum(convertToDouble(content.substring(i,content.length())));
            return cmd;
        }
        else if(order.length==5) {//格式化交易指令
            ((OrderCmd) cmd).setExchangeName(order[1]);
            ((OrderCmd) cmd).setSymbol(order[2]);
            ((OrderCmd) cmd).setNum(Double.parseDouble(order[3]));
            ((OrderCmd) cmd).setPrice(Double.parseDouble(order[4]));
            return cmd;
        }else
        {
            return null;
        }
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
            String contentPy = Pinyin4jUtil.converterToFirstSpell(content);
            logger.info("success convert to pinyin:{}",contentPy);
            for (String s : EXCHANGE) {
                logger.info("start convert exchange  :{} to pinyin",content);
                String ss[] = Pinyin4jUtil.converterToFirstSpell(s).split("\\|");
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

    /**
     * 得到命令中的价格
     * @param content
     * @return
     */
    public static Double getPrice(String content)
    {
        Double price=0d;
        int yuanIndex=0,jiaoIndex=0;
        //解析元
        if(content.contains("元"))
        {
            yuanIndex=content.indexOf("元");
            String yuan=content.substring(0,yuanIndex);
            price=convertToDouble(yuan);
        }else if(content.contains("块"))
        {
            yuanIndex=content.indexOf("块");
            String yuan=content.substring(0,yuanIndex);
            price=convertToDouble(yuan);
        }
        //解析角
        if(content.contains("角"))
        {
            jiaoIndex=content.indexOf("角");
            String jiao=content.substring(yuanIndex+1,jiaoIndex);
            price=price+convertToDouble(jiao)*0.1;
        }else if(content.contains("毛"))
        {
            jiaoIndex=content.indexOf("毛");
            String jiao=content.substring(yuanIndex+1,jiaoIndex);
            price=price+convertToDouble(jiao)*0.1;
        }
        //解析分
        if(content.contains("分"))
        {
            int fenIndex=content.indexOf("分");
            String fen=content.substring(jiaoIndex+1,fenIndex);
            price=price+convertToDouble(fen)*0.01;
        }
        //有18块三 ，那么三则代表三毛
        if(!content.contains("角")&&!content.contains("毛")&&!content.contains("分")&&yuanIndex<content.length())
        {
            String jiao=content.substring(yuanIndex+1,content.length());
            price=price+convertToDouble(jiao)*0.1;
        }
        //有18块三毛二 ，那么二则代表二分
        else if(!content.contains("分")&&yuanIndex<content.length())
        {
            String fen=content.substring(jiaoIndex+1,content.length());
            price=price+convertToDouble(fen)*0.01;
        }
        return price;
    }

    /**
     * 将汉字文字转换为数字
     * @param content
     * @return
     */
    public static Double convertToDouble(String content)
    {
        System.out.println(content);
        if(content.contains("万")) {
            int wan=content.indexOf("万");
            return convertToDouble(content.substring(0,wan))*10000+convertToDouble(content.substring(wan+1,content.length()));
        }
        Double number=0d,current=0d;
        int len=content.length();
        if(content.startsWith("十")) {
            current=1d;
        }
        for(int i=0;i<len;i++)
        {
            int temp=getIndex(content.charAt(i)+"");
            while(temp<10)
            {
                current=current*10+(double) temp;
                if(i<len-1) {
                    temp = getIndex(content.charAt(++i) + "");
                }else
                {
                    break;
                }
            }
            if(temp>9) {
                current=current>0?current*Math.pow(10, temp - 9):Math.pow(10, temp - 9);
            }
            number += current;
            current = 0d;
        }
        return number;
    }

    /**
     * 得到汉字的数字
     * @param num
     * @return
     */
    public static int getIndex(String num)
    {
        for(int i=0;i<NUMBER.length;i++)
        {
            if(num.equals(NUMBER[i]))
                return i;
        }
        return 0;
    }

    /**
     * 得到购买数量的位置索引
     * @param content
     * @return
     */
    public static int getNumber(String content)
    {
        int len=content.length();
        boolean flag=false;
        for(int i=len-1;i>-1;i--)
        {
            int temp=getIndex(content.charAt(i)+"");
            if(flag&&temp==0)
            {
                return i+1;
            }
            if(temp>0)
            {
                flag=true;
            }

        }
        return -1;
    }

    /**
     * 两句话匹配的程度
     * @param name
     * @param fullName
     * @return
     */
    public static  double match(String name,String fullName)
    {
        int len=0;
        String []namePy=Pinyin4jUtil.converterToSpell(name).split("\\|");
        String[]fullNamePy=Pinyin4jUtil.converterToSpell(fullName).split("\\|");
        for(int i=0;i<namePy.length;i++)
        {
            for(int j=0;j<fullNamePy.length;j++)
            {
                int tempLen=0;
                int l=0;
                String []namePy0=namePy[i].split(",");
                String []fullNamePy0=fullNamePy[j].split(",");
                for(int k=0;k<namePy0.length;k++)
                {
                    for(;l<fullNamePy0.length;l++)
                    {
                        if(namePy0[k].equals(fullNamePy0[l])) {
                            tempLen++;
                            break;
                        }
                    }
                }
                if(len<tempLen)
                    len=tempLen;
            }
        }
        return len*1.0/fullName.length();
    }
}
