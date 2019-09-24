//命令模式
//将“请求”封装成对象, 以便使用不同的请求 队列或日志来参数化其他对象
//命令模式的意义在于解耦 解除 请求者 和 被调用者 和 调用方式(要求被调用者做出的动作) 之间的耦合性


//命令接口 
interface Command {
    void execute();
}

//遥控器 在这里实现解耦 被调用者储存在这个对象中 调用者进行调用 
//被调用的对象可以随意更换 调用者也是想用哪个用哪个 想怎么操作就怎么操作 而不用改代码
class RemoteControl {
    private Command[] commands;
    public RemoteControl(int num) {
        commands = new Command[num];
    }

    public void setCommand(int i, Command command) {
        commands[i] = command;
    }

    public void press(int i) {
        commands[i].execute();
    }
}

//电视接口及其实现类 被调用者
interface TV{
    void prepare();
    void on();
}

class BigTV implements TV{
    public void prepare() {
        System.out.println("将遥控器对准大电视");
    }

    public void on() {
        System.out.println("打开了大电视");
    }
}

class OldTV implements TV{
    public void prepare() {
        System.out.println("将遥控器对准旧电视");
    }

    public void on() {
        System.out.println("打开了旧电视");
    }
}

//打开电视命令 调用方式
class OnTV implements Command {
    private TV tv;

    public OnTV(TV tv) {
        this.tv = tv;
    }

    public void execute() {
        tv.prepare();
        tv.on();
    }
}

//电灯接口及其实现类 被调用者
interface Light{
    void on();
}

class RedLight implements Light{
    public void on() {
        System.out.println("打开了红色的灯");
    }
}

class BlueLight implements Light{
    public void on() {
        System.out.println("打开了蓝色的灯");
    }
}

//打开灯命令 调用方式
class OnLight implements Command {
    private Light light;

    public OnLight(Light light) {
        this.light = light;
    }

    public void execute() {
        light.on();
    }
}

//主函数
public class Test {
    public static void main(String[] args) {
        Command onBigTV = new OnTV(new BigTV());
        Command onOldTV = new OnTV(new OldTV());
        Command onRedLight = new OnLight(new RedLight());
        Command onBlueLight = new OnLight(new BlueLight());

        RemoteControl control = new RemoteControl(4);
        control.setCommand(0, onBigTV);
        control.setCommand(1, onOldTV);
        control.setCommand(2, onRedLight);
        control.setCommand(3, onBlueLight);

        control.press(0);
        control.press(1);
        control.press(2);
        control.press(3);
    }
}