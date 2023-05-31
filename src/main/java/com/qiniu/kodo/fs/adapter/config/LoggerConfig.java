package com.qiniu.kodo.fs.adapter.config;

public class LoggerConfig extends AConfigBase {
    public final String level;

    public LoggerConfig(IQiniuConfiguration conf, String namespace) {
        super(conf, namespace);
        this.level = level();
    }


    public String level() {
        return conf.get(namespace + ".level", "INFO");
    }
}
