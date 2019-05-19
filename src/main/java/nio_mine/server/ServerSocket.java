package nio_mine.server;
import java.io.IOException;

public class ServerSocket {


    private static NioServerHandler nioServerHandler;

    public static void start() throws IOException {
        if(nioServerHandler!=null){
            nioServerHandler.stop();
        }
        nioServerHandler=new NioServerHandler(1234);
        new Thread(nioServerHandler,"服务器已经启动").start();
    }


    public static void main(String[] args) throws IOException {
        start();
    }
    
}
