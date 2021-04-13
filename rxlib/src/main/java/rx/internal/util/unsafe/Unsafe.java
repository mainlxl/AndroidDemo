package rx.internal.util.unsafe;

import java.lang.reflect.Field;

import rx.internal.util.atomic.LinkedQueueNode;

/**
 * Created by Mainli on 2018-3-22.
 * 创建一个默认空实现 应使用标准API 避免使用Unsafe
 */
public class Unsafe {
    public int getIntVolatile(Object obj, long offset) {
        return 0;
    }

    public boolean compareAndSwapInt(Object obj, long offset, int current, int next) {
        return false;
    }

    public long objectFieldOffset(Field f) {
        return 0;
    }

    public int arrayIndexScale(Object aClass) {
        return 0;
    }

    public int arrayBaseOffset(Object aClass) {
        return 0;
    }

    public  void putObject(Object[] buffer, long offset, Object e) {

    }

    public  void putOrderedObject(Object[] buffer, long offset, Object e) {

    }

    public Object getObject(Object[] buffer, long offset) {
        return null;
    }

    public Object  getObjectVolatile(Object buffer, long offset) {
        return null;
    }

    public  void putOrderedLong(Object es, long pIndexOffset, long v) {

    }

    public  long getLongVolatile(Object es, long pIndexOffset) {
        return 0;
    }

    public  boolean compareAndSwapLong(Object es, long pIndexOffset, long expect, long newValue) {
        return false;
    }

    public  boolean compareAndSwapObject(Object es, long pNodeOffset, Object oldVal, LinkedQueueNode newVal) {
        return false;
    }
}
