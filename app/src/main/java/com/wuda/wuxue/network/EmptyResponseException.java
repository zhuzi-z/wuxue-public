package com.wuda.wuxue.network;

public class EmptyResponseException extends Exception {
    public EmptyResponseException() {
        super("The response of request is empty!");
    }
}
