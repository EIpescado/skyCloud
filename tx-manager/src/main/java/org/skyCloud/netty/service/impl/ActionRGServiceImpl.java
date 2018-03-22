package org.skyCloud.netty.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.skyCloud.manager.service.TxManagerService;
import org.skyCloud.netty.service.IActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 强制回滚事务组
 * create by lorne on 2017/11/11
 */
@Service(value = "rg")
public class ActionRGServiceImpl implements IActionService{


    @Autowired
    private TxManagerService txManagerService;

    @Override
    public String execute(String channelAddress, String key, JSONObject params ) {
        String res = "";
        String groupId = params.getString("g");
        boolean bs = txManagerService.rollbackTransactionGroup(groupId);
        res = bs ? "1" : "0";
        return res;
    }
}
