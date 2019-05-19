package nio_mine.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 *客户端处理器
 */
public class NioClientHandler implements Runnable {

        private String host;
        private int port;
        private Selector selector;//论选监听
        private SocketChannel socketChannel;

        private static volatile boolean state=false;

    public NioClientHandler(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        selector=Selector.open();
        socketChannel=SocketChannel.open();
        socketChannel.configureBlocking(false);//设置为非阻塞
        state=true;
    }

    @Override
    public void run() {
        try{
            doConnect();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        while (state){
            try {
                selector.select();
                Set<SelectionKey> set=   selector.selectedKeys();
                Iterator it=set.iterator();
                SelectionKey selectionKey=null;
                while (it.hasNext()){
                    selectionKey=  (SelectionKey) it.next();
                    it.remove();
                    try {
                        handlerKey(selectionKey);
                    }catch (Exception e){
                        e.printStackTrace();
                        if(selectionKey!=null){
                            selectionKey.cancel();
                            socketChannel.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(selector!=null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlerKey(SelectionKey selectionKey) throws IOException {
        if(selectionKey.isValid()){
        SocketChannel socketChannel=(SocketChannel)selectionKey.channel();
        if(selectionKey.isConnectable()){
            if(socketChannel.finishConnect()){
            }else {
                System.exit(1);
            }
        }
        if (selectionKey.isReadable()) {
            ByteBuffer buffer=ByteBuffer.allocate(1024);//开辟内存空间
            int  readBufff=socketChannel.read(buffer);
            if(readBufff>0){
                buffer.flip();
                byte[] bytes=new byte[buffer.remaining()];
                buffer.get(bytes);
                String str=new String(bytes,"UTF-8");
                System.out.println("accept message is"+str);
            }else {
                selectionKey.cancel();
                socketChannel.close();
            }

        }

        }
    }

    public void sendMsg(String msg) throws IOException {
        socketChannel.register(selector,SelectionKey.OP_READ);
        doWrite(socketChannel,msg);
    }

    private void doWrite(SocketChannel socketChannel, String msg) throws IOException {
        byte[]  bytes=msg.getBytes();
        //获取的数据转化为字节数组
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        //将数据放入buff中
        byteBuffer.put(bytes);
        //切换为
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

    }

    /**
     * 创建连接  如果没有创建成功 那么注册到监听上面
     * @throws IOException
     */
    public void doConnect() throws IOException {
        if(socketChannel.connect(new InetSocketAddress(host,port))){
            System.out.printf("和服务器连接建立成功");
        }else {
            //注册连接事件
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }
    /**
     * 停止对应的channel
     * @throws IOException
     */
    public void stop() throws IOException {
        if(socketChannel!=null){
            socketChannel.close();
        }
        state=false;
    }
}
