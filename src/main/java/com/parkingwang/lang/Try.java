package com.parkingwang.lang;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yoojia Chen (yoojiachen@gmail.com)
 * @since 1.0.5
 */
final public class Try {

    private Try(){}

    public static void close(final AutoCloseable closeable){
        ignore(new ActionThrow.Action() {
            @Override public void call() throws Throwable {
                if (closeable != null) closeable.close();
            }
        });
    }

    public static void ignore(ActionThrow action){
        try{
            action.invoke();
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    public static <T> T die(SupplierThrow<T> supplier){
        try{
            return supplier.call();
        }catch (Throwable t){
            t.printStackTrace();
            System.exit(-1);
            throw new IllegalStateException("User manual shutdown the JVM on errors", t);
        }
    }

    public static void retry(int maxRetries, RetryAction action, int sleepAtNext) {
        final AtomicInteger retries = new AtomicInteger(0);
        boolean failed = true;
        while (retries.get() < maxRetries){
            try {
                action.onWork();
                failed = false;
                break;
            } catch (Throwable err) {
                final int next = retries.incrementAndGet();
                action.onThrows(err, next);
                final int sleep = next * sleepAtNext;
                if (retries.get() < maxRetries && sleep > 0) {
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException sleepErr) {
                        break;
                    }
                }
            }
        }
        if (failed){
            action.onFailed();
        }
    }

    public interface RetryAction{
        void onWork() throws Throwable;
        void onThrows(Throwable err, int retry);
        void onFailed();
    }

    public static abstract class JustRetryAction implements RetryAction{
        @Override
        public void onFailed() {}
    }

    public static abstract class ExitRetryAction implements RetryAction{
        @Override
        public void onFailed() { System.exit(-1); }
    }
}
