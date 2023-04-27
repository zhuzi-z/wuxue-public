package com.wuda.wuxue.network;

public class ParseResponseException extends Exception {
    public ParseResponseException() {
        super("The data of response could not be parsed correctly!");
    }
}
