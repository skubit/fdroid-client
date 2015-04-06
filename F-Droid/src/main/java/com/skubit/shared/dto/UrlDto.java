package com.skubit.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public final class UrlDto implements Dto {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String url;

    public UrlDto() {

    }

    public UrlDto(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UrlDto [url=" + url + "]";
    }
}
