package nio_mine.client;

import java.io.IOException;
import java.util.Scanner;

public class ClientSocket {

    private static NioClientHandler nioClientHandler;
    public static void start() throws IOException {
        if(nioClientHandler!=null){
            nioClientHandler.stop();
        }
        nioClientHandler=new NioClientHandler("127.0.0.1",1234);
        new Thread(nioClientHandler,"客户端线程").start();
    }

    public static boolean send(String msg) throws IOException {
        nioClientHandler.sendMsg(msg);
        return true;
    }

    public static void main(String[] args) throws IOException {
        start();
        Scanner scanner=new Scanner(System.in);
        while (ClientSocket.send(scanner.next()));
    }
}

