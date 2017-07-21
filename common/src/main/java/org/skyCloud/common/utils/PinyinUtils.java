package org.skyCloud.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by yq on 2017/03/30 11:33.
 * 汉字转拼音工具类,依赖pinyin4j
 */
public class PinyinUtils {

   private static final HanyuPinyinOutputFormat WITHOUT_TONE_FORMAT= new HanyuPinyinOutputFormat();

   static {
       WITHOUT_TONE_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);//全部小写
       //WITH_TONE_MARK 音标 ,WITHOUT_TONE 无音标 ,WITH_TONE_NUMBER 数字表示音标
       WITHOUT_TONE_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
       WITHOUT_TONE_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);
   }

    private PinyinUtils() {
    }

    /**
     * 字符串中汉字转拼音,保留非汉字字符
     * @param str 待转换拼音字符串
     * @param retainNotHanYuChar 是否保留非汉字字符
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public static String hanYu2PinyinString(String str,boolean retainNotHanYuChar) throws BadHanyuPinyinOutputFormatCombination {
        if(StringUtils.isEmpty(str)){
            return "";
        }else {
            return PinyinHelper.toHanYuPinyinString(str,WITHOUT_TONE_FORMAT,"",retainNotHanYuChar);
        }
    }

    /**
     * 获取字符串汉语的首字母(小写)
     * @param str 汉语
     */
    public static String obtainHanYuFirstLetter(String str){
        StringBuilder sb = new StringBuilder();
        char[] nameChar = str.toCharArray();
        for (char ch : nameChar) {
            if (ch > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(
                            ch, WITHOUT_TONE_FORMAT)[0].charAt(0));
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception{
        System.out.println(obtainHanYuFirstLetter("C扁2君3A"));
    }

}
