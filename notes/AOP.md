## 1. 应用切面
- 定义：**促使软件系统实现关注点的分离一项技术。**
- 每个组件除了核心功能，还需承担额外的职责，诸如日志、事务管理的服务融入到自身具有的核心业务逻辑的组件中去，**这些服务通常被称为横切关注点**，因为它们会跨越系统的多个组件
- 如果将这些关注点分散到多个组件中去，代码会带来双重的复杂性：
  1. 代码会重复出现在多个组件中
  1. 组件会因为那些与自身核心业务无关的代码而变得混乱。
- 我们可以把切面想象为覆盖在很多组件之上的一个外壳。应用是由那些实现各自业务功能的模块组成的。借助AOP，可以使用各种功能层去包裹核心业务层。这些层以声明的方式灵活地应用到系统中，你的核心应用甚至根本不知道它们的存在。这是一个非常强大的理念，可以将安全、事务和日志关注点与核心业务逻辑相分离

## 2. 模板
1. Spring 旨在通过模板封装来消除样板式代码。Spring 的 JdbcTemplate 使得执行数据库操作时，避免传统的 JDBC 样板代码称为可能
    1. 使用 Spring 的 JdbcTemplate 重写方法，仅仅关注核心逻辑，而不需要迎合诸如 JDBC API 之类的需求
```java
public Employee getEmployeeById(long id){
    return jdbcTemplate.queryForObject{
        "select id, firstname, lastname, salary"+"from employee where id=?", new RowMapper<Employee>(){
                public Employee mapRow(ResultSet rs, int rowNum) throws SQLException{
                Employee employee = new Employee();
                employee.setId(rs.getLong("id"));
                employee.setFirstName(rs.getString("firstname"));
                employee.setLastName(rs.getString("lastname"));
                employee.setSalary(rs.getString("salary"));
                return employee;
            }
        },
        id};
}
```

# Spring 容器
## 1. 使用上下文
## 2. bean 生命周期
  1. Spring对bean进行实例化；
  2. Spring将值和bean的引用注入到bean对应的属性中；
  3. 如果bean实现了BeanNameAware接口，Spring将bean的ID传递给setBean-Name()方法；
  4. 如果bean实现了BeanFactoryAware接口，Spring将调用setBeanFactory()方法，将BeanFactory容器实例传入；
  5. 如果bean实现了ApplicationContextAware接口，Spring将调用setApplicationContext()方法，将bean所在的应用上下文的引用传入进来；
  6. 如果bean实现了BeanPostProcessor接口，Spring将调用它们的post-ProcessBeforeInitialization()方法；
  7. 如果bean实现了InitializingBean接口，Spring将调用它们的after-PropertiesSet()方法。类似地，如果bean使用initmethod声明了初始化方法，该方法也会被调用；
  8. 如果bean实现了BeanPostProcessor接口，Spring将调用它们的post-ProcessAfterInitialization()方法；
  9. 此时，bean已经准备就绪，可以被应用程序使用了，它们将一直驻留在应用上下文中，直到该应用上下文被销毁；
  10. 如果bean实现了DisposableBean接口，Spring将调用它的destroy()接口方法。同样，如果bean使用destroy-method声明了销毁方法，该方法也会被调用。

# Spring 概览
