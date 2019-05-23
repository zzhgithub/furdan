
# Für Dan

---
# OverView

`Für Dan` 是一个给spring cloud的项目。致力于在服务治理使用纯的接口定义作为
两个系统间的约束。而不是使用定义文档的restful接口。  

## 用了 `Für Dan` 能做的什么呢？  

比如我定义了一个接口在一个springCloud项目引用后。如下。  
接口定义：  
```java
package com.zzh.api;

public interface TestApi {
    String test(String var1);
    }
```

`Für Dan`运行你仅仅使用一个注解在它的实现类上就给Eureka上注册了接口。并且按照
一定的规则就可以在其他springCould项目里调用。也仅仅需要一些配置。简单来说
他屏蔽了暴露成http和调用他的一些细节。让你以为自己在使用RPC调用。

> 但是其实他里面还是使用http进行通信的


# 快速开始

## 项目依赖

```xml
    <dependency>
        <groupId>com.zzh.furdan</groupId>
        <artifactId>furdan</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
```
很遗憾，你现在要自己下载项目并且打包。之后引用。

接下来配置在编译时是用apt，在idea里可能像下面一样改变pom文件
```xml
<build>
        <plugins>
            ...
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.5.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                    <annotationProcessors>
                        <annotationProcessor>com.zzh.furdan.core.FurDanProcessor</annotationProcessor>
                    </annotationProcessors>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

## 使用步骤

- 建立一个接口

(详细见"用了 `Für Dan` 能做的什么呢？")

- 在系统A里实现这个接口，并且使用`FurDan`注解

```java
import com.zzh.api.TestApi;
import org.springframework.stereotype.Service;
import com.zzh.furdan.annotation.FurDan;

@Service
@FurDan
public class TestApiImpl implements TestApi {

    @Override
    public String test(String avg) {
        return "ok";
    }
}
```
之后不用做其他的了，保证你的项目其他配置正确，这个方法就会正确的注册到
Eureka上了。但是要记住项目的appName；

-  在项目B中引入api并且配置好bean

```java
import com.zzh.api.TestApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import com.zzh.furdan.core.BeanProxy;

@Configuration
public class ConfigMe {
    
    // 这里要和上面的系统配置一致
    @Value(${test.api.name})
    private String appName;
    
    @Autowired
    RestTemplate restTemplate;

    @Bean
    public TestApi testApi(){
        return (TestApi)new BeanProxy().createProxy(TestApi.class,restTemplate,appName);
    }

}
```


- 调用。
现在你就可以在项目B的任何地方调用掉这个接口了。

```java
@RestController
public class TestController {

    @Autowired
    private TestApi testApi;

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String test(){
        // 这里就可直接调用了
        return testApi.test("");
    }
}

```


# 为什么不用spring cloud openFeign?

获取你会觉得使用spring cloud openFeign更加优雅，而且也更加健壮。
可是我的目的是不在api接口定义上加上过多的东西。你可以看到我这里接口的定义上
没有使用任何的注解。可能是使用dubbo的缘故我觉得在一个单独的api项目上加一个
底层的注解是不可以接受的，除了@Data这样的注解。

并且`Für Dan`的作者本人，并没有取代Feign的意思。如果可以不用它的注解
在api项目中，我很乐意使用Feign的技术重写一次。

# 我想改造可以吗？
欢迎提各种意见。唯独在api上单独加上什么注解是我不会接受的其他都可以。