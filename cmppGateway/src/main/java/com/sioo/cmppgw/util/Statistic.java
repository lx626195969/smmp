package com.sioo.cmppgw.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leeson 2014年8月22日 上午9:21:36 li_mr_ceo@163.com <br>
 *
 */
public class Statistic {
    private final static Logger logger = LoggerFactory.getLogger((Statistic.class).getSimpleName());

    private static AtomicInteger submitCount = new AtomicInteger(0);
    private static AtomicInteger deliverCount = new AtomicInteger(0);
    private int lastSbumitCount = 0;
    private int lastDeliverCount = 0;

    public static void addSubmit(Integer num) {
        submitCount.addAndGet(num);
    }

    public static void addSubmit() {
        addSubmit(1);
    }

    public static void addDeliver(Integer num) {
        deliverCount.addAndGet(num);
    }

    public static void addDeliver() {
        addDeliver(1);
    }

    public void logOutSpeed() {
        int temp = submitCount.get();
        logger.info("Submit total:[{}],Speed:[{}/s]", temp, temp - lastSbumitCount);
        lastSbumitCount = temp;
        temp = deliverCount.get();
        logger.info("Deliver total:[{}],Speed:[{}/s]", temp, temp - lastDeliverCount);
        lastDeliverCount = temp;
    }
}