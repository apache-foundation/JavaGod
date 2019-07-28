//代理模式
//为另一个对象提供一个替身或占位符以控制这个对象的访问



//代理接口 代理类和实现类都必须实现这个接口
//图片接口 假如有一个页面要调用这个类的getImage()
public interface Image {
    int getSize();
    String getImage();
}

//代理类 当图片类还无法构造时代替图片类
public class ProxyImage implements Image{
    private RealImage realImage;
    private boolean   startLoad = false;

    @Override
    public int getSize() {
        if (realImage == null)
            return 0;
        else
            return realImage.getSize();
    }

    @Override
    public String getImage() {
        if (realImage == null) {
            if (!startLoad) {
                startLoad = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        realImage = new RealImage(12, "zhangqi");
                    }
                }).start();
            }
            return "loading.....";
        }
        else
            return realImage.getImage();
    }
}

//真正的图片类
public class RealImage implements Image{
    private int size;
    private String image;

    public RealImage(int size, String image) {
    	//这里假如要从服务器获取图片 需要等待一段时间
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.size = size;
        this.image = image;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getImage() {
        return image;
    }
}

//主类 假如这个是一个页面要显示一张图片
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Image image = new ProxyImage();
        for (int i = 0; i < 10; i++) {
            System.out.println(image.getImage());
            Thread.sleep(50);
        }
    }
}