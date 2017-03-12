package com.matcha.test.aio.client;

import com.matcha.test.aio.client.handler.DipPackageTimeClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;

/**
 * Created by Matcha on 2017/3/12.
 */
public class NettyDipPackageTimeClient
{
    public static void main(String[] args)
    {
        NettyDipPackageTimeClient client = new NettyDipPackageTimeClient();
        client.start();
    }

    public void start()
    {
        EventLoopGroup eventLoopGroup = null;
        try
        {
            eventLoopGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChildChannelHandler());
            bootstrap.bind(new InetSocketAddress(8667)).sync();
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(8668)).sync();
            channelFuture.channel().closeFuture().sync();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally
        {
            if(eventLoopGroup != null)
                eventLoopGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>
    {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception
        {
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new DipPackageTimeClientHandler());
        }
    }
}
