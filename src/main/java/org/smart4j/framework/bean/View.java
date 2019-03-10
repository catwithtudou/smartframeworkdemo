package org.smart4j.framework.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 郑煜
 * @Title: View
 * @ProjectName smartframework
 * @Description: 返回视图对象
 * @date 2019/3/10下午 10:27
 */
public class View {

    /**
     * 视图路径
     */
    private String path;

    /**
     * 模型数据
     */
    private Map<String,Object> model;

    public View(String path){
        this.path = path;
        model=new HashMap<String, Object>();
    }

    public View addModel(String key,Object value){
        model.put(key,value);
        return this;
    }

    public String getPath(){
        return path;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
