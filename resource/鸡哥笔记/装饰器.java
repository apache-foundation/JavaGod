//
//装饰器模式的定义
//动态的将责任附加到对象上。若要扩展功能，装饰者提供了比继承更有弹性的设计方案
//java i/o那几个类用的就是这个设计模式
//
/*
Beverage 饮料类
*/
public abstract class Beverage {
    private int price;
    private String description;

    protected Beverage(int price, String description) {
        this.price = price;
        this.description = description;
    }

    public int cost() {
        return price;
    }

    public String description() {
        return description;
    }
}

/*
CondimentDecorator 调料类
*/
public abstract class CondimentDecorator extends Beverage{
    Beverage beverage;

    protected CondimentDecorator(int price, String description, Beverage beverage) {
        super(price, description);
        this.beverage = beverage;
    }

    @Override
    public int cost() {
        return super.cost() + beverage.cost();
    }

    @Override
    public String description() {
        return super.description() + " " + beverage.description();
    }
}

/*
Main 类
*/
public class Main {
    public static void main(String[] args) {
        Beverage darkRoast = new DarkRoast();
        darkRoast = new Milk(darkRoast);
        darkRoast = new Mocha(darkRoast);
        System.out.println(darkRoast.cost());
        System.out.println(darkRoast.description());
    }
}

class DarkRoast extends Beverage {

    public DarkRoast() {
        super(10, "DarkRoast");
    }
}

class Milk extends CondimentDecorator {

    public Milk(Beverage beverage) {
        super(1, "Milk", beverage);
    }
}

class Mocha extends CondimentDecorator {

    public Mocha(Beverage beverage) {
        super(2, "Mocha", beverage);
    }
}
