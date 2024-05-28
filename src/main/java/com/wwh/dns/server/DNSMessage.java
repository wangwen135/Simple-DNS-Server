package com.wwh.dns.server;

import java.nio.ByteBuffer;

/**
 * DNSMessage from gpt
 *
 * @author wangwh
 * @date 2024/05/28
 */
public class DNSMessage {

    // Simplified DNS message parser
    private byte[] data;
    private String questionDomain;

    public static DNSMessage parse(byte[] data) {
        DNSMessage message = new DNSMessage();
        message.data = data;
        message.questionDomain = message.parseQuestionDomain();
        return message;
    }

    private String parseQuestionDomain() {
        StringBuilder domain = new StringBuilder();
        int position = 12; // Skip header
        while (data[position] != 0) {
            int length = data[position];
            for (int i = 1; i <= length; i++) {
                domain.append((char) data[position + i]);
            }
            domain.append('.');
            position += length + 1;
        }
        return domain.substring(0, domain.length() - 1); // Remove trailing dot
    }

    public String getQuestionDomain() {
        return questionDomain;
    }

    public byte[] buildResponse(String ipAddress) {
        ByteBuffer responseBuffer = ByteBuffer.allocate(512);

        // 复制请求报头（前12字节）
        responseBuffer.put(data, 0, 12);

        // 设置响应标志
        responseBuffer.put(2, (byte) 0x81); // 响应和递归可用
        responseBuffer.put(3, (byte) 0x80); // 无错误

        // 问题数
        responseBuffer.putShort(4, (short) 1); // 复制请求问题数

        // 回答数
        responseBuffer.putShort(6, (short) 1);

        // 权威记录数
        responseBuffer.putShort(8, (short) 0);

        // 附加记录数
        responseBuffer.putShort(10, (short) 0);

        // 复制查询问题部分（从报头之后到第一个0字节结束）
        int questionLength = 12;
        while (data[questionLength] != 0) {
            questionLength++;
        }
        questionLength += 5; // 包括末尾的0字节和查询类型与类字段
        responseBuffer.put(data, 12, questionLength - 12);

        // 回答部分
        responseBuffer.putShort((short) 0xC00C); // 查询名称的指针
        responseBuffer.putShort((short) 0x0001); // 类型A
        responseBuffer.putShort((short) 0x0001); // 类别IN
        responseBuffer.putInt(0x0000003C); // TTL 60秒
        responseBuffer.putShort((short) 0x0004); // 数据长度

        // IP地址
        String[] octets = ipAddress.split("\\.");
        for (String octet : octets) {
            responseBuffer.put((byte) Integer.parseInt(octet));
        }

        return responseBuffer.array();
    }
}
