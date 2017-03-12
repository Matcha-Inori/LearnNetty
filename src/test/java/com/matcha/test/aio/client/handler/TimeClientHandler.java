package com.matcha.test.aio.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * Created by Matcha on 2017/3/12.
 */
public class TimeClientHandler extends ChannelHandlerAdapter
{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String message = runtimeMXBean.getName() + runtimeMXBean.getVmName();
        byte[] messageBytes = message.getBytes("UTF-8");
        ByteBuf byteBuf = Unpooled.buffer(4 + 4 + messageBytes.length);
        byteBuf.writeInt(0x0001);
        byteBuf.writeInt(messageBytes.length);
        byteBuf.writeBytes(messageBytes);
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        ByteBuf message = (ByteBuf) msg;
        byte[] messageBytes = new byte[message.readableBytes()];
        message.readBytes(messageBytes);
        System.out.println(String.format("%s-%s", "client", new String(messageBytes, "UTF-8")));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        cause.printStackTrace();
        ctx.close();
    }
}
