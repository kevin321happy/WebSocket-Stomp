package com.example.kevin.websocket_stomp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.kevin.websocket_stomp.socket.SocketConnectionService;
import com.example.kevin.websocket_stomp.stomp.StompCommand;
import com.example.kevin.websocket_stomp.stomp.StompHeader;
import com.example.kevin.websocket_stomp.stomp.StompMessage;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private WebSocketClient mWebSocketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 1、开始建立连接
     * @param view
     */
    public void connectStomp(View view) throws URISyntaxException {
        //开启Sokcet服务
        startSocketService();
        Log.d("TAG","点击了开始连接");
//        URI uri = new URI("ws://api.qiansquare.com/im-ws/469/101346jpbefgloia3232lnefhgrkbskijz/websocket");
//        URI uri = new URI("http://192.168.2.150:8201/im-ws/469/101346jpbefgloia3232lnefhgrkbskijz/websocket");
//        mWebSocketClient = new WebSocketClient(uri) {
//            @Override
//            public void onOpen(ServerHandshake handshakedata) {
//                Log.d("TAG","连接打开");
//            }
//
//            @Override
//            public void onMessage(String message) {
//                Log.d("TAG","收到消息:"+message);
//            }
//
//            @Override
//            public void onClose(int code, String reason, boolean remote) {
//                Log.d("TAG","连接关闭:"+reason+"错误码："+code);
//
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                Log.d("TAG","连接出错");
//
//            }
//            /**
//             * 发送Ping给服务端
//             * @param conn
//             * @param f
//             */
//            @Override
//            public void onWebsocketPing(WebSocket conn, Framedata f) {
//                super.onWebsocketPing(conn, f);
//                Log.d("TAG","发送了PING");
//            }
//            /**
//             * 接受服务端响应的Pong
//             * @param conn
//             * @param f
//             */
//            @Override
//            public void onWebsocketPong(WebSocket conn, Framedata f) {
//                super.onWebsocketPong(conn, f);
//                Log.d("TAG","收到了PONG");
//            }
//        };
//        mWebSocketClient.connect();
    }

    /**
     * 开启Socket服务
     */
    private void startSocketService() {
        //启动Socket服务
        startService(new Intent(this, SocketConnectionService.class));
    }

    /**
     * 断开连接
     * @param view
     */
    public void disconnectStomp(View view) {
        //关闭
        mWebSocketClient.close();
    }

    /**
     * 2、发送COnnect命令
     * @param view
     */
    public void sendConnect(View view) {
        ArrayList<StompHeader> stompHeaders = new ArrayList<>();
        stompHeaders.add(new StompHeader("accept-version", "1.1,1.0"));
        stompHeaders.add(new StompHeader("heart-beat", "30000,30000"));
        //secret
        stompHeaders.add(new StompHeader("passcode", "334da134ae1a4b7c9055ef53001666fa1519729392"));
        //token
        stompHeaders.add(new StompHeader("login", "d047c45434e943f296e8e833c476e5a518025811"));
        StompMessage stompMessage = new StompMessage(StompCommand.CONNECT, stompHeaders, null);
        String compile = stompMessage.compile();
        mWebSocketClient.send(compile);
    }
    /**
     * 3、发送心跳包
     * @param view
     */
    public void sendBeat(View view) {
        StringBuffer heart = new StringBuffer();
        heart.append("[");
        heart.append("\\");
        heart.append("\"");
        heart.append("\\n\\\"");
//       append heart.append()
        heart.append("]");
        String beatHeart = heart.toString();
        //发送心跳
        mWebSocketClient.send("[\"\\n\"]");
        Log.d("TAG","发送了💗");
    }
    /**
     * TODO
     * 发送订阅命令
     */
    /**
     * 发送Ping
     * @param view
     */
    public void sendPing(View view) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap("Hello".getBytes());
            FramedataImpl1 resp = new FramedataImpl1(Framedata.Opcode.PING) {
                @Override
                public void isValid() throws InvalidDataException {
                }
            };
            resp.setFin(true);
            resp.setPayload(buffer);
            //发送Ping
            mWebSocketClient.getConnection().sendFrame(resp);
            Log.d("TAG","发送了PING");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 4、开始订阅
     * 订阅两条
     * @param view
     */
    public void Startsubscribe(View view) {

    }
    /**
     * 发送消息
     * @param view
     */
    public void sendMessage(View view) {
        if (mWebSocketClient!=null){
            String meaage="[\"SEND\\ndestination:/message/chat\\ncontent-length:76\\n\\n{\\\"callId\\\":\\\"43401\\\",\\\"receiver\\\":\\\"person:100287\\\",\\\"content\\\":\\\"今天天气不错\\\"}\\u0000\"]";
            mWebSocketClient.send(meaage);
        }
    }
    /**
     *
     */
}
