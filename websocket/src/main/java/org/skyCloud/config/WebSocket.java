package org.skyCloud.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by yq on 2017/09/18 17:50.
 * 建立了一个WebSocket的服务端
 * WebSocket服务端运行在ws://[Server端IP或域名]:[Server端口]/项目/push的访问端点，
 * 客户端浏览器已经可以对WebSocket客户端API发起HTTP长连接了。
 */
@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocket {

    private static final Logger logger = LoggerFactory.getLogger(WebSocket.class);

    //存放每个用户对应连接会话
    private static ConcurrentHashMap<String,Session> webSocketMap = new ConcurrentHashMap<>();

    public Map<String,Session> getWebSocketMap(){
        return webSocketMap;
    }

    @OnOpen
    public void onOpen (@PathParam("userId") String userId, Session session){
        webSocketMap.put(userId,session);
        logger.info(userId + "加入连接...,当前连接总数 {}",webSocketMap.size());
    }

    @OnClose
    public void onClose (@PathParam("userId") String userId, Session session, CloseReason reason){
        webSocketMap.remove(userId);
        logger.info("session {} 关闭,原因 {}",session.getId(),reason.getCloseCode());
    }

    @OnMessage
    public void onMessage (String message, Session session) throws IOException {
        logger.info("来自客户端的消息:" + message);
    }
}
