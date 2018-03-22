package com.example.kevin.websocket_stomp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.kevin.websocket_stomp.stomp.StompCommand;
import com.example.kevin.websocket_stomp.stomp.StompHeader;
import com.example.kevin.websocket_stomp.stomp.StompMessage;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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
        URI uri = new URI("ws://api.qiansquare.com/im-ws/469/101346jpbefgloia3232lnefhgrkbskijz/websocket");
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("TAG","连接打开");

            }

            @Override
            public void onMessage(String message) {
                Log.d("TAG","收到消息:"+message);

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("TAG","连接关闭:"+reason+"错误码："+code);

            }

            @Override
            public void onError(Exception ex) {
                Log.d("TAG","连接出错");

            }
        };
        mWebSocketClient.connect();

    }

    /**
     * 断开连接
     * @param view
     */
    public void disconnectStomp(View view) {


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
        stompHeaders.add(new StompHeader("passcode", "dabfd6cfd8f94fd89b23eb6e2bd7d7af1521684997"));
        //token
        stompHeaders.add(new StompHeader("login", "458ea8d6138b42009e5e0619f7751b3d18038102"));
        StompMessage stompMessage = new StompMessage(StompCommand.CONNECT, stompHeaders, null);
        String compile = stompMessage.compile();
        mWebSocketClient.send(compile);

    }



    /**
     * 3、发送心跳
     * @param view
     */
    public void sendBeat(View view) {

    }
    /**
     * TODO
     * 发送订阅命令
     */
}
