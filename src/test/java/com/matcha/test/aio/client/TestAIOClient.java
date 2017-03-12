package com.matcha.test.aio.client;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

/**
 * Created by Matcha on 2017/3/11.
 */
public class TestAIOClient
{
    public static void main(String[] args)
    {
        try(AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open())
        {
            socketChannel.bind(new InetSocketAddress(8687));
            socketChannel.connect(new InetSocketAddress(8668)).get();
            ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            String data = runtimeMXBean.getName() + ":" + runtimeMXBean.getVmName();
            byte[] dataBytes = data.getBytes("UTF-8");
            sizeBuffer.putInt(dataBytes.length);
            sizeBuffer.flip();
            ByteBuffer dataBuffer = ByteBuffer.allocate(dataBytes.length);
            dataBuffer.put(dataBytes);
            dataBuffer.flip();
            socketChannel.write(sizeBuffer).get();
            socketChannel.write(dataBuffer).get();

            sizeBuffer.clear();
            socketChannel.read(sizeBuffer).get();
            sizeBuffer.flip();
            int size = sizeBuffer.getInt();
            ByteBuffer inDataBuffer = ByteBuffer.allocate(size);
            socketChannel.read(inDataBuffer).get();
            inDataBuffer.flip();
            byte[] inDataBytes = new byte[size];
            inDataBuffer.get(inDataBytes);
            System.out.println(String.format("%s-%s", "client", new String(inDataBytes, "UTF-8")));
        }
        catch (IOException | InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
