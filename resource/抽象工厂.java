//
//抽象工厂
//提供一个接口，用于创建相关或依赖对象的家族，而不是需要明确指定的具体类
//
/*
 *电器类
 */
interface TV {
}

interface AirConditioner {
}

interface Refrigerator {
}

class GREETV implements TV {
    @Override
    public String toString() {
        return "GREETV";
    }
}

class GREEAirConditioner implements AirConditioner {
    @Override
    public String toString() {
        return "GREEAirConditioner";
    }
}

class GREERefrigerator implements Refrigerator {
    @Override
    public String toString() {
        return "GREERefrigerator";
    }
}

class HaierTV implements TV {
    @Override
    public String toString() {
        return "HaierTV";
    }
}

class HaierAirConditioner implements AirConditioner {
    @Override
    public String toString() {
        return "HaierAirConditioner";
    }
}

class HaierRefrigerator implements Refrigerator {
    @Override
    public String toString() {
        return "HaierRefrigerator";
    }
}
/*
 * 抽象工厂类
 */
public interface HAFactory { //家电工厂
    TV ProductTV();
    AirConditioner ProductAirConditioner();
    Refrigerator ProductRefrigerator();
}

class GREEFactory implements HAFactory {

    @Override
    public TV ProductTV() {
        return new GREETV();
    }

    @Override
    public AirConditioner ProductAirConditioner() {
        return new GREEAirConditioner();
    }

    @Override
    public Refrigerator ProductRefrigerator() {
        return new GREERefrigerator();
    }
}

class HaierFactory implements HAFactory {

    @Override
    public TV ProductTV() {
        return new HaierTV();
    }

    @Override
    public AirConditioner ProductAirConditioner() {
        return new HaierAirConditioner();
    }

    @Override
    public Refrigerator ProductRefrigerator() {
        return new HaierRefrigerator();
    }
}

/*
 * Main 类
 */
public class Main {
    public static void main(String[] args) {
        HAFactory GREEFactory = new GREEFactory();
        HAFactory HaierFactory = new HaierFactory();
        Product(GREEFactory);
        Product(HaierFactory);

    }

    public static void Product(HAFactory factory) {
        System.out.println(factory.ProductTV());
        System.out.println(factory.ProductAirConditioner());
        System.out.println(factory.ProductRefrigerator());
    }
}