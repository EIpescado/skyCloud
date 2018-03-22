package org.skyCloud.netty.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.skyCloud.manager.service.TxManagerService;
import org.skyCloud.netty.service.IActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 关闭事务组
 * create by lorne on 2017/11/11
 */
@Service(value = "ctg")
public class ActionCTGServiceImpl implements IActionService{


    @Autowired
    private TxManagerService txManagerService;

    @Override
    public String execute(String channelAddress, String key, JSONObject params ) {
        String groupId = params.getString("g");
        int state = params.getInteger("s");
        String res = String.valueOf(txManagerService.closeTransactionGroup(groupId,state));
        return res;
    }
}
