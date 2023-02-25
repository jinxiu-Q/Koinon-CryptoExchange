package com.bizzan.bitrade.util;

import com.bizzan.bitrade.client.Client;
import com.bizzan.bitrade.socket.ws.WebSocketHuobi;

public class WebSocketConnectionManage {

    private static Client client;
    public static WebSocketHuobi ws; // 价格监听websocket
    public static Client getClient() { return client; }
    public static void setClient(Client client) {
        WebSocketConnectionManage.client = client;
    }

    public static WebSocketHuobi getWebSocket() { return ws; }
    public static void setWebSocket(WebSocketHuobi ws) { WebSocketConnectionManage.ws = ws; }
}
