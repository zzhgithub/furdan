package com.zzh.furdan.core;

import java.io.Serializable;
import java.util.List;

/**
 * 方法数据类
 */
public class MethodData implements Serializable {
    private static final long serialVersionUID = -8759514856898537358L;

    /**
     * 接口名称 （含包名）
     */
    private String interfaceName;

    /**
     * 方法入参说明
     */
    private List<String> paramsTypesNames;

    /**
     * 方法出参说明
     */
    private String returnType;

    /**
     * 方法名
     */
    private String methodName;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public List<String> getParamsTypesNames() {
        return paramsTypesNames;
    }

    public void setParamsTypesNames(List<String> paramsTypesNames) {
        this.paramsTypesNames = paramsTypesNames;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "MethodData{" +
                "interfaceName='" + interfaceName + '\'' +
                ", paramsTypesNames=" + paramsTypesNames +
                ", returnType='" + returnType + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
