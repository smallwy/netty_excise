package netty_01.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EhoServer {

    private  int port;

    public EhoServer(int port) {
        this.port = port;
    }

    final EhoServerHandler ehoServerHandler=new EhoServerHandler();

    public void start() throws InterruptedException {
        EventLoopGroup eventLoopGroup =new NioEventLoopGroup();
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(eventLoopGroup).channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(ehoServerHandler)  ;
            }
        });
        ChannelFuture channelFuture= serverBootstrap.bind().sync();
        channelFuture.channel().closeFuture().sync();
    }


    public static void main(String[] args) throws InterruptedException {
        new EhoServer(9876).start();
    }
}
