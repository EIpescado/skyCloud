package org.skyCloud.manager.service;

import org.skyCloud.model.LoadBalanceInfo;

/**
 * create by lorne on 2017/12/5
 */
public interface LoadBalanceService {

    boolean put(LoadBalanceInfo loadBalanceInfo);

    LoadBalanceInfo get(String groupId, String key);

    boolean remove(String groupId);

    String getLoadBalanceGroupName(String groupId);

}
