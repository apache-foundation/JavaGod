//策略模式
//定义了算法族, 分别封装起来, 让它们之间可以互相替换, 此模式让算法的变化独立于使用算法的客户 


class Duck {
    private FlyBehavior flyBehavior = new NoFly();

    public void setFlyBehavior(FlyBehavior flyBehavior) {
        this.flyBehavior = flyBehavior;
    }

    //将这个方法变成“活的”, 可插拔使用的, 可以根据不同情况动态的设置不同的算法去解决问题
    public void toFly() {
        flyBehavior.fly();
    }
}

//Fly行为的接口
public interface FlyBehavior {
    void fly();
}

//三种Fly行为的具体实现
class UpFly implements FlyBehavior {
    public void fly() {
        System.out.println("up up up");
    }
}

class DownFly implements FlyBehavior {
    public void fly() {
        System.out.println("down down down");
    }
}

class NoFly implements FlyBehavior {
    public void fly() {
        System.out.println("I can't fly");
    }
}