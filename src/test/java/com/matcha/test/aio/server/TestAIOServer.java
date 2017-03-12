package com.matcha.test.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by Matcha on 2017/3/8.
 */
public class TestAIOServer
{
    private static volatile TestAIOServer instance;

    public static TestAIOServer getInstance()
    {
        if(instance == null)
            createInstance();
        return instance;
    }

    private static synchronized void createInstance()
    {
        if(instance == null)
            instance = new TestAIOServer();
    }

    private CountDownLatch countDownLatch;

    private TestAIOServer()
    {
        countDownLatch = new CountDownLatch(1);
    }

    public void start()
    {
        try(AsynchronousServerSocketChannel channel = AsynchronousServerSocketChannel.open())
        {
            channel.bind(new InetSocketAddress(8668));
            channel.accept(this, new CompletionHandler<AsynchronousSocketChannel, TestAIOServer>()
            {
                @Override
                public void completed(AsynchronousSocketChannel result, TestAIOServer attachment)
                {
                    attachment.accept(result);
                    channel.accept(attachment, this);
                }

                @Override
                public void failed(Throwable exc, TestAIOServer attachment)
                {
                    exc.printStackTrace();
                    attachment.stop();
                }
            });
            countDownLatch.await();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void accept(AsynchronousSocketChannel socketChannel)
    {
        try
        {
            ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
            socketChannel.read(sizeBuffer).get();
            sizeBuffer.flip();
            int size = sizeBuffer.getInt();
            ByteBuffer dataBuffer = ByteBuffer.allocate(size);
            socketChannel.read(dataBuffer).get();
            dataBuffer.flip();
            byte[] dataBytes = new byte[size];
            dataBuffer.get(dataBytes);
            System.out.println(new String(dataBytes, "UTF-8"));
            byte[] outDataBytes = "This is AIOServer".getBytes("UTF-8");
            int outSize = outDataBytes.length;
            sizeBuffer.clear();
            sizeBuffer.putInt(outSize);
            sizeBuffer.flip();
            ByteBuffer outDataBuffer = ByteBuffer.allocate(outSize);
            outDataBuffer.put(outDataBytes);
            outDataBuffer.flip();
            socketChannel.write(sizeBuffer).get();
            socketChannel.write(outDataBuffer);
            socketChannel.close();
        }
        catch (IOException | InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void stop()
    {
        countDownLatch.countDown();
    }

    public static void main(String[] args)
    {
        TestAIOServer server = TestAIOServer.getInstance();
        server.start();
    }
}
