package com.matcha.test.aio.server;

import com.matcha.test.aio.server.handler.TimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Matcha on 2017/3/12.
 */
public class NettyTimeServer
{
    private static volatile NettyTimeServer instance;

    public static NettyTimeServer getInstance()
    {
        if(instance == null)
            createInstance();
        return instance;
    }

    private static synchronized void createInstance()
    {
        if(instance == null)
            instance = new NettyTimeServer();
    }

    private NettyTimeServer()
    {

    }

    public void start()
    {
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;
        try
        {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());
            ChannelFuture future = bootstrap.bind(8668).sync();
            future.channel().closeFuture().sync();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally
        {
            if(bossGroup != null)
                bossGroup.shutdownGracefully();
            if(workerGroup != null)
                workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args)
    {
        NettyTimeServer nettyTimeServer = NettyTimeServer.getInstance();
        nettyTimeServer.start();
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>
    {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception
        {
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }
}
