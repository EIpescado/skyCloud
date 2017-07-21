package org.skyCloud.common.utils;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yq on 2017/01/04 17:15.
 */
public class StringUtils {

    //匹配所有特殊字符的正则表达式
    public static final String SPECIAL_CHARACTER_REGEX = "[`~!@#$%^&*()\\-+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）—_+|{}【】‘；：”“’。，、\"？\\s]";

    public static final Pattern SPECIAL_CHARACTER_PATTERN = Pattern.compile(SPECIAL_CHARACTER_REGEX) ;

    //中文正则
    public static final String CHINESE_REGEX = "[\u4e00-\u9fa5]" ;

    //匹配括号中间内容
    private static final Pattern BRACKET_CONTENT_PATTERN = Pattern.compile("(?<=\\()(.+?)(?=\\))");

    private StringUtils() {
    }

    /**
     * 二进制转字符串
     */
    static String byte2hex(byte[] b) {
        String hs = "" ;
        String stmp = "" ;
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF)) ;
            if (stmp.length() == 1) hs = hs + "0" + stmp ;
            else hs = hs + stmp ;
        }
        return hs.toUpperCase();
    }

    /**
     * 字符串去左右空格
     */
    public static  String null2EmptyWithTrim(String s) {
        if (s == null) {
            return "";
        }else{
            return s.trim();
        }
    }

    /**
     * 字符串是否为空
     */
    public static  boolean isEmpty(String foo) {
        return (foo == null || foo.trim().length() == 0);
    }

    /**
     * 字符串是否不为空
     */
    public static  boolean isNotEmpty(String foo) {
        return (null != foo && foo.trim().length() > 0);
    }

    /**
     * 计算一个字符串的长度，汉字当成两个字符计算，ascii英文字符当成一个。
     * @param aStr 要计算长度的目标字符串
     * @return 计算出的长度
     */
    public static int lengthOfCN(String aStr) {
        char c;
        int length = 0;
        if(isNotEmpty(aStr)){
            for (int i = 0; i < aStr.length(); i++) {
                c = aStr.charAt(i);
                if (c >= 127) {
                    length += 2;
                }else{
                    length += 1;
                }
            }
        }
        return length;
    }

    /**
     * 字符串首字母大写或小写
     */
    public static String firstLetterUpperOrLower(String str,boolean isLowerCase) {
        if (isNotEmpty(str)){
            if (str.length() == 1) {
                return isLowerCase ? str.toLowerCase() : str.toUpperCase() ;
            }else{
                String first = str.substring(0, 1).toLowerCase();
                first = isLowerCase ? first.toLowerCase() : first.toUpperCase() ;
                return (first + str.substring(1));
            }
        }
        return str;
    }

    /**
     * 去除字符串所有特殊字符
     */
    public static String removeAllSpecialChar(String str) {
        Matcher m = SPECIAL_CHARACTER_PATTERN.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 判断str是否在strArr中
     */
    public static boolean strInArray(String str, String[] strArr) {
        for(String s : strArr){
            if (str.equals(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * 按分隔符分隔字符串
     */
    @Deprecated
    public static String[] split(String text, String separator) {

        StringTokenizer st = new StringTokenizer(text, separator);
        String[] values = new String[st.countTokens()]; //分隔符数量大小的字符串数组
        int pos = 0;
        while (st.hasMoreTokens())//是否还有分隔符
        {
            values[pos++] = st.nextToken();//返回从当前位置到下一个分隔符的字符串
        }
        return values;
    }

    /**
     * 判断字符串中是否包含中文
     */
    public static boolean isContainChinese(String str) {
        Matcher m = SPECIAL_CHARACTER_PATTERN.matcher(str);
        return  m.find() ;
    }

    /**
     * 获取字符串中所有中文
     */
    public static String getAllChineseInStr(String str){
        return  str.replaceAll("[^\u4e00-\u9fa5]", "") ;
    }

    /**
     * 获取括号内内容
     * @param str
     */
    public static  String getBracketContent(String str){
        Matcher matcher = BRACKET_CONTENT_PATTERN.matcher(str);
        if(matcher.find()){
            return  matcher.group(0);
        }else {
            return "";
        }
    }

    public static void main(String[] args) {
        System.out.println(removeAllSpecialChar("d@ _|\"qwe?%$#251"));
    }
}
