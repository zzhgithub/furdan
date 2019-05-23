package com.zzh.furdan.core;

import com.zzh.furdan.annotation.FurDan;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class FurDanProcessor extends AbstractProcessor {

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (Element element : roundEnv.getElementsAnnotatedWith(FurDan.class)) {
                if (element.getKind() == ElementKind.CLASS) {
                    // 显示转换元素类型
                    TypeElement typeElement = (TypeElement) element;
                    // 输出元素名称
                    System.out.println("即将处理:" + typeElement.getQualifiedName());
                    System.out.println("生成服务名" + typeElement.getAnnotation(FurDan.class).value());
                    //获取 继承接口
                    List<? extends TypeMirror> typeMirrors = typeElement.getInterfaces();
                    //FIXME 这里没有考虑到如果是用了多实现接口时遇到的问题
                    String superClassName = typeMirrors.get(0).toString();
                    System.out.println("这里要处理父类是：" + superClassName);

                    // 接下来要获取包含泛型的入参出参
                    List<MethodData> methodDatas = new LinkedList<>();
                    //  获取子元素
                    List<? extends Element> annotatedElements = typeElement.getEnclosedElements();
                    for (Element element1 : annotatedElements) {
                        if (element1 instanceof ExecutableElement) {
                            // 获取被继承的接口
                            Override override = element1.getAnnotation(Override.class);
                            if (override != null) {
                                System.out.println("开始暴露接口" + element1.getSimpleName());
                                ExecutableElement executableElement = (ExecutableElement) element1;
                                //  获取这里的方法签名的入参和出参
                                // 出参类型
                                String returnType = ((ExecutableElement) element1).getReturnType().toString();
                                // 入参类型列表
                                List<String> paramsTypes = ((ExecutableElement) element1).getParameters().stream()
                                        .map(e -> ((VariableElement) e).asType().toString()).collect(Collectors.toList());
                                MethodData methodData = new MethodData();
                                methodData.setInterfaceName(superClassName);
                                methodData.setMethodName(element1.getSimpleName().toString());
                                methodData.setParamsTypesNames(paramsTypes);
                                methodData.setReturnType(returnType);
                                methodDatas.add(methodData);
                            }
                        }
                    }

                    //  写一个文件到目标文件中
                    writeControllerFile(typeElement.getQualifiedName().toString(), methodDatas);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(FurDan.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    private void writeControllerFile(String className, List<MethodData> methodDatas) throws IOException {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        String simpleClassName = className.substring(lastDot + 1);
        // 生成随机类名
        String builderClassName = className + "Controller";
        String builderSimpleClassName = builderClassName
                .substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);

        String interfaceName = methodDatas.get(0).getInterfaceName();
        int interfaceLastDot = interfaceName.lastIndexOf('.');
        String simpleInterfaceName = interfaceName.substring(interfaceLastDot + 1);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }
            // 定义必要包
            out.println("import org.springframework.web.bind.annotation.RequestMapping;");
            out.println("import org.springframework.web.bind.annotation.RequestMethod;");
            out.println("import org.springframework.web.bind.annotation.RestController;");
            out.println("import org.springframework.beans.factory.annotation.Autowired;");
            out.print("import ");
            out.print(interfaceName);
            out.println(";");
            // TODO 计算需要import的依赖包
            out.println("@RestController");
            out.print("public class ");
            out.print(builderSimpleClassName);
            out.println(" {");
            // TODO 添加私有变量的 的使用
            out.println("   @Autowired");
            out.print("   private ");
            out.print(simpleInterfaceName + " ");
            out.print(simpleInterfaceName.toLowerCase());
            out.println(";");
            // todo 这里添加方法 根据
            for (MethodData methodData : methodDatas) {
                Helper.genMethod(out, methodData);
            }

            out.println("}");
        }
    }
}
