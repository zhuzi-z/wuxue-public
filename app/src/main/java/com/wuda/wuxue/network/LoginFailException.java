package com.wuda.wuxue.network;

public class LoginFailException extends Exception {
    LoginFailException() {
        super("Fail to login via cas!");
    }
}
