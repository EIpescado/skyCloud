package org.skyCloud.lcn;

import com.codingapi.tx.config.service.TxManagerTxUrlService;
import org.skyCloud.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yq
 * @date 2018/03/20 11:38
 * @description
 * @since V1.0.0
 */
@Service
public class ITxManagerTxUrlService implements TxManagerTxUrlService {

    private final Logger logger = LoggerFactory.getLogger(ITxManagerTxUrlService.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    private static final String TX_MANAGER_URL_SUF = "/tx/manager/";
    private static final String TX_MANAGER_URL_PRE = "http://";

    @Override
    public String getTxUrl() {
        List<ServiceInstance> list = discoveryClient.getInstances("tx-manager");
        String url = null;
        if(CollectionUtils.isNotEmpty(list)){
            ServiceInstance instance = list.get(0);
            EurekaDiscoveryClient.EurekaServiceInstance eurekaServiceInstance = (EurekaDiscoveryClient.EurekaServiceInstance) instance;
            String ip = eurekaServiceInstance.getInstanceInfo().getIPAddr();
            Integer port = instance.getPort();
            url = TX_MANAGER_URL_PRE + ip + ":" + port + TX_MANAGER_URL_SUF;
        }
        logger.info("tx-manager地址:{}",url);
        return url;
    }
}
