//观察者模式
//定义了对象之间的一对多依赖, 这样一来,当一个对象改变状态时, 它的所有依赖者都会收到通知并自动更新

//被观察者接口
public abstract class Subject {
    protected ArrayList<Observer> observers = new ArrayList<Observer>();

    //将观察者注册到被观察者中
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    //当被观察发生变化时通知观察者
    public abstract void notifyObservers();
}

//观察者接口
public interface Observer {
    void update(float temp, float humidity);
}

//气象局
class MetOffice extends Subject{
    private float temp;
    private float humidity;

    public void notifyObservers() {
        for(int i = 0; i < observers.size(); i++) {
            Observer observer = observers.get(i);
            observer.update(temp, humidity);
        }
    }

    public void setMeasurements(float temp, float humidity) {
        this.temp = temp;
        this.humidity = humidity;
        notifyObservers();
    }
}

//A地区布告板
class DisplayInA implements Observer {
    private float temp;
    private float humidity;

    public void update(float temp, float humidity) {
        this.temp = temp;
        this.humidity = humidity;
    }

    public void display() {
        System.out.println("this is A temp: " + temp + " humidity: " + humidity);
    }
}

//B地区布告板
class DisplayInB implements Observer {
    private float temp;
    private float humidity;

    public void update(float temp, float humidity) {
        this.temp = temp;
        this.humidity = humidity;
    }

    public void display() {
        System.out.println("this is B temp: " + temp + " humidity: " + humidity);
    }
}