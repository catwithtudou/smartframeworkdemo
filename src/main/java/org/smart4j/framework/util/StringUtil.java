package org.smart4j.framework.util;


import org.apache.commons.lang3.StringUtils;

/**
 * @author 郑煜
 * @Title: StringUtil
 * @ProjectName chapter2
 * @Description: 字符串操作
 * @date 2019/3/7上午 11:13
 */
public final class StringUtil {

    /**
     * 判断字符是否为空
     */
    public static boolean isEmpty(String str){
        if(str!=null){
            str=str.trim();
        }
        return StringUtils.isEmpty(str);
    }

    /**
     * 判断字符串是否非空
    　　*/
    public static boolean isNotEmpty(String str){
        return  !isEmpty(str);
    }

    public static String[] splitString(String s, String pattern) {
        return StringUtils.split(s,pattern);
    }
}
