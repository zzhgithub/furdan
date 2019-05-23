package com.zzh.furdan.core;

import java.io.PrintWriter;
import java.util.List;

public abstract class Helper {

    public static void genMethod(PrintWriter out, MethodData methodData){
        String interfaceName = methodData.getInterfaceName();
        int interfaceLastDot = interfaceName.lastIndexOf('.');
        String simpleInterfaceName = interfaceName.substring(interfaceLastDot + 1);

        // 书写方法注解
        out.print("    @RequestMapping( value=\"");
        out.print(methodData.getInterfaceName()+"/"+methodData.getMethodName()+"\", ");
        out.println("method = RequestMethod.POST)");
        // 函数出参
        out.print("    public ");
        out.print(methodData.getReturnType()+" ");
        // 入参列表
        out.print(methodData.getMethodName()+" (");
        List<String> params = methodData.getParamsTypesNames();
        for (int i=0;i<params.size();i++){
            out.print(params.get(i));
            out.print(" arg"+i);
            if (i<params.size()-1){
                out.print(",");
            }
        }
        out.println(") {");
        // 函数体

        out.print("         return ");
        out.print(simpleInterfaceName.toLowerCase()+"."+methodData.getMethodName()+"(");
        for (int i=0;i<params.size();i++){
            out.print(" arg"+i);
            if (i<params.size()-1){
                out.print(",");
            }
        }
        out.println(");");

        out.println("   }");
    }

}
