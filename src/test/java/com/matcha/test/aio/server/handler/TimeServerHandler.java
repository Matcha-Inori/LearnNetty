package com.matcha.test.aio.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * Created by Matcha on 2017/3/12.
 */
public class TimeServerHandler extends ChannelHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        ByteBuf byteBuf = (ByteBuf) msg;
        int command = byteBuf.readInt();
        if(command != 0x0001)
            return;
        int size = byteBuf.readInt();
        byte[] data = new byte[size];
        byteBuf.readBytes(data);
        System.out.println(new String(data, "UTF-8"));
        String currentDate = new Date().toString();
        ByteBuf outDataBuffer = Unpooled.copiedBuffer(currentDate.getBytes("UTF-8"));
        ctx.write(outDataBuffer);
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
