package com.sioo.cmppgw.entity;

import java.nio.ByteBuffer;

/**
 * 
 * @author leeson 2014年8月22日 上午9:18:33 li_mr_ceo@163.com <br>
 *
 */
public class ActiveTest extends CmppHead {
	private static final long serialVersionUID = 3307679196695805174L;

	public ActiveTest() {
        totalLength = 12;
        commandId = CMPPConstant.APP_ACTIVE_TEST;
    }

    @Override
    protected void doSubEncode(ByteBuffer bb) {

    }

    @Override
    protected void doSubDecode(ByteBuffer bb) {

    }

    @Override
    protected void processHead() {

    }
}
