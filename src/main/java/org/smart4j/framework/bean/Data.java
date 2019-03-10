package org.smart4j.framework.bean;

/**
 * @author 郑煜
 * @Title: Data
 * @ProjectName smartframework
 * @Description: 返回数据对象
 * @date 2019/3/10下午 10:31
 */
public class Data {

    /**
     * 模型数据
     */
    private Object model;

    public Data(Object model){
        this.model = model;
    }

    public Object getModel() {
        return model;
    }
}
