package org.smart4j.framework.bean;

import org.smart4j.framework.util.CastUtil;

import java.util.Map;

/**
 * @author 郑煜
 * @Title: Param
 * @ProjectName smartframework
 * @Description: 请求参数对象
 * @date 2019/3/10下午 10:21
 */
public class Param {
    private Map<String, Object> paramsMap;

    public Param(Map<String,Object> pragmaMap){
        this.paramsMap =pragmaMap;
    }

    /***
     * 根据参数名获取long型参数值
     */
    public long getLong(String name){
        return CastUtil.castLong(paramsMap.get(name));
    }

    /**
     * 获取所有字段消息
     */
    public Map<String, Object> getMap(){
        return paramsMap;
    }
}
