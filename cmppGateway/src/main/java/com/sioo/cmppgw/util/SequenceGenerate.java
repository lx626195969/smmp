package com.sioo.cmppgw.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author leeson 2014年8月22日 上午9:22:08 li_mr_ceo@163.com <br>
 *
 */
public class SequenceGenerate {
    private static final AtomicInteger sequenceId = new AtomicInteger((int) (System.currentTimeMillis() % 10000000 * 100));

    /**
     * 生成短信唯一标识
     * 
     * @return
     */
    public synchronized static int getSequenceId() {
        if (sequenceId.get() == Integer.MAX_VALUE) {
            sequenceId.set(1);
        }else {
            sequenceId.incrementAndGet();
        }
        return sequenceId.get();
    }
}


