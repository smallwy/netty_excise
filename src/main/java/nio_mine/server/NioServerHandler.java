package nio_mine.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServerHandler implements Runnable {
        private ServerSocketChannel serverSocketChannel;
        private Selector selector;
        private static volatile boolean state=false;




    public NioServerHandler(int port) throws IOException {
        selector=Selector.open();
        serverSocketChannel= ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        state=true;
    }

    @Override
    public void run() {
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
                        hanleInput(selectionKey);
                    }catch (Exception e){
                        e.printStackTrace();
                        if(selectionKey!=null){
                            selectionKey.cancel();
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
    public void hanleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            if(key.isAcceptable()){
                 ServerSocketChannel serverSocketChannel=(ServerSocketChannel)key.channel();
                 SocketChannel socketChannel=  serverSocketChannel.accept();
                 socketChannel.configureBlocking(false);
                 socketChannel.register(selector,SelectionKey.OP_READ);

            }
            if (key.isReadable()) {
                ByteBuffer buffer=ByteBuffer.allocate(1024);//开辟内存空间
                SocketChannel socketChannel=(SocketChannel)key.channel();
                int  readBufff=socketChannel.read(buffer);
                if(readBufff>0){
                    buffer.flip();
                    byte[] bytes=new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String str=new String(bytes,"UTF-8");
                    System.out.println("服务器accept message is"+str);
                    doWrite(socketChannel,"do something success");
                }else {
                    key.cancel();
                    socketChannel.close();
                }
            }
        }
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
     * 停止对应的channel
     * @throws IOException
     */
    public void stop() throws IOException {
        if(serverSocketChannel!=null){
            serverSocketChannel.close();
        }
        state=false;
    }
}
