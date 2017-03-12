package com.matcha.test.aio.client;

import com.matcha.test.aio.client.handler.TimeClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by Matcha on 2017/3/12.
 */
public class NettyTimeClient
{
    public void start()
    {
        EventLoopGroup group = null;
        try
        {
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>()
                    {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception
                        {
                            socketChannel.pipeline().addLast(new TimeClientHandler());
                        }
                    });
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
            if(group != null)
                group.shutdownGracefully();
        }
    }

    public static void main(String[] args)
    {
        NettyTimeClient client = new NettyTimeClient();
        client.start();
    }
}
