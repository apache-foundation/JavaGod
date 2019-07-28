package Try;

public class Main2 {
	public static void main(String[] args) {
		selfManaged se = new selfManaged();
	}
}

class selfManaged implements Runnable {
	private Thread self = new Thread(this);

	public selfManaged() {
		self.start(); ////start()必须放在构造器的最后一行否则 对象还没有创建好就开始运行了 就会不稳定
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			System.out.println(i);
		}
	}
	
}

