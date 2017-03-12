package com.matcha.test.aio.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Matcha on 2017/3/12.
 */
public class DipPackageTimeServerHandler extends ChannelHandlerAdapter
{
    private AtomicLong count;

    public DipPackageTimeServerHandler()
    {
        this.count = new AtomicLong(0);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        long count = this.count.incrementAndGet();
        String message = (String) msg;
        System.out.println(String.format("%d\t%s", count, message));
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(message) ? new Date().toString() : "BAD ORDER";
        currentTime += System.getProperty("line.separator");
        ByteBuf byteBuf = Unpooled.copiedBuffer(currentTime.getBytes("UTF-8"));
        ctx.write(byteBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        cause.printStackTrace();
        ctx.close();
    }
}
