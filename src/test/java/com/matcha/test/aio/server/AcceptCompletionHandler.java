package com.matcha.test.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by Matcha on 2017/3/8.
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, CountDownLatch>
{
    @Override
    public void completed(AsynchronousSocketChannel result, CountDownLatch attachment)
    {

    }

    @Override
    public void failed(Throwable exc, CountDownLatch attachment)
    {

    }

    private void accept(AsynchronousSocketChannel socketChannel) throws ExecutionException, InterruptedException
    {
        ByteBuffer sizeByteBuffer = ByteBuffer.allocateDirect(4);
        socketChannel.read(sizeByteBuffer).get();
        sizeByteBuffer.flip();
        final int size = sizeByteBuffer.getInt();
        ByteBuffer dataByteBuffer = ByteBuffer.allocateDirect(size);
        socketChannel.read(dataByteBuffer).get();
        dataByteBuffer.flip();
        byte[] dataSize = new byte[size];
        dataByteBuffer.get(dataSize);
    }
}
