package com.skubit.shared.dto;

import java.util.ArrayList;

public abstract class DtoList<T> implements Dto {

    /**
     *
     */
    private static final long serialVersionUID = -3982851172980416150L;

    private ArrayList<T> items;

    private String nextLink;

    private String previousLink;

    private String nextWebCursor;

    private int currentItemCount;

    public void setCurrentItemCount(int currentItemCount) {
        this.currentItemCount = currentItemCount;
    }

    public String getNextWebCursor() {
        return nextWebCursor;
    }

    public void setNextWebCursor(String nextWebCursor) {
        this.nextWebCursor = nextWebCursor;
    }

    /*
     * Link to go to the next page of items
     */
    public String getNextLink() {
        return nextLink;
    }

    /**
     * Do not include in POST or PUT
     */
    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }

    /**
     * Link to go to the previous page of items
     */
    public String getPreviousLink() {
        return previousLink;
    }

    /**
     * Do not include in POST or PUT
     */
    public void setPreviousLink(String previousLink) {
        this.previousLink = previousLink;
    }

    public int getCurrentItemCount() {
        return currentItemCount;
    }

    public void setItems(ArrayList<T> items) {
        this.items = items;
    }

    public ArrayList<T> getItems() {
        return items;
    }
}
