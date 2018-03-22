package org.skyCloud.compensate.service;

import com.lorne.core.framework.exception.ServiceException;
import org.skyCloud.compensate.model.TransactionCompensateMsg;
import org.skyCloud.compensate.model.TxModel;
import org.skyCloud.model.ModelName;
import org.skyCloud.netty.model.TxGroup;

import java.util.List;

/**
 * create by lorne on 2017/11/11
 */
public interface CompensateService {

    boolean saveCompensateMsg(TransactionCompensateMsg transactionCompensateMsg);

    List<ModelName> loadModelList();

    List<String> loadCompensateTimes(String model);

    List<TxModel> loadCompensateByModelAndTime(String path);

    void autoCompensate(String compensateKey, TransactionCompensateMsg transactionCompensateMsg);

    boolean executeCompensate(String key) throws ServiceException;

    void reloadCompensate(TxGroup txGroup);

    boolean hasCompensate();

    boolean delCompensate(String path);

    TxGroup  getCompensateByGroupId(String groupId);
}
