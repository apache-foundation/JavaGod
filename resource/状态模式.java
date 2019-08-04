//
//状态模式定义
//允许对象在内部状态改变时改变它的行为，
//对象看起来好像修改了它的类
//

/*
 * 糖果机的状态 不同的状态都有相同的方法但是实现不同
 */
public interface State {
    void chargeMoney();
    void moneyBack();
    void turnCrank();
}

class NoMoneyState implements State {
    private CandyMachine candyMachine;
    public NoMoneyState(CandyMachine candyMachine) {
        this.candyMachine = candyMachine;
    }

    @Override
    public void chargeMoney() {
        System.out.println("付款成功");
        candyMachine.setState(candyMachine.getHasMoneyState());
    }

    @Override
    public void moneyBack() {
        System.out.println("退款失败 没有钱");
    }

    @Override
    public void turnCrank() {
        System.out.println("请先投钱");
    }
}

class HasMoneyState implements State {
    private CandyMachine candyMachine;
    public HasMoneyState(CandyMachine candyMachine) {
        this.candyMachine = candyMachine;
    }

    @Override
    public void chargeMoney() {
        System.out.println("已经有钱了");
    }

    @Override
    public void moneyBack() {
        System.out.println("退钱成功");
        candyMachine.setState(candyMachine.getNoMoneyState());
    }

    @Override
    public void turnCrank() {
        System.out.println("已出糖");
        if (candyMachine.soldCandy() == 0) {
            candyMachine.setState(candyMachine.getSoldOutState());
        } else {
            candyMachine.setState(candyMachine.getNoMoneyState());
        }
    }
}

class SoldOutState implements State {
    private CandyMachine candyMachine;
    public SoldOutState(CandyMachine candyMachine) {
        this.candyMachine = candyMachine;
    }

    @Override
    public void chargeMoney() {
        System.out.println("已售完 自动退钱");
    }

    @Override
    public void moneyBack() {
        System.out.println("没钱");
    }

    @Override
    public void turnCrank() {
        System.out.println("已售空");
    }
}

/*
 * 糖果机 调用状态接口的方法 而不考虑现在是什么状态你状态在状态接口的实现类中自动切换
 */
public class CandyMachine {
    private State state;
    private NoMoneyState noMoneyState;
    private HasMoneyState hasMoneyState;
    private SoldOutState soldOutState;
    private int candyCount = 3;

    public CandyMachine() {
        noMoneyState = new NoMoneyState(this);
        hasMoneyState = new HasMoneyState(this);
        soldOutState = new SoldOutState(this);
        state = noMoneyState;
    }

    public void chargeMoney() {
        state.chargeMoney();
    }

    public void moneyBack() {
        state.moneyBack();
    }

    public void turnCrank() {
        state.turnCrank();
    }

    void setState(State state) {
        this.state = state;
    }

    NoMoneyState getNoMoneyState() {
        return noMoneyState;
    }

    HasMoneyState getHasMoneyState() {
        return hasMoneyState;
    }

    SoldOutState getSoldOutState() {
        return soldOutState;
    }

    int soldCandy() {
        return --candyCount;
    }
}

/* 
 * Main 类
 */
public class Main{
    public static void main(String[] args) {
        CandyMachine candyMachine = new CandyMachine();
        candyMachine.chargeMoney();
        candyMachine.moneyBack();
        candyMachine.chargeMoney();
        candyMachine.turnCrank();
        candyMachine.turnCrank();
    }
}