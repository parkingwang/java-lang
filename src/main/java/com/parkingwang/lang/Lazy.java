package com.parkingwang.lang;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 延迟器加载实现
 * @author Yoojia Chen (yoojiachen@gmail.com)
 * @since 1.0.1
 */
public class Lazy<T>{

    private final Supplier<T> mSupplier;
    private final AtomicReference<T> mValue = new AtomicReference<>(null);

    public Lazy(Supplier<T> supplier) {
        this.mSupplier = supplier;
    }

    @NotNull
    public T get(){
        final T cached = mValue.get();
        if (cached == null) {
            final T newObj = mSupplier.call();
            if (mValue.compareAndSet(null, newObj)) {
                return newObj;
            }else{
                return mValue.get();
            }
        }else{
            return cached;
        }
    }

    /**
     * @return 返回是否已设置值
     * @since 2.3.2
     */
    public boolean isSet(){
        return null != mValue.get();
    }

    /**
     * 移除已加载的值
     */
    public void remove(){
        mValue.set(null);
    }

    public static <T> Lazy<T> from(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }
}
