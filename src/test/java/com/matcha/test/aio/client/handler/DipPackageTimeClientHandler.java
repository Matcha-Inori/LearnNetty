package com.matcha.test.aio.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Matcha on 2017/3/13.
 */
public class DipPackageTimeClientHandler extends ChannelHandlerAdapter
{
    private AtomicLong count;

    public DipPackageTimeClientHandler()
    {
        this.count = new AtomicLong(0);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        for(int i = 0;i < 100;i++)
        {
            String message = "QUERY TIME ORDER" + System.getProperty("line.separator");
            ByteBuf messageBuffer = Unpooled.copiedBuffer(message.getBytes("UTF-8"));
            ctx.writeAndFlush(messageBuffer);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        long count = this.count.incrementAndGet();
        String message = (String) msg;
        System.out.println(String.format("%d\tclient: %s", count, message));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        cause.printStackTrace();
        ctx.close();
    }
}
