package com.skubit.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Date;

@JsonInclude(Include.NON_NULL)
public final class AppDto implements Dto {

    /**
     *
     */
    private static final long serialVersionUID = 4470786898069014384L;

    private String bitcoin;

    private String category;

    private String currencySymbol;

    private String description;

    private String displayName;

    private String donationWebsite;

    private String flattrID;

    private String iconUrl;

    private boolean isEnabled;

    private boolean isRoot;

    private String litecoin;

    private String nodeName;

    private String packageName;

    private double price;

    private Date publishDate;

    private long satoshi;

    private String summary;

    private Date updateDate;

    private String vendorName;

    private String versionName;

    private String website;

    private String productId;

    private int versionCode;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBitcoin() {
        return bitcoin;
    }

    public String getCategory() {
        return category;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDonationWebsite() {
        return donationWebsite;
    }

    public String getFlattrID() {
        return flattrID;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getLitecoin() {
        return litecoin;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getPackageName() {
        return packageName;
    }

    public double getPrice() {
        return price;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public long getSatoshi() {
        return satoshi;
    }

    public String getSummary() {
        return summary;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getWebsite() {
        return website;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setBitcoin(String bitcoin) {
        this.bitcoin = bitcoin;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDonationWebsite(String donationWebsite) {
        this.donationWebsite = donationWebsite;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void setFlattrID(String flattrID) {
        this.flattrID = flattrID;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setLitecoin(String litecoin) {
        this.litecoin = litecoin;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public void setSatoshi(long satoshi) {
        this.satoshi = satoshi;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

}
