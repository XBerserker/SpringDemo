## 1. Spring 配置方案
- Spring容器负责创建应用程序中的bean并通过DI来协调这些对象之间的关系
- Spring 提供了三种主要的装配机制：
  - 在 XML 中进行显示配置
  - 在 Java 中进行显示配置
  - 隐式的 bean 发现机制和自动装配
- **建议：尽可能地使用自动配置的机制，显示配置越少越好。**
- 使用装配机制的优先级：隐式的 bean 发现机制和自动装配 > 在 Java 中进行显示配置 > 在 XML 中进行显示配置

### 1. 创建可被发现的bean
1. 使用 @Component 注解，表明该类会作为组件类，并告知 Spring 要为这个类创建bean。
  - 没有必要显示配置 SgtPeppersbean， 使用了 @Component 注解，Spring 会处理妥当
2. 需要显式的配置组件扫描
```java
@Configuration
@ComponentScan  //使用 @ComponentScan 注解，在Spring中启用组件扫描
public class CDPlayerConfig{
  //如果没有其他配置的话，@ComponentScan默认会扫描与配置类相同的包。
  //因为 CDPlayerConfig类位于soundsystem包中，因此Spring将会扫描这个包下的所有子包，查找带有@Component注解的类。这样的话，就能够发现CompactDisc，并且会在Spring中自动为其创建一个bean。
}
```

### 2. 给组件扫描的 bean 命名
- Spring应用上下文中所有的bean都会给定一个ID。在前面的例子中，尽管我们没有明确地为SgtPeppersbean设置ID，但Spring会根据类名为其指定一个ID。具体来讲，这个bean所给定的ID为sgtPeppers，也就是将类名的第一个字母变为小写。
- 为 bean 设置不一样的ID：
```java
//第一种方法
@Component("loneyHeartsClub")
public class SgtPeppers implements CompactDisc{
  //Some code here!!!
}

//第二种方法(使用了 Java 依赖注入规范所提供的@Named)——不推荐
import javax.inject.Named;

@Named("loneyHeartsClub")
public class SgtPeppers implements CompactDisc{
  //Some code here~~~
}
```

### 3. 设置组件扫描的基础包
- 有一个原因会促使我们明确地设置基础包，那就是我们想要将配置类放在单独的包中，使其与其他的应用代码区分开来。如果是这样的话，那默认的基础包就不能满足要求了。
- 为了指定不同的基础包，你所需要做的是在 @ComponentScan 的 value 属性中指明包的名称：
```java
//在@ComponentScan 的 value 属性中指明包的名称
@Configuration
@ComponentScan("soundsystem")
public class CDPlayerConfig{}

//如果想要更加清晰的表明设置的是基础包
//可以通过 basePackages 属性进行配置
@Configuration
@ComponentScan(basePackages="soundsystem")
public class CDPlayerConfig{}
```

