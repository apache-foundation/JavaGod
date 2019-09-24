[TOC]



## ioc

https://www.jianshu.com/p/17b66e6390fd
IOC 把创建对象的控制权由转给转向容器，

*DIP\*即依赖倒置原则。上层类依赖下层类，controller层依赖service层，serveice层又依赖dao层。不在上层类实例化了，在容器中实例化，把底层类作为参数，传给上层类。上层建筑依赖下层的弊端在于，假如下层的构造函数新增一个参数，那么上层类就得改下层实例化代码。而且也不用每次都写一大堆初始化代码。

DI 依赖注入 Dependency Inject. 简单地说就是拿到的对象的属性，已经被注入好相关值了，直接使用即可。

*DIP\IOC\DI关系：依赖倒置原则给ioc思路，DI依赖注入是实现ioc思想的方法*

由容器根据配置文件去创建实例并创建各个实例之间的依赖关系。

 **IoC** 容器实际上就是个map，存放的是各种对象。spring启动时去读取去读取注解或者xml，获取bean配置信息生成一份相应的bean配置注册表。将bean的实例放在bean缓存池cmp里面，需要获取bean的时候看看缓存map里有没命中，没有就实例化bean，关键是利用反射动态的创建对象

目的：解耦， 使用注解注入依赖对象，不用再在代码中写依赖对象的setter方法或者该类的构造方法。

@Autowired和@Resource区别：@Autowired默认按类型装配，@Resource默认按名称装配，当找不到与名称匹配的bean时，才会按类型装配，找到之后，会对字段commonDao进行注入    @Resource(name="commonDao")    private ICommonDao commonDao;

核心接口：beanfactoryapplicationContext面向开发者，继承beanfactory

## Spring bean的生命周期？

![1564554784479](C:\Users\home.11\AppData\Roaming\Typora\typora-user-images\1564554784479.png)

Spring创建、管理对象。Spring容器负责创建对象，装配它们，配置它们并管理它们的整个生命周期。
•	实例化：Spring对bean进行实例化
•	填充属性：Spring将值和bean的引用注入到bean对应的属性中
•	调用BeanNameAware的setBeanName()方法：若bean实现了BeanNameAware接口，Spring将bean的id传递给setBeanName方法
•	调用BeanFactoryAware的setBeanFactory()方法：若bean实现了BeanFactoryAware接口，Spring调用setBeanFactory方法将BeanFactory容器实例传入
•	调用ApplicationContextAware的setApplicationContext方法：如果bean实现了ApplicationContextAware接口，Spring将调用setApplicationContext方法将bean所在的应用上下文传入
•	调用BeanPostProcessor的预初始化方法：如果bean实现了BeanPostProcessor，Spring将调用它们的叛postProcessBeforeInitialization方法
•	调用InitalizingBean的afterPropertiesSet方法：如果bean实现了InitializingBean接口，Spring将调用它们的afterPropertiesSet方法
•	如果bean实现了BeanPostProcessor接口，Spring将调用它们的postProcessAfterInitialzation方法
•	此时bean已经准备就绪，可以被应用程序使用，它们将一直驻留在应用杀死那个下文中，直到该应用的上下文被销毁。
•	如果bean实现了DisposableBean接口，Spring将调用它的destroy方法。
•	 

## bean的作用域？

•	单例（Singleton）：在整个应用中，只创建bean一个实例。
•	原型（Prototype）：每次注入或通过Spring应用上下文获取时，都会创建一个新的bean实例。
•	会话（Session）：在Web应用中，为每个会话创建一个bean实例。
•	请求（Request）：在Web应用中，为每个请求创建一个bean实例。
默认情况下Spring中的bean都是单例的。

## bean的注入方法有哪些？将一个类声明为Spring的 bean 的注解有哪些?

•	XML配置，如<bean id="">
•	Java配置即JavaConfig，使用@Bean注解
•	自动装配，组件扫描（component scanning）和自动装配（autowiring），@ComponentScan和@AutoWired注解
bean的注入方式有：
•	构造器注入
•	属性的setter方法注入
推荐对于强依赖使用构造器注入，对于弱依赖使用属性注入。

## 将一个类声明为Spring的 bean 的注解有哪些?

我们一般使用 @Autowired 注解自动装配 bean，要想把类标识成可用于 @Autowired 注解自动装配的 bean 的类,采用以下注解可实现
•	@Component ：通用的注解，可标注任意类为 Spring 组件。如果一个Bean不知道属于拿个层，可以使用@Component 注解标注。
•	@Repository : 对应持久层即 Dao 层，主要用于数据库相关操作。
•	@Service : 对应服务层，主要涉及一些复杂的逻辑，需要用到 Dao层。
•	@Controller : 对应 Spring MVC 控制层，主要用户接受用户请求并调用 Service 层返回数据给前端页面。

## Spring 管理事务的方式有几种？

•	编程式事务，在代码中硬编码。(不推荐使用)
•	声明式事务，在配置文件中配置（推荐使用）
声明式事务又分为两种：
•	基于XML的声明式事务
•	基于注解的声明式事务

## Spring 事务中的隔离级别有哪几种?

TransactionDefinition 接口中定义了五个表示隔离级别的常量：

•	TransactionDefinition.ISOLATION_DEFAULT: 使用后端数据库默认的隔离级别，Mysql 默认采用的 REPEATABLE_READ隔离级别 Oracle 默认采用的 READ_COMMITTED隔离级别.
•	TransactionDefinition.ISOLATION_READ_UNCOMMITTED: 最低的隔离级别，允许读取尚未提交的数据变更，可能会导致脏读、幻读或不可重复读
•	TransactionDefinition.ISOLATION_READ_COMMITTED: 允许读取并发事务已经提交的数据，可以阻止脏读，但是幻读或不可重复读仍有可能发生
•	TransactionDefinition.ISOLATION_REPEATABLE_READ: 对同一字段的多次读取结果都是一致的，除非数据是被本身事务自己所修改，可以阻止脏读和不可重复读，但幻读仍有可能发生。
•	TransactionDefinition.ISOLATION_SERIALIZABLE: 最高的隔离级别，完全服从ACID的隔离级别。所有的事务依次逐个执行，这样事务之间就完全不可能产生干扰，也就是说，该级别可以防止脏读、不可重复读以及幻读。但是这将严重影响程序的性能。通常情况下也不会用到该级别。

## Spring 事务中哪几种事务传播行为?

支持当前事务的情况：
•	TransactionDefinition.PROPAGATION_REQUIRED： 如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。**有就加没则新建**
•	TransactionDefinition.PROPAGATION_SUPPORTS： 如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务的方式继续运行。**有就加没则算了**
•	TransactionDefinition.PROPAGATION_MANDATORY： 如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。（mandatory：强制性）**有就加没有就抛出异常**

不支持当前事务的情况：
•	TransactionDefinition.PROPAGATION_REQUIRES_NEW： 创建一个新的事务，如果当前存在事务，则把当前事务挂起。**新建事务把之前的挂起**
•	TransactionDefinition.PROPAGATION_NOT_SUPPORTED： 以非事务方式运行，如果当前存在事务，则把当前事务挂起。**有事务就挂起**
•	TransactionDefinition.PROPAGATION_NEVER： 以非事务方式运行，如果当前存在事务，则抛出异常。**有事务就抛出异常**

其他情况：
•	TransactionDefinition.PROPAGATION_NESTED： 如果当前存在事务，则创建一个事务作为当前事务的嵌套事务来运行；如果当前没有事务，则该取值等价于TransactionDefinition.PROPAGATION_REQUIRED。

## Spring中涉及到哪些设计模式？

•	工厂方法模式。在各种BeanFactory以及ApplicationContext创建中都用到了；
•	单例模式。在创建bean时用到，Spring默认创建的bean是单例的；
•	代理模式。在AOP中使用Java的动态代理；
•	策略模式。比如有关资源访问的Resource类
•	模板方法。比如使用JDBC访问数据库，JdbcTemplate。
•	观察者模式。Spring中的各种Listener，如ApplicationListener
•	装饰者模式。在Spring中的各种Wrapper和Decorator
•	适配器模式。Spring中的各种Adapter，如在AOP中的通知适配器AdvisorAdapter

## spring怎么解用三级缓存解决单例bean的循环依赖？

spring单例对象的初始化大略分为三步：

1. createBeanInstance：实例化，其实也就是调用对象的构造方法实例化对象
2. populateBean：填充属性，这一步主要是多bean的依赖属性进行填充
3. initializeBean：调用spring xml中的init 方法。

## **SpringMVC** 工作原理了解吗?

![1564554039016](C:\Users\home.11\AppData\Roaming\Typora\typora-user-images\1564554039016.png)

上图的一个笔误的小问题：Spring MVC 的入口函数也就是前端控制器 DispatcherServlet 的作用是接收请求，响应结果。

流程说明（重要）：

1. 客户端（浏览器）发送请求，直接请求到 DispatcherServlet。
2. （映射）DispatcherServlet 根据请求信息调用 HandlerMapping，解析请求对应的 Handler。
3. 解析到对应的 Handler（也就是我们平常说的 Controller 控制器）后，开始由 HandlerAdapter 适配器处理。
4. HandlerAdapter 会根据 Handler 来调用真正的处理器开处理请求，并处理相应的业务逻辑。
5. 处理器处理完业务后，会返回一个 ModelAndView 对象，Model 是返回的数据对象，View 是个逻辑上的 View。
6. (映射)ViewResolver 会根据逻辑 View 查找实际的 View。
7. DispaterServlet 把返回的 Model 传给 View（视图渲染）。
8. 把 View 返回给请求者（浏览器）



## MyBatis和Hibernate的区别和应用场景？

Hibernate :是一个标准的ORM(对象关系映射) 框架； SQL语句是自己生成的，程序员不用自己写SQL语句。因此要对SQL语句进行优化和修改比较困难。适用于中小型项目。

MyBatis： 程序员自己编写SQL， SQL修改和优化比较自由。 MyBatis更容易掌握，上手更容易。主要应用于需求变化较多的项目，如互联网项目等。



[spring源码解析](https://www.jianshu.com/p/17b66e6390fd)