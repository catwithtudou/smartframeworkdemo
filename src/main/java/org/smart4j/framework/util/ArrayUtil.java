package org.smart4j.framework.util;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author 郑煜
 * @Title: ArrayUtil
 * @ProjectName smartframework
 * @Description: 数组工具类
 * @date 2019/3/9上午 01:54
 */
public final class ArrayUtil {
    /**
     * 判断数组是否为空
     */
    public static boolean isNotEmpty(Object[] array){
        return !ArrayUtils.isEmpty(array);
    }

    /**
     * 判断数组是否为空
     */
    public static boolean isEmpty(Object[] array){
        return ArrayUtils.isEmpty(array);
    }
}
