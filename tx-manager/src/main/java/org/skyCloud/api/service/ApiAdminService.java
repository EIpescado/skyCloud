package org.skyCloud.api.service;

import com.lorne.core.framework.exception.ServiceException;
import org.skyCloud.compensate.model.TxModel;
import org.skyCloud.model.ModelName;
import org.skyCloud.model.TxState;

import java.util.List;

/**
 * create by lorne on 2017/11/12
 */
public interface ApiAdminService {

    TxState getState();

    String loadNotifyJson();

    List<ModelName> modelList();


    List<String> modelTimes(String model);

    List<TxModel> modelInfos(String path);

    boolean compensate(String path) throws ServiceException;

    boolean hasCompensate();

    boolean delCompensate(String path);

}
