package com.dayuarchi.springmvc.servlet.spring;

/**
 * @author qiankeqin
 * @Description: DESCRIPTION
 * @date 2019-09-03 12:45
 */
public class SpringUtils {

    /**
     * 设置首字母消协
     * @param simpleName
     * @return
     */
    public static String lowerFirstCase(String simpleName) {
        if(null==simpleName || simpleName.length()==0){
            return "";
        }
        if(simpleName.length() == 1){
            return simpleName.toLowerCase();
        }
        return simpleName.substring(0,1).toLowerCase() + simpleName.substring(1);
    }

}