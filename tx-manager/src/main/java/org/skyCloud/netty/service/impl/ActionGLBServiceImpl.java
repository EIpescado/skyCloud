package org.skyCloud.netty.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.skyCloud.manager.service.LoadBalanceService;
import org.skyCloud.model.LoadBalanceInfo;
import org.skyCloud.netty.service.IActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 获取负载模块信息
 * create by lorne on 2017/11/11
 */
@Service(value = "glb")
public class ActionGLBServiceImpl implements IActionService{


    @Autowired
    private LoadBalanceService loadBalanceService;


    @Override
    public String execute(String channelAddress, String key, JSONObject params ) {
        String res;
        String groupId = params.getString("g");
        String k = params.getString("k");

        LoadBalanceInfo loadBalanceInfo =  loadBalanceService.get(groupId,k);
        if(loadBalanceInfo==null){
            res = "";
        }else{
            res = loadBalanceInfo.getData();
        }
        return res;
    }
}
