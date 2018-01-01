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



