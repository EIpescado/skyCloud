package org.skyCloud.common.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by yq on 2017/01/10 15:10.
 * 中国银行外汇汇率
 */
public class BankOfChinaExchangeRate{

    /**
     * 货币名称 中文简体
     */
    private String currency;

    private String currencyCode;

    private String flag ; //是否已经抓取唯一标识 pulishTime + currency

    /**
     * 现汇买入价
     */
    private BigDecimal buyingRate;

    /**
     * 现钞买入价
     */
    private BigDecimal cashBuyingRate;

    /**
     * 现汇卖出价
     */
    private BigDecimal sellingRate;

    /**
     * 现钞卖出价
     */
    private BigDecimal cashSellingRate;

    /**
     * 外管局中间价
     */
    private BigDecimal safeMiddleRate;

    /**
     * 中行折算价
     */
    private BigDecimal bankConvertRate;

    /**
     * 发布时间
     */
    private Date publishTime;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getBuyingRate() {
        return buyingRate;
    }

    public void setBuyingRate(BigDecimal buyingRate) {
        this.buyingRate = buyingRate;
    }

    public BigDecimal getCashBuyingRate() {
        return cashBuyingRate;
    }

    public void setCashBuyingRate(BigDecimal cashBuyingRate) {
        this.cashBuyingRate = cashBuyingRate;
    }

    public BigDecimal getSellingRate() {
        return sellingRate;
    }

    public void setSellingRate(BigDecimal sellingRate) {
        this.sellingRate = sellingRate;
    }

    public BigDecimal getCashSellingRate() {
        return cashSellingRate;
    }

    public void setCashSellingRate(BigDecimal cashSellingRate) {
        this.cashSellingRate = cashSellingRate;
    }

    public BigDecimal getSafeMiddleRate() {
        return safeMiddleRate;
    }

    public void setSafeMiddleRate(BigDecimal safeMiddleRate) {
        this.safeMiddleRate = safeMiddleRate;
    }

    public BigDecimal getBankConvertRate() {
        return bankConvertRate;
    }

    public void setBankConvertRate(BigDecimal bankConvertRate) {
        this.bankConvertRate = bankConvertRate;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date pulishTime) {
        this.publishTime = pulishTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return super.toString() +  "BankOfChinaExchangeRate{" +
                "currency='" + currency + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", flag='" + flag + '\'' +
                ", buyingRate=" + buyingRate +
                ", cashBuyingRate=" + cashBuyingRate +
                ", sellingRate=" + sellingRate +
                ", cashSellingRate=" + cashSellingRate +
                ", safeMiddleRate=" + safeMiddleRate +
                ", bankConvertRate=" + bankConvertRate +
                ", pulishTime=" + publishTime +
                '}';
    }
}
