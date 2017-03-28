/*
 * Copyright (c) 2017 coodex.org (jujus.shen@126.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.coodex.concrete.common;

import org.coodex.concurrent.ExecutorsHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by davidoff shen on 2017-03-24.
 */
public abstract class ConcreteCache<K, V> {

    static class Cached<V> {
        V value;
        ScheduledFuture future;

        public Cached(V value, ScheduledFuture future) {
            this.value = value;
            this.future = future;
        }
    }

    private final static ScheduledExecutorService THREADS = ExecutorsHelper.newScheduledThreadPool(
            ConcreteHelper.getProfile().getInt("cache.thread.pool.size", 1)
    );


    private Map<K, Cached<V>> cache = new HashMap<K, Cached<V>>();

    private int time;
    private TimeUnit unit;

    public ConcreteCache() {
        this(ConcreteHelper.getProfile().getInt("cache.object.life", 10), TimeUnit.MINUTES);
    }

    /**
     * time个unit单位后移除缓存
     *
     * @param time
     * @param unit
     */
    public ConcreteCache(int time, TimeUnit unit) {
        this.time = time;
        this.unit = unit;
    }

    public V get(final K key) {
        synchronized (cache) {
            if (!cache.containsKey(key)) {
                cache.put(key, new Cached<V>(load(key),
                        THREADS.schedule(new Runnable() {
                            @Override
                            public void run() {
                                cache.remove(key);
                            }
                        }, time, unit)));
            }
        }
        Cached<V> cached = cache.get(key);
        return cached == null ? null : cached.value;
    }

    /**
     * 使指定的缓存对象失效
     *
     * @param key
     */
    public void invalidate(K key) {
        Cached<V> cached = cache.remove(key);
        if (cached != null) {
            cached.future.cancel(false);
        }
    }

    protected abstract V load(K key);
}