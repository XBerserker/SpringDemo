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
@ComponentScan(basePackages={"soundsystem", "video"})    //这种方法是类型不安全的，当重构代码时，那指定的基础包可能就会出现错误
public class CDPlayerConfig{}

//推荐方法
//将其指定为包中所包含的类或接口
@org.springframework.context.annotation.Configuration
@org.springframework.context.annotation.ComponentScan(basePackageClasses = {CDplayer.class, DVDPlayer.class})
public class  CDPlayerConfig{}

/**
  * 如果所有对象都是独立的，彼此间没有任何依赖，所需的可能就是组件扫描而已
  * 但是很多对象会依赖其他对象才能完成任务，我们需要一种方法能够将组件扫描得到的bean和它们的依赖装配在一起。
  * 那就是“自动装配”
  */

```

### 4. 通过注解实现自动装配
- 为了声明要进行自动装配，我们可以借助`@AutoWired`注解
- 不管是构造器、Setter方法还是其他的方法，Spring都会尝试满足方法参数上所声明的依赖。如果只有一个bean匹配依赖需求的话，那这个bean将会被装配进来
- 如果没有匹配的bean，那么在应用上下文创建的时候，Spring会抛出异常，*为了避免异常的出现， 你可以将@AutoWired的required属性设置为false*
```java
@org.springframework.beans.factory.annotation.Autowired(required = false)
public CDPlayer(CompactDisc cd){
    this.cd = cd;
}
```

### 验证
略。。。


## 3. 通过 Java 代码显示装配bean
- **应用场景：**使用第三方库中的组件装配到应用中，这种情况下，是没有办法在它的类上添加@Component和@Autowired注解的，因此就不能使用自动化装配的方案了
- **注意：**JavaConfig与其他的Java代码是有所区别的，它不应该包含任何的业务逻辑，JavaConfig也不应该侵入到业务逻辑代码之中。通常，将JavaConfig放到单独的包中，使其与其他的应用程序逻辑分离开来。

### 1. 创建配置类
```java
@Configuration  //表明这是一个配置类
//@ComponentScan    //注释后，抛出BeanCreation-Exception 异常
public class CDPlayerConfig {
}

```

### 2. 生命简单的bean
- 要在JavaConfig中生命bean，我们需要编写一个方法，这个方法会创建所需类型的实例，然后给这个方法添加@Bean注解:
```java
@org.springframework.context.annotation.Bean    //默认的bean的名字是和方法名一样的，这里默认的是sgtPeppers
//@Bean(name="lonelyHeartsClubBand")        //为bean重新指定一个不同的名字
public CompactDisc sgtPeppers(){
    return new SgtPeppers();
}
```
- @Bean注解会告诉Spring这个方法将会返回一个对象，该对象要注册为Spring应用上下文中的bean。方法体中包含了最终产生bean的实例的逻辑
- **默认情况下，Spring中的bean都是单例的**
```java
@org.springframework.context.annotation.Bean
public CDPlayer cdPlayer(){
    return new CDPlayer(sgtPeppers());
}

@org.springframework.context.annotation.Bean
public CDPlayer anotherCDPlayer(){
    return new CDPlayer(sgtPeppers());
}
```
  - Spring会拦截对sgtPeppers()的调用并确保返回的是Spring所创建的bean，也就是Spring本身调用sgtPeppers()时所创建的CompactDiscbean，因此，两个CDPlayer bean会得到相同的SgtPeppers实例

- **另一个理解起来更为简单的方式：**
```java
@org.springframework.context.annotation.Bean
public CDPlayer cdPlayer(CompactDisc compactDisc){
    return new CDPlayer(compactDisc);
}
```
  - cdPlayer() 方法请求一个CompactDisc作为参数，当Spring调用cdPlayer()创建CDPlayer bean 的时候，它会自动装配一个CompactDisc到配置方法之中。然后，方法体就可以按照合适的方式来说使用它。借助这种技术，cdPlayer()方法也能够将CompactDisc注入到CDPlayer的构造器中，而不用明确引用CompactDisc的@Bean方法。
  - 通过这种方式引用其他的bean通常是最佳的选择，因为它不会要求将CompactDisc声明到同一个配置类之中。甚至没有要求CompactDisc必须要在JavaConfig中声明，实际上它可以通过组件扫描功能自动发现或者通过XML来进行配置。
  -**注意：**这里使用CDPlayer的构造器实现了DI 功能，但是我们完全可以采用其他风格的DI配置。比如说，你想通过Setter方法注入CompactDisc的话：
  ```java
  @org.springframework.context.annotation.Bean
  public CDPlayer cdPlayer(CompactDisc compactDisc){
    CDPlayer cdPlayer = new CDPlayer(compactDisc);
    cdPlayer.setCompactDisc(compactDisc);
    return cdPlayer;
  }
  ```
  - 带有@Bean注解的方法可以采用任何必要的Java功能来产生bean实例。构造器和Setter方法只是@Bean方法的两个简单样例。
  
## 4. 通过XML装配bean
- 碰到的时候再回来看


## 5. 导入和混合配置
- 关于混合配置，第一件需要了解的事情就是在自动装配时，它并不在意要装配的bean来自哪里。自动装配的时候会考虑到Spring容器中所有的bean，不管他是在JavaConfig或XML中声明的还是通过组件扫描获取到的。
1. JavaConfig中引用XML配置
  1. 使用@Import注解，可以应用其他类或者XML中的Bean组件，推荐使用Java配置的方式来引用，而不是使用String的方式
    - 因为String引用Bean是类型不安全的，当项目重构的时候，就会发生这样那样的错误
    - 而使用JavaConfig的方式配置，就不会有这样那样的问题出现了，这种方式是官方推荐的。
    
## 6. Bean组件装配总结
- Spring装配Bean的三种主要方式：
  1. 自动化，隐式配置；
  2. 基于Java的显式配置
  3. 基于XML的显示配置
- 自动化配置的好处：
  1. 可以避免显示配置所带来的维护成本， 使用显示配置Spring的话，应该优先选择基于Java的配置
  2. 比XML的配置更加强大、类型安全、易于重构
- 依赖注入是Spring中非常重要的组成部分




















