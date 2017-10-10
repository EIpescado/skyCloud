package org.skyCloud.web;

import com.alibaba.fastjson.JSONObject;
import org.skyCloud.common.dataWrapper.Result;
import org.skyCloud.common.utils.StringUtils;
import org.skyCloud.config.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yq on 2017/10/10 15:35.
 * WebSocketController
 */
@RestController
@RequestMapping("/webSocket")
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private WebSocket webSocket;

    //支持的消息类型
    private List<String> SUPPORT_CONTENT_TYPE = new ArrayList<String>(){
        {
            add("html");
            add("image");
            add("text");
        }
    } ;

    /**
     * 群发消息
     */
    @PostMapping("massPush")
    public Result massPush(@RequestBody JSONObject json){
        Map<String,Session> webSocketMap = webSocket.getWebSocketMap();
        if(webSocketMap != null && !webSocketMap.isEmpty() && !json.isEmpty()){
            logger.info("准备群发消息...当前连接用户:{}",webSocketMap.size());
            String msg = judgeContent(json);
            if(StringUtils.isNotEmpty(msg)){
                return new Result(msg);
            }
            int i = 0;
            for(Map.Entry<String,Session> entry : webSocketMap.entrySet()){
                  Session session = entry.getValue();
                  if(send(session,entry.getKey(),json,webSocketMap)){
                      i ++ ;
                  }
            }
            logger.info("群发完毕..成功发送条数:{}",i);
            return  new Result();
        }else {
            return  new Result("消息发送异常");
        }
    }

    /**
     * 发送给指定用户
     */
    @PostMapping("/pushAssignUser/{userId}")
    public Result pushAssignUser(@PathVariable(name = "userId")String userId, @RequestBody JSONObject json){
        Map<String,Session> webSocketMap = webSocket.getWebSocketMap();
        if(webSocketMap != null && !webSocketMap.isEmpty() && !json.isEmpty()){
            logger.info("开始发送给{}",userId);
            String msg = judgeContent(json);
            if(StringUtils.isNotEmpty(msg)){
                return new Result(msg);
            }
            Session session = webSocketMap.get(userId);
            boolean success = send(session,userId,json,webSocketMap);
            logger.info("发送完毕..是否成功发送:{}",success);
        }else {
           return new Result("消息发送异常");
        }
        return new Result();
    }

    //判断发送消息
    private String judgeContent(JSONObject json){
        String contentType = json.getString("contentType");//正文类型 html,image,text 前端处理方式有差异
        String content = json.getString("content");//正文
        logger.info("正文类型 {}",contentType);
        if(StringUtils.isEmpty(content)){
            return "发送内容不存在";
        }
        if(!SUPPORT_CONTENT_TYPE.contains(contentType)){
            return "不支持的消息类型";
        }
        return null;
    }

    //发送消息
    private boolean send(Session session,String userId,JSONObject json, Map<String,Session> webSocketMap){
        if(session != null){
            //用户保持连接中
            if(session.isOpen()){
                try {
                    session.getBasicRemote().sendText(json.toString());
                    return true;
                } catch (IOException e) {
                    logger.error("消息发送失败,用户ID:{}",userId);
                    webSocketMap.remove(userId);
                    return false;
                }
            }else {
                webSocketMap.remove(userId);
                try {
                    session.close();
                } catch (IOException e) {
                    logger.error("关闭连接失败,用户ID:{}",userId);
                }
                return false;
            }
        }else {
            logger.info("用户 {} 会话已经关闭",userId);
            return false;
        }
    }
}
