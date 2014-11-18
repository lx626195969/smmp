package com.ddk.smmp.channel.smgp.helper;

/**
 * 
 * @author leeson 2014-6-9 下午03:09:00 li_mr_ceo@163.com<br>
 *         SMGP全局常量类
 */
public class SmgpConstant {
	public static final byte TRANSMITTER = (byte) 0x00;

	public static int PDU_HEADER_SIZE = 12;

	public static final int CMD_LOGIN = 0x00000001;
	public static final int CMD_LOGIN_RESP = 0x80000001;

	public static final int CMD_SUBMIT = 0x00000002;
	public static final int CMD_SUBMIT_RESP = 0x80000002;

	public static final int CMD_DELIVER = 0x00000003;
	public static final int CMD_DELIVER_RESP = 0x80000003;

	public static final int CMD_ACTIVE_TEST = 0x00000004;
	public static final int CMD_ACTIVE_TEST_RESP = 0x80000004;

	public static final int CMD_EXIT = 0x00000006;
	public static final int CMD_EXIT_RESP = 0x80000006;

	// American Standard Code for Information Interchange
	public static final String ENC_ASCII = "ASCII";
	// Windows Latin-1
	public static final String ENC_CP1252 = "Cp1252";
	// ISO 8859-1, Latin alphabet No. 1
	public static final String ENC_ISO8859_1 = "ISO8859_1";
	// Sixteen-bit Unicode Transformation Format, big-endian byte order
	// with byte-order mark
	public static final String ENC_UTF16_BEM = "UnicodeBig";
	// Sixteen-bit Unicode Transformation Format, big-endian byte order
	public static final String ENC_UTF16_BE = "UnicodeBigUnmarked";
	// Sixteen-bit Unicode Transformation Format, little-endian byte order
	// with byte-order mark
	public static final String ENC_UTF16_LEM = "UnicodeLittle";
	// Sixteen-bit Unicode Transformation Format, little-endian byte order
	public static final String ENC_UTF16_LE = "UnicodeLittleUnmarked";
	// Eight-bit Unicode Transformation Format
	public static final String ENC_UTF8 = "UTF8";
	// Sixteen-bit Unicode Transformation Format, byte order specified by
	// a mandatory initial byte-order mark
	public static final String ENC_UTF16 = "UTF-16";
}
