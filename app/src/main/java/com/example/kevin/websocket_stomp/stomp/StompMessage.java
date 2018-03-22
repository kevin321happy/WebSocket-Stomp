package com.example.kevin.websocket_stomp.stomp;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by naik on 05.05.16.
 * Stomp协议传输需要是数据格式
 */
public class StompMessage {
    /**
     * 默认的结束标示
     */
    public static final String TERMINATE_MESSAGE_SYMBOL = "\\u0000";

    private static final Pattern PATTERN_HEADER = Pattern.compile("([^:\\s]+)\\s*:\\s*([^:\\s]+)");
    /**
     * 命令类型
     */
    private final String mStompCommand;
    /**
     * Heard头的参数的集合
     */
    private final List<StompHeader> mStompHeaders;
    /**
     * 发送的body的内容
     */
    private final String mPayload;

    /**
     * 构造函数
     * @param stompCommand  指定的命令
     * @param stompHeaders  heard头的参数
     * @param payload       发送的body内容
     */
    public StompMessage(String stompCommand, List<StompHeader> stompHeaders, String payload) {
        mStompCommand = stompCommand;
        mStompHeaders = stompHeaders;
        mPayload = payload;
    }

    public List<StompHeader> getStompHeaders() {
        return mStompHeaders;
    }

    public String getPayload() {
        return mPayload;
    }

    public String getStompCommand() {
        return mStompCommand;
    }

    /**
     * 根据key值得到heard头里面的参数值
     * @param key
     * @return
     */
    public String findHeader(String key) {
        if (mStompHeaders == null) return null;
        for (StompHeader header : mStompHeaders) {
            if (header.getKey().equals(key)) return header.getValue();
        }
        return null;
    }
    /**
     * 将heard头的参数拼接成后台需要的帧格式
     * @return
     */
    public String compile() {
        StringBuilder builder = new StringBuilder();
        builder.append("[\"");
        builder.append(mStompCommand).append("\\n");
        for (StompHeader header : mStompHeaders) {
            builder.append(header.getKey()).append(':').append(header.getValue()).append("\\n");
        }
        builder.append("\\n");
        builder.append(TERMINATE_MESSAGE_SYMBOL);
        builder.append("\"]");
        if (mPayload != null) {
            builder.append(mPayload);
        }
//        String s = builder.toString();
        return builder.toString();
    }
    public static StompMessage from(String data) {
        if (data == null || data.trim().isEmpty()) {
            return new StompMessage(StompCommand.UNKNOWN, null, data);
        }
        Scanner reader = new Scanner(new StringReader(data));
        reader.useDelimiter("\\n");
        String command = reader.next();
        List<StompHeader> headers = new ArrayList<>();
        while (reader.hasNext(PATTERN_HEADER)) {
            Matcher matcher = PATTERN_HEADER.matcher(reader.next());
            matcher.find();
            headers.add(new StompHeader(matcher.group(1), matcher.group(2)));
        }
        reader.skip("\\s");

        reader.useDelimiter(TERMINATE_MESSAGE_SYMBOL);
        String payload = reader.hasNext() ? reader.next() : null;
        return new StompMessage(command, headers, payload);
    }
}
