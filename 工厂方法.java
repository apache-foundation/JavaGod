//
//工厂方法模式
//定义了一个创建对象的接口，但由子类决定要实例化的类是哪一个。工厂方法让类把实例化推迟到子类
//

/*
 * Pizza 工厂类
 */
public abstract class PizzaFactory {
    public Pizza orderPizza(String pizzaName) {
    	//我的理解是这里也不一定要做piazz 也可以不产生对象返回 工厂方法的关键是让子类产出需要的对象供父类调用
        Pizza pizza = createPizza(pizzaName);
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();
        return pizza;
    }
    protected abstract Pizza createPizza(String pizzaName);
}

class BJPizzaFactory extends PizzaFactory{

    @Override
    protected Pizza createPizza(String pizzaName) {
        if (pizzaName.equals("Chess"))
            return new BJChessPizza();
        else if (pizzaName.equals("Fruits"))
            return new BJFruitsPizza();
        else
            return null;
    }

}

class SHPizzaFactory extends PizzaFactory{

    @Override
    protected Pizza createPizza(String pizzaName) {
        if (pizzaName.equals("Chess"))
            return new SHChessPizza();
        else if (pizzaName.equals("Fruits"))
            return new SHFruitsPizza();
        else
            return null;
    }
}

/*
 * Pizza 类
 */
public abstract class Pizza {
    void prepare() {
        System.out.println("prepare pizza");
    }
    void bake() {
        System.out.println("bake pizza");
    }
    void cut() {
        System.out.println("cut pizza");
    }
    void box() {
        System.out.println("box pizza");
    }
}

class BJChessPizza extends Pizza {
    public BJChessPizza() {
        System.out.println("BJChessPizza");
    }
}

class BJFruitsPizza extends Pizza {
    public BJFruitsPizza() {
        System.out.println("BJFruitsPizza");
    }
}

class SHChessPizza extends Pizza {
    public SHChessPizza() {
        System.out.println("SHChessPizza");
    }
}

class SHFruitsPizza extends Pizza {
    public SHFruitsPizza() {
        System.out.println("SHFruitsPizza");
    }
}

/*
 * Main 类
 */
public class Main {
    public static void main(String[] args) {
        PizzaFactory BJFactory = new BJPizzaFactory();
        PizzaFactory SHFactory = new SHPizzaFactory();
        BJFactory.orderPizza("Chess");
        SHFactory.orderPizza("Fruits");
    }
}