package jvm.classloading;



import java.sql.SQLException;

public class Test {

    public void tell() {
        System.out.println("aaaa");
    }
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // 会调用静态代码块
//        Class.forName("jvm.classloading.SuperClass");
//        // 不会调用静态代码块
//        Test.class.getClassLoader().loadClass("jvm.classloading.SuperClass");
//        // 会调用静态代码块
//        new SuperClass();
//        // 不会调用静态代码块
//        SuperClass superClass = null;

//        Class.forName("com.mysql.jdbc.Driver");
//        Driver driver = null;

        // 如果单纯使用new和用forName 其实没有特别大的区别
        // 都会去调用静态代码块，不过用forName 可以对代码进行解耦
        // jdk 现在主要实现了一个 driver接口 但是由于数据库的差异
        // jdk 会将这个driver的接口的子类交给厂商去实现
        // 实际上new Driver new出来的是com.mysql.什么什么的Driver
        // 数据库的信息你写在配置文件里面
        // 运行时的实例就是数据库产商实现的东西
//        String url = "jdbc:mysql://111.231.255.225:3306/db_example";
//        Connection connection = DriverManager.getConnection(url, "root", "123456zjd");
//
//        java.util.Enumeration<java.sql.Driver> driverEnumeration = DriverManager.getDrivers();
//        java.sql.Driver driver1 = driverEnumeration.nextElement();
//
//        System.out.println(driver1.getMajorVersion());

    }

}
