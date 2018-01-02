[toc]
# Junit
## 常用注解介绍
### @BeforeClass

在所有测试方法前执行一次
### @AfterClass

在所有测试方法后执行一次
### @Before

在每个测试执行前执行一次
### @After

在每个测试执行后执行一次
### @Test

指定被执行测试的方法
- @Test(timeout = 1000)
  - 测试方法执行超时时间，超时则失败
- @Test(expected = Exception.class)
  - 指定期望得到的异常，如果抛出该异常，则失败

### @Ignore
可以注解在类或者方法上，表示忽略执行测试

### @RunWith
指定使用那种Runner来调用测试代码

#### Suite.class
打包测试

##### @SuiteClasses
指定执行多个单元测试类

# mock




# maven生成测试报告
- 插件配置
```
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>cobertura-maven-plugin</artifactId>
    <version>2.7</version>
    <configuration>
        <instrumentation>
            <!-- 配置过滤的类文件 -->
            <excludes>
                <exclude>org/wxd/junit/demo/facade/*.class</exclude>
                <exclude>org/wxd/junit/demo/*.class</exclude>
                <exclude>org/wxd/junit/demo/domain/*.class</exclude>
                <exclude>org/wxd/junit/demo/dao/*.class</exclude>
                <exclude>org/wxd/junit/demo/api/*.class</exclude>
            </excludes>
        </instrumentation>
    </configuration>
</plugin>
```
- 生成命令
```
mvn clean cobertura:cobertura
```
也可以绑定到指定的生命周期执行
- 报告查看
  - 命令执行完成后，会在**target**目录下生成**site**目录，直接打开**cobertura/index.html**即可查看测试报告，显示总共测试了多少个类、代码行以及分支的覆盖率、逻辑复杂度等。
![image](https://note.youdao.com/yws/public/resource/cdaa7f8458add01b443daaae6cdf785a/xmlnote/WEBRESOURCEb547da6a555fd761a0530f3d8b5f781a/3227)

  - 也可以查看具体类的覆盖情况
![image](https://note.youdao.com/yws/public/resource/58337173f7fa25b04d4651d2e153bc1c/xmlnote/WEBRESOURCE8fff7583178822e229eb1e13dc9c036c/3238)