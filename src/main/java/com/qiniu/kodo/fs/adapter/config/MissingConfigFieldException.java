package com.qiniu.kodo.fs.adapter.config;

public class MissingConfigFieldException extends Exception {
    public MissingConfigFieldException(String key) {
        super("miss config field on key: " + key);
    }
}