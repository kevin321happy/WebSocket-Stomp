package com.example.kevin.websocket_stomp.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.kevin.websocket_stomp.stomp.StompCommand;
import com.example.kevin.websocket_stomp.stomp.StompHeader;
import com.example.kevin.websocket_stomp.stomp.StompMessage;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by kevin on 2018/3/26.
 * Sorcket连接的服务
 */

public class SocketConnectionService extends Service {

    private WebSocketClient mWebSocketClient;
    private Thread mSendHeartBeat;
    private Thread mSendPing;
    private boolean flag = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * 无论start多少次,只会调用一次
     */
    @Override
    public void onCreate() {
        super.onCreate();
        flag=true;
        //创建Sorcket对象
        try {
            URI uri = new URI("http://192.168.0.202:8201/im-ws/469/101346jpbefgloia3232lnefhgrkbskijz/websocket");
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("Socket", "socket连接打开");
                    //发送Connect命令
                    sendConnect();
                }

                @Override
                public void onMessage(String message) {
                    Log.d("Socket", "收到消息了:" + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("Socket", "socket关闭了");

                }

                @Override
                public void onError(Exception ex) {
                    Log.d("Socket", "socket连接失败了");

                }
            };
            //打开Socket通道
            mWebSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送Connect命令
     */
    private void sendConnect() {
        ArrayList<StompHeader> stompHeaders = new ArrayList<>();
        //TODO 这里的passcode和login是测试的Screct和Token实际中根据用户登陆的那个来拿
        stompHeaders.add(new StompHeader("accept-version", "1.1,1.0"));
        stompHeaders.add(new StompHeader("heart-beat", "30000,30000"));
        //secret
        stompHeaders.add(new StompHeader("passcode", "334da134ae1a4b7c9055ef53001666fa1519729392"));
        //token
        stompHeaders.add(new StompHeader("login", "d047c45434e943f296e8e833c476e5a518025811"));
        StompMessage stompMessage = new StompMessage(StompCommand.CONNECT, stompHeaders, null);
        String compile = stompMessage.compile();
        //发送了Connect
        mWebSocketClient.send(compile);
        //发送订阅
        sendSubscribe();
    }
    /**
     * 发送订阅
     */
    private void sendSubscribe() {
        if (mWebSocketClient != null) {
            String meaage = "[\"SEND\\ndestination:/message/chat\\ncontent-length:76\\n\\n{\\\"callId\\\":\\\"43401\\\",\\\"receiver\\\":\\\"person:100287\\\",\\\"content\\\":\\\"今天天气不错\\\"}\\u0000\"]";
            mWebSocketClient.send(meaage);
            Log.d("Socket", "发送了订阅");
        }
        //轮询发送心跳
        mSendHeartBeat = new SendHeartBeatThread();
        //开启线程发送心跳
        mSendHeartBeat.start();
        //轮询发送PING
        mSendPing=new SendPingThread();
        mSendPing.start();
    }

    /**
     * 服务开启时
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 回多次调用
     *
     * @param intent
     * @param startId
     */
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    /**
     * 轮询发送PING
     */
    private class SendPingThread extends Thread {
        @Override
        public void run() {
            while (flag) {
                try {
                    Thread.sleep(30000);
                    //发送PING
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
                    Log.d("TAG", "发送了PING");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 轮询发送心跳的线程
     */
    private class SendHeartBeatThread extends Thread {
        @Override
        public void run() {
            while (flag) {
                try {
                    Thread.sleep(30000);
                    //每隔10S发送一次心跳
                    if (mWebSocketClient != null) {
                        mWebSocketClient.send("[\"\\n\"]");
                        Log.d("TAG", "发送了💗");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 服务销毁断开socket连接
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebSocketClient!=null){
            mWebSocketClient.close();
            flag=false;
        }
    }
}
