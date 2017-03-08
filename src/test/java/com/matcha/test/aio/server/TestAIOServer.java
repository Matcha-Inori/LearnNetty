package com.matcha.test.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Matcha on 2017/3/8.
 */
public class TestAIOServer
{
    public static void main(String[] args)
    {
        CountDownLatch finish = new CountDownLatch(1);
        try(AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open())
        {
            serverSocketChannel.bind(new InetSocketAddress(8668));
            serverSocketChannel.accept(finish, new AcceptCompletionHandler());
            finish.await();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
