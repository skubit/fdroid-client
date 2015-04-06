package com.skubit.market.provider.accounts;

import com.skubit.market.provider.base.BaseModel;

import java.util.Date;

/**
 * Data model for the {@code accounts} table.
 */
public interface AccountsModel extends BaseModel {

    /**
     * Get the {@code bitid} value.
     * Can be {@code null}.
     */
    String getBitid();

    /**
     * Get the {@code token} value.
     * Can be {@code null}.
     */
    String getToken();

    /**
     * Get the {@code date} value.
     * Can be {@code null}.
     */
    Long getDate();
}
