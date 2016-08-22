package com.bijietech.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Pinyin4jUtil {
    private static final Logger logger = LoggerFactory.getLogger(Pinyin4jUtil.class);

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变，特殊字符丢失 支持多音字，生成方式如（长沙市长:cssc,zssz,zssc,cssz）
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines) {
        try {
            StringBuffer pinyinName = new StringBuffer();
            char[] nameChar = chines.toCharArray();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            for (int i = 0; i < nameChar.length; i++) {
                if (nameChar[i] > 128) {
                    try {
                        // 取得当前汉字的所有全拼
                        String[] strs = PinyinHelper.toHanyuPinyinStringArray(
                                nameChar[i], defaultFormat);
                        if (strs != null) {
                            for (int j = 0; j < strs.length; j++) {
                                // 取首字母
                                pinyinName.append(strs[j].charAt(0));
                                if (j != strs.length - 1) {
                                    pinyinName.append(",");
                                }
                            }
                        }
                        // else {
                        // pinyinName.append(nameChar[i]);
                        // }
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        logger.info("BadHanyuPinyinOutputFormatCombination:{}", e);
                    }
                } else {
                    pinyinName.append(nameChar[i]);
                }
                pinyinName.append(" ");
            }
            // return pinyinName.toString();
            return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
        } catch (Exception e) {
            logger.warn("get some error when converterToFirstSpell to pinyin:{}", e);
        }
        return null;
    }

    /**
     * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失
     * 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen
     * ,chongdangshen,zhongdangshen,chongdangcan）
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        try {
            StringBuffer pinyinName = new StringBuffer();
            char[] nameChar = chines.toCharArray();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            for (int i = 0; i < nameChar.length; i++) {
                if (nameChar[i] > 128) {
                    try {
                        // 取得当前汉字的所有全拼
                        String[] strs = PinyinHelper.toHanyuPinyinStringArray(
                                nameChar[i], defaultFormat);
                        if (strs != null) {
                            for (int j = 0; j < strs.length; j++) {
                                pinyinName.append(getFuzzyPinyin(strs[j]));
                                if (j != strs.length - 1) {
                                    pinyinName.append(",");
                                }
                            }
                        }
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        logger.info("BadHanyuPinyinOutputFormatCombination:{}", e);
                    }
                } else {
                    pinyinName.append(nameChar[i]);
                }
                pinyinName.append(" ");
            }
            // return pinyinName.toString();
            return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
        } catch (Exception e) {
            logger.warn("get some error when converterToSpell to pinyin:{}", e);
        }
        return null;
    }

    /**
     * 去除多音字重复数据
     *
     * @param theStr
     * @return
     */
    private static List<Map<String, Integer>> discountTheChinese(String theStr) {
        // 去除重复拼音后的拼音列表
        List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
        // 用于处理每个字的多音字，去掉重复
        Map<String, Integer> onlyOne = null;
        String[] firsts = theStr.split(" ");
        // 读出每个汉字的拼音
        for (String str : firsts) {
            onlyOne = new HashMap<>();
            String[] china = str.split(",");
            // 多音字处理
            for (String s : china) {
                Integer count = onlyOne.get(s);
                if (count == null) {
                    onlyOne.put(s, new Integer(1));
                } else {
                    onlyOne.remove(s);
                    count++;
                    onlyOne.put(s, count);
                }
            }
            mapList.add(onlyOne);
        }
        return mapList;
    }

    /**
     * 解析并组合拼音，对象合并方案(推荐使用)
     *
     * @return
     */
    private static String parseTheChineseByObject(
            List<Map<String, Integer>> list) {
        Map<String, Integer> first = null; // 用于统计每一次,集合组合数据
        // 遍历每一组集合
        for (int i = 0; i < list.size(); i++) {
            // 每一组集合与上一次组合的Map
            Map<String, Integer> temp = new HashMap<>();
            // 第一次循环，first为空
            if (first != null) {
                // 取出上次组合与此次集合的字符，并保存
                for (String s : first.keySet()) {
                    for (String s1 : list.get(i).keySet()) {
                        String str = s + "," + s1;
                        temp.put(str, 1);
                    }
                }
                // 清理上一次组合数据
                if (temp != null && temp.size() > 0) {
                    first.clear();
                }
            } else {
                for (String s : list.get(i).keySet()) {
                    String str = s;
                    temp.put(str, 1);
                }
            }
            // 保存组合数据以便下次循环使用
            if (temp != null && temp.size() > 0) {
                first = temp;
            }
        }
        String returnStr = "";
        if (first != null) {
            // 遍历取出组合字符串
            for (String str : first.keySet()) {
                returnStr += (str + "|");
            }
        }
        if (returnStr.length() > 0) {
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }
        return returnStr;
    }

    /**
     * 两个汉字的音是否匹配
     *
     * @param ch1
     * @param ch2
     * @param fuzzy
     * @return
     */
    public static boolean matchByPinYin(char ch1, char ch2, boolean fuzzy) {
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String[] strs1 = new String[0];
        String[] strs2 = new String[0];
        try {
            strs1 = PinyinHelper.toHanyuPinyinStringArray(
                    ch1, defaultFormat);
            strs2 = PinyinHelper.toHanyuPinyinStringArray(
                    ch2, defaultFormat);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }

        //需要使用模糊音
        if (fuzzy) {
            for (String s1 : strs1) {
                for (String s2 : strs2) {
                    if (getFuzzyPinyin(s1).equals(getFuzzyPinyin(s2)))
                        return true;
                }
            }
        } else {//不适用模糊音
            for (String s1 : strs1) {
                for (String s2 : strs2) {
                    if (s1.equals(s2))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * 得到一个拼音的模糊音
     *
     * @param str
     * @return
     */
    public static String getFuzzyPinyin(String str) {
        if (str.startsWith("zh")) {
            str = str.replace("zh", "z");
        } else if (str.startsWith("sh")) {
            str = str.replace("sh", "s");
        } else if (str.startsWith("ch")) {
            str = str.replace("ch", "c");
        } else if (str.startsWith("h")) {
            str = str.replace("h", "f");
        }
        if (str.endsWith("ang")) {
            str = str.replace("ang", "an");
        } else if (str.endsWith("eng")) {
            str = str.replace("eng", "en");
        } else if (str.endsWith("ing")) {
            str = str.replace("ing", "in");
        } else if (str.endsWith("iang")) {
            str = str.replace("iang", "ian");
        } else if (str.endsWith("uang")) {
            str = str.replace("uang", "uan");
        }
        return str;
    }
}