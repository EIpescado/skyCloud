package org.skyCloud.manager.service;

import org.skyCloud.model.TxServer;
import org.skyCloud.model.TxState;

/**
 * create by lorne on 2017/11/11
 */
public interface MicroService {

    String  tmKey = "tx-manager";

    TxServer getServer();

    TxState getState();
}
