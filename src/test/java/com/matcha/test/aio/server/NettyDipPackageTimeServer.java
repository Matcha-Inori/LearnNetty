package com.matcha.test.aio.server;

import com.matcha.test.aio.server.handler.DipPackageTimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;

/**
 * Created by Matcha on 2017/3/12.
 */
public class NettyDipPackageTimeServer
{
    private static volatile NettyDipPackageTimeServer instance;

    public static NettyDipPackageTimeServer getInstance()
    {
        if(instance == null)
            createInstance();
        return instance;
    }

    private static synchronized void createInstance()
    {
        if(instance == null)
            instance = new NettyDipPackageTimeServer();
    }

    private NettyDipPackageTimeServer()
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
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(8668)).sync();
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
        NettyDipPackageTimeServer server = NettyDipPackageTimeServer.getInstance();
        server.start();
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>
    {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception
        {
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new DipPackageTimeServerHandler());
        }
    }
}
