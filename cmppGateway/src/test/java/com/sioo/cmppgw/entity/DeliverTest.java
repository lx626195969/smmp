package com.sioo.cmppgw.entity;

import static junit.framework.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

/**
 * Title ：
 * Description :
 * Create Time: 14-4-15 下午4:50
 */
public class DeliverTest {
    public Random r = new Random();


    @Test
    public void testRptCmpp3() {
        Deliver d = new Deliver(Constants.PROTOCALTYPE_VERSION_CMPP3);
        processCommon(d);

        processCommonRpt(d);

        r.nextBytes(d.getSrcTerminalId());
        d.setSrcTerminalType((byte) 4);

        r.nextBytes(d.getDestTerminalId());
        r.nextBytes(d.getReservedOrLinkId());
        Deliver d1 = new Deliver(Constants.PROTOCALTYPE_VERSION_CMPP3);
        d1.doDecode(d.doEncode());
        assertEquals(d, d1);
        Deliver d2 = d1.clone();
        assertEquals(d2, d1);
    }

    @Test
    public void testRptCmpp2() {
        Deliver d = new Deliver(Constants.PROTOCALTYPE_VERSION_CMPP2);
        processCommon(d);

        processCommonRpt(d);
        r.nextBytes(d.getSrcTerminalId());
        r.nextBytes(d.getDestTerminalId());
        r.nextBytes(d.getReservedOrLinkId());
        Deliver d1 = new Deliver(Constants.PROTOCALTYPE_VERSION_CMPP2);
        d1.doDecode(d.doEncode());
        assertEquals(d, d1);
        Deliver d2 = d1.clone();
        assertEquals(d2, d1);
    }

    @Test
    public void testMOCmpp3() {
        Deliver d = new Deliver(Constants.PROTOCALTYPE_VERSION_CMPP3);
        processCommon(d);
        r.nextBytes(d.getSrcTerminalId());
        d.setSrcTerminalType((byte) 4);
        r.nextBytes(d.getReservedOrLinkId());

        d.setMsgContent(new byte[128]);
        r.nextBytes(d.getMsgContent());
        d.setRegisteredDelivery((byte) 0);
        Deliver d1 = new Deliver(Constants.PROTOCALTYPE_VERSION_CMPP3);
        d1.doDecode(d.doEncode());
        assertEquals(d, d1);
        Deliver d2 = d1.clone();
        assertEquals(d2, d1);
    }

    @Test
    public void testMOCmpp2() {
        Deliver d = new Deliver(Constants.PROTOCALTYPE_VERSION_CMPP2);
        processCommon(d);
        r.nextBytes(d.getSrcTerminalId());
        r.nextBytes(d.getReservedOrLinkId());
        d.setMsgContent(new byte[160]);
        r.nextBytes(d.getMsgContent());
        d.setRegisteredDelivery((byte) 0);
        Deliver d1 = new Deliver(Constants.PROTOCALTYPE_VERSION_CMPP2);
        d1.doDecode(d.doEncode());
        assertEquals(d, d1);
        Deliver d2 = d1.clone();
        assertEquals(d2, d1);
    }

    private void processCommonRpt(Deliver d) {
        r.nextBytes(d.getMsg_Id());
        r.nextBytes(d.getStat());
        r.nextBytes(d.getSubmitTime());
        r.nextBytes(d.getDoneTime());
        r.nextBytes(d.getSmscSequence());
        d.setRegisteredDelivery((byte) 1);
    }

    private void processCommon(Deliver d) {
        r.nextBytes(d.getMsgId());
        r.nextBytes(d.getDestId());
        r.nextBytes(d.getServiceId());
        d.setTpPid((byte) 1);
        d.setTpUdhi((byte) 2);
        d.setMsgFmt((byte) 3);
    }
}
