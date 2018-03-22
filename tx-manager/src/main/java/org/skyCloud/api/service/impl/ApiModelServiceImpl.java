package org.skyCloud.api.service.impl;

import org.skyCloud.api.service.ApiModelService;
import org.skyCloud.manager.ModelInfoManager;
import org.skyCloud.model.ModelInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * create by lorne on 2017/11/13
 */
@Service
public class ApiModelServiceImpl implements ApiModelService {


    @Override
    public List<ModelInfo> onlines() {
        return ModelInfoManager.getInstance().getOnlines();
    }


}
