package org.smart4j.framework.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author 郑煜
 * @Title: CollectionUtil
 * @ProjectName chapter2
 * @Description: 集合工具类
 * @date 2019/3/7上午 11:18
 */
public class CollectionUtil {
    /**
     * 判断Collection是否为空
     */
    public static  boolean isEmpty(Collection<?> collection){
        return CollectionUtils.isEmpty(collection);
    }
    /**
     * 判断Collection是否为空
     */
    public  static boolean isNotEmpty(Collection<?> collection){
        return !isEmpty(collection);
    }
    /**
     * 判断Map是否为空
     */
    public static boolean isEmpty(Map<?,?> map){
        return MapUtils.isEmpty(map);
    }
    /**
     * 判断Map是否为空
     */
    public static boolean isNotEmpty(Map<?,?> map){
        return  !isEmpty(map);
    }
}
