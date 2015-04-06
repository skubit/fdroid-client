package com.skubit.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;

// https://google-styleguide.googlecode.com/svn/trunk/jsoncstyleguide.xml#Top-Level_Reserved_Property_Names
public final class ErrorMessage implements Dto {

    /**
     *
     */
    private static final long serialVersionUID = 3699695277368465994L;

    private int code;

    private String message;

    private SubErrorMessage[] messages;

    public ErrorMessage() {
    }

    public ErrorMessage(@JsonProperty("code") int code,
            @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public boolean hasErrors() {
        return messages != null && messages.length > 0;
    }

    public void setSubErrors(SubErrorMessage... messages) {
        this.messages = messages;
    }

    public void setSubErrors(ArrayList<SubErrorMessage> messages) {
        if (messages != null) {
            this.messages = ((SubErrorMessage[]) messages
                    .toArray(new SubErrorMessage[messages.size()]));
        }
    }

    public SubErrorMessage[] getMessages() {
        return messages;
    }

    public void setMessages(SubErrorMessage[] messages) {
        this.messages = messages;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorMessage [code=" + code + ", message=" + message
                + ", messages=" + Arrays.toString(messages) + "]";
    }

}
