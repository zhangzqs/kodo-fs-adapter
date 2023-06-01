package com.qiniu.kodo.fs.adapter.config;


import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class AuthConfig extends AConfigBase {
    public final String ACCESS_KEY = namespace + ".accessKey";
    public final String SECRET_KEY = namespace + ".secretKey";

    public final String accessKey;
    public final String secretKey;

    public AuthConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.accessKey = accessKey();
        this.secretKey = secretKey();
    }


    public String accessKey() {
        return conf.get(ACCESS_KEY);
    }


    public String secretKey() {
        return conf.get(SECRET_KEY);
    }

}
