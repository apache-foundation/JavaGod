# juc包脉络梳理

*J.U.C* 即java.util.concurrent，由Doug Lea大神编写，在jdk1.5之后引入，由提供java并发解决方案的包。

juc有两大核心原理：**CAS**是原子类的基础,AQS是锁、信号量等的基础。内容我将分为五部分介绍。

[TOC]

![JUC](C:\Users\home.11\Desktop\小a笔记\images\JUC.png)



《java并发的艺术》指出了J.U.C包的基本操作

- 首先,声明共享变量为 volatile
- 然后,使用CAS的原子条件更新来实现线程之间的同步。
- 同时,配合以 volatile的读/写和CAS所具有的 volatile读和写的内存语义来实现线程之间的通信。

## 两大核心

### **CAS**是原子类的基础

### **AQS**是锁、信号量等的基础



![图片来源美团技术博客](C:\Users\home.11\Desktop\小a笔记\images\乐悲观锁.png)

## 1、锁（AQS）:可重入锁、读写锁、condition、lockSupport

### 读写锁

- 读写锁维护了一对锁，一个读锁和一个写锁。读写锁在同一时刻可以允许多个读线程访问，但是在**写线程访问时，所有的读线程和其他写线程均被阻塞**，目的是读取到正确的数据，不会出现脏读。
- 它通过分离读和写，提示了性能。
- 使用场景：读多写少，比如一个共享的用作缓存数据结构，大部分时间提供读服务（例如查询和搜索）。

## 2、线程执行器：线程池、FutureTask

线程池最重要的知识点是线程的创建时机。

- 线程池是**懒加载**的，声明的时候并不会创建好线程等待任务，而是当提交第一个任务时才去新建线程；
- 当提交一个任务时，如果当前线程数**小于corePoolSize**，就直接创建一个新线程执行任务；
- 如果当前线程数**大于corePoolSize**，继续提交的任务被保存到**阻塞队列中**，等待被执行；
- 如果阻塞队列满了，并且当前线程数**小于maxPoolSize**，那就创建新的线程执行当前任务；
- 如果池里的线程数**大于maxPoolSize**,这时再有任务来，只能调用**拒绝策略**。

## 3、并发集合类：Chm、阻塞队列

chm见HashMap一文。

### CopyOnWriteArrayList

#### 读写分离

- 写操作在一个复制的数组上进行，读操作还是在原始数组中进行，读写分离，互不影响。
- 写操作需要加锁，防止并发写入时导致写入数据丢失。
- 写操作结束之后需要把原始数组指向新的复制数组。

#### 适用场景

CopyOnWriteArrayList 在写操作的同时允许读操作，大大提高了读操作的性能，因此很适合**读多写少**的应用场景。

但是 CopyOnWriteArrayList 有其缺陷：

- 内存占用：在写操作时需要复制一个新的数组，使得**内存**占用为原来的两倍左右；
- 数据不一致：**读操作不能读取实时性的数据**，因为部分写操作的数据还未同步到读数组中。

所以 CopyOnWriteArrayList 不适合内存敏感以及对实时性要求很高的场景。



### 阻塞队列

多用于生产者消费者模式。JDK7 提供了 7 个阻塞队列。分别是

- ArrayBlockingQueue ：一个由**数组**结构组成的**有界**阻塞队列。
- LinkedBlockingQueue ：一个由**链表**结构组成的**有界**阻塞队列。
- PriorityBlockingQueue ：一个支持**优先级排序**的**无界**阻塞队列。
- DelayQueue：一个使用**优先级队列**实现的**无界**阻塞队列。
- SynchronousQueue：一个**不存储元素**的阻塞队列。
- LinkedTransferQueue：一个由**链表**结构组成的**无界**阻塞队列。
- LinkedBlockingDeque：一个由**链表**结构组成的**双向**阻塞队列。

以LinkedBlockingQueue为例子，最重要的两个方法是**put和take**。如果put或者take操作无法立即执行，这两个方法调用将会发生**阻塞**，直到能够执行。

|          | *抛出异常*  | *特殊值*   | *阻塞*   |         *超时*         |
| -------- | ----------- | ---------- | -------- | :--------------------: |
| **插入** | `add(e)`    | `offer(e)` | `put(e)` | `offer(e, time, unit)` |
| **移除** | `remove()`  | `poll()`   | `take()` |   `poll(time, unit)`   |
| **检查** | `element()` | `peek()`   | *不可用* |        *不可用*        |



## 4、原子类(CAS)

原子类就具有原子操作特征的类，方便无锁的进行原子操作。

它基于CAS为原理，CAS全称 Compare And Swap（比较与交换），在更新一个变量的时候，拿**预期值**与当前内存里的值做比较，一致才做更新操作，否则抛弃当前更新值，进行重试或抛出异常。

原子类通过调用Unsafe类的本地方法compareAndSwapInt（）来对变量最更新操作。



CAS的**关键**在于比较和更新这两个操作是一个整体，操作系统的CMPXCHG指令通过lock前缀保证**比较和更新两个操作是原子的**。

CAS的是一种无锁算法（是没有线程被阻塞，而不是不加锁），合适读多写少的情况，原子类尤其适合更新变量的情况。



## 5、并发工具类信号量、倒计时器、循环栅栏、交换器、Executors

并发工具类包装了一些**并发控制工具**。

- 信号量Semaphore，**限制访问资源的线程数目。**
- 倒计时器CountDownLatch，**让主线程等待一组事件发生后继续执行**，这里的事件就是指countDown()。类似“倒数十个数火箭就发射”。

```
CountDownLatch countDownLatch = new CountDownLatch(3);

//下面的代码是在每个线程里。。。
countDownLatch.countDown();
System.out.println("countDown()后的这句话不会被阻塞，每个线程还可以做其他工作");

//countDownLatch.await();
System.out.println("await()后的这句话会被阻塞，直到倒计数3次全完成了，才会输出");
```



- 循环栅栏CyclicBarrier，**阻塞当前线程，等待其他线程都完成了，所有线程同时触发执行某事件。**类似“公寓的班车总是在公寓楼下装满一车人之后，出发并开到地铁站，接着再回来接下一班人。”构造函数需要传入两个参数，第一个参数指定我们的屏障最多拦截多少个线程后就打开屏障，第二个参数指明最后一个到达屏障的线程需要额外做的操作。注意屏障是循环发生的

```
 CyclicBarrier barrier = new CyclicBarrier(3,new BarrierTask)
//下面的代码是在每个线程里。。。
System.out.println(thread.getName()+"调用await方法");
 barrier.await();
 System.out.println("任务完成");
 barrier.await();
 System.out.println("任务完成");
 
 // BarrierTask任务是输出System.out.println("BarrierTask");
 
/*输出结果:
         thread1.调用await方法
         thread2.调用await方法
         BarrierTask
         任务完成
         thread1.调用await方法
         thread2.调用await方法
         BarrierTask*/
```



与CountDownLatch与CyclicBarrier的区别：

1. CountDownLatch一旦倒计数完成，await 方法就会失效，它是**一次性**的。CyclicBarrier可以重置倒计数器，它是**循环**发生的。
2. CountDownLatch**强调一个线程等待其他线程执行完成后才能继续执行**，countDown()后的代码是**不阻塞**的，await()才是阻塞的。CountDownLatch**强调线程之间的相互等待**。它没有countDown()方法，用await()做计数。必须所有线程等准备完毕，一次性全部激活执行第二个参数的任务。像一个百米冲刺一样。



> 网上并发学习资源
>
> [不可不说的Java“锁”事](https://tech.meituan.com/2018/11/15/java-lock.html)  美团技术团队的文章





## 总结

线程池

- 好处
- 参数：核心、最大、存活时间和单位、集合
- 创建时机
- 拒绝策略 直接丢、丢了抛异常、丢老的、新建线程
- 合理分配线程池大小



原子类

- 顾名思义
-  cas：概念、乐观锁、无锁
- unsafe指针和偏移量
- 关键：比较交换操作数指令
- 问题：ABA和自旋、
- 整形、数组、更新器、引用与版本号。



hashmap

-  KV散列表，默认1<<4，扩容oldCap << 1
- 1.7数组+双向链表，结合数组和链表的优点。1.8红黑树优化，树化阈值和最小容量.get方法对null值的处理
- 负载因子0.75自动扩容
- 位运算：2次幂、扰动函数、链表复制
- 线程不安全：结点更新丢失、死循环。可以用集合工具类synchronizedMap、HashTable、chm
- 与ht区别：null、安全、字典、2次幂