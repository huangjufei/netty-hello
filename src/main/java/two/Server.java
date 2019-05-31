package two;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务端入口
 * 使用说明:复制index.html绝对路径到浏览器,作为客户端发送消息到服务端
 */
public class Server {

    public static void main(String[] args) {
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup,workGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new MyWebSocketChannelHandler());// 调用自己封装的处理类
            System.out.println("服务端开启等待客户端连接...");
            Channel ch = b.bind(8888).sync().channel();
            ch.closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 优雅地退出程序
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
