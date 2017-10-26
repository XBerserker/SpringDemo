## 1. 环境配置和 profile 
- 应用场景：
  1. 隔离开开发与生产环境的数据库配置，比如说dev环境、test环境、生产环境
  2. 在开发环境中，很大可能会使用嵌入式数据库，并预先加载测试数据，在Spring配置类中，可能会在带有@Bean注解的方法上使用EmbeddedDatabaseBuilder:
```java
@org.springframework.context.annotation.Bean(destroyMethod = "shutdown")
public DataSource dataSource(){
    return new EmbeddedDatabaseBuilder()
        ,addScript("classpath:schema.sql")
        .addScript("classpath:test-data.sql")
        .build();
}
```
  3. 当在开发环境中运行继承测试或者启动应用进行手动测试的时候，这个DataSource是很有用的。每次启动的时候，都能让数据库处于一个给定的状态。
    - 尽管EmbeddedDatabaseBuilder创建的DataSource非常适于开发环境，但是对于生产环境来说，**这会是一个糟糕的选择**。在生产环境的配置中，你可能会希望使用JNDI从容器中获取一个DataSource。在这样的场景中有如下例子：
```java
@org.springframework.context.annotation.Bean
public DataSource dataSource(){
    JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
    jndiObjectFactoryBean.setJndiName("jdbc/myDS");
    jndiObjectFactoryBean.setResourceRef(true);
    jndiObjectFactoryBean.setPRoxyInterface(javax.sql.DataSource.class);
    return (DataSource) jndiObjectFactoryBean.getObject();
}
```
  4. 在QA环境中，你可以选择完全不同的DataSource配置，可以配置为Commons DBCP连接池：
```java
@org.springframework.context.annotation.Bean(destroyMethod = "close")
public DataSource dataSource(){
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUrl("jdbc:h2:tcp://dbserver/~/test");
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUserName("sa");
    dataSource.setPassword("password");
    dataSource.setInitialSize(20);
    dataSource.setMaxActive(30);
    
    return dataSource;
    return dataSource;
}
```


### 1. 配置 profile bean
- 在3.1版本中，Spring 引入了bean profile 的功能。要使用profile，首先要将所有不同的bean定义整理到一个或多个profile之中，在将应用部署到每个环境时，要确保对应的profile处于激活状态：
```java
import javax.activation.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
@Profile("dev")
public class DevelopmentProfileConfig{
    @Bean(destroyMethod = "shutdown")
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:schema.sql")
            .addScript("classpath:test-data.sql0")
            .build();
    }
}
```
- 剩下的代码碎片及不写了，和上面的基本一致，不过增加了Profile的标注……
- 从Spring 3.2开始，可以在方法级别上使用@Profile注解。与@Bean注解一同使用，就能将这两个Bean的声明放到同一个配置类之中：
```java
import javax.activation.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
public class DataSourceConfig{
    @Bean(destroyMethod = "shutdown")
    @Profile("dev")         //
    public DataSource embeddedDataSource(){
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:schema.sql")
            .addScript("classpath:test-data.sql")
            .build();
    }
    
    @Bean
    @Profile("prod")        //
    public DataSource jndiDataSource(){
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiName("jdbc/myDS");
        jndiObjectFactoryBean.setResourceRef(true);
        jndiObjectFactoryBean.setProxyInterface(javax.sql.DataSource.class);
        return (DataSource) jndiObjectFactoryBean.getObject();
    }
}
```

- **在XML中配置profile**
略。

### 2. 激活profile
- Spring通过两个独立的属性来判断profile是否处于激活状态：
  1. `spring.profiles.active`
  2. `spring.profiles.default`
  3. 如果两个都没有设置，那就只会创建那些没有定义在profile中的bean
- 设置这两个属性的一些方法途径：
  1. 作为DispatcherServlet的初始化参数；
  2. 作为Web应用的上下文参数；
  3. 作为JNDI条目；
  4. 作为环境变量；
  5. 作为JVM的系统属性
  6. 在集成测试类上，使用 @ActiveProfiles 注解设置
- 推荐方法：
  - 在web.xml文件中设置默认的profile：
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<web-app version="2.5"
xmlns="http://java.sun.com/xml/ns/javaee"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
<context-param>
<param-name>contextConfigLocation</param-name>
<param-value>/WEB-INF/spring/root-context.xml</param-value>
</context-param>

<context-param>
<param-name>spring.profiles.default</param-name>
<param-value>dev</param-value>
</context-param>
<listener>
<listener-class>org.springframework.web.context.ContextLoaderListerner</listener-class>
</listener>

<servlet>
<servlet-name>spring.profiles.default</servlet-name>
<init-param><param-name>spring.profiles.default</param-name><param-value>dev</param-value></init-param>
</servlet>

<servlet-mapping><servlet-name>appServlet</servlet-name><url-pattern>/</url-pattern></servlet-mapping>
</web-app>
```
  - 按照这种方式设置spring.profiles.default，所有的开发人员都能从版本控制软件中获得应用程序源码，并使用开发环境的设置运行代码，而不需要任何额外的配置
  - 当应用程序部署到QA、生产或其他环境中时，负责部署的人根据情况使用系统属性、环境变量或JNDI设置spring.profiles.active即可。当设置spring.profiles.active以后，至于spring.profiles.default是什么值就已经无所谓了，系统会优先使用spring.profiles.acticve中所设置的profile。

- **Spring提供了@ActiveProfiles注解，我们可以使用它来指定运行测试时要激活哪个profile。在继承测试时，通常想要激活的是开发环境的profile。**：
```java
@org.junit.runner.RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(classes = {PersistenceTestConfig.class})
@org.springframework.test.context.ActiveProfiles("dev")
public class PersistenceTest{
    ...
}
```

## 2. 条件化的 bean
- Spring4 新增功能
- @Conditional注解：条件化地配置Bean组件， 可以用到带有@Bean注解的方法上，如果给定的条件计算结果为true，就创建这个bean，否则的话，这个bean就会被忽略
- 例子：
```java
//假设有一个名为`MagicBean`的类，我们希望设置了magic环境属性的是哦户，Spring才会实例化这个类，如果环境中没有这个属性，嘛呢MagicBean将会被忽略
//条件化的配置了MagicBean

@Bean
@Conditional(MagicExistsCondition.class)
public MagicBean magicBean(){
    return new MagicBean();
}
```
- 设置给@Conditional的类可以是任意实现了Condition接口的类型，这个接口实现起来很简单，**只需要提供matches()方法的实现即可**
    - 如果matches()方法返回true，那么就会创建带有@Conditional注解的bean，如果matches()方法返回false，将不会创建这些bean。

### ConditionContext接口
```java
public interface ConditionContext{
    BeanDefinitionRegistry getRegistry();               
    //借助 getRegistry() 返回的 BeanDefinitionRegistry 检查 bean 定义；
    ConfigurableListableBeanFactory getBeanFactory();
    //借助 getBeanFactory() 返回的 ConfigurableListableBean 检查 bean 是否存在，甚至探查bean的属性
    Enviroment getEnvironment();
    //借助 getEnvironment() 返回的Environment检查环境变量是否存在以及它的值是什么
    ResourceLoader getResourceLoader();
    //读取并探查getResourceLoader()返回的ResourceLoader所加载的资源；
    ClassLoader getClassLoader();
    //借助getClassLoader()返回的ClassLoader加载并检查类是否存在。
}
```

P114