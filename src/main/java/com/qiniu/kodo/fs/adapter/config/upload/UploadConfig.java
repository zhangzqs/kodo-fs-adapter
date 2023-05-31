package com.qiniu.kodo.fs.adapter.config.upload;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.config.IQiniuConfiguration;

public class UploadConfig extends AConfigBase {
    public final SignConfig sign;
    // 同一时刻最大并发上传文件数
    public final int maxConcurrentUploadFiles;
    public final int maxConcurrentTasks;
    public final boolean accUpHostFirst;
    public final boolean useDefaultUpHostIfNone;
    public final V2Config v2;
    public final int bufferSize;

    public UploadConfig(IQiniuConfiguration conf, String namespace) {
        super(conf, namespace);
        this.sign = sign();
        this.maxConcurrentUploadFiles = maxConcurrentUploadFiles();
        this.maxConcurrentTasks = maxConcurrentTasks();
        this.accUpHostFirst = accUpHostFirst();
        this.useDefaultUpHostIfNone = useDefaultUpHostIfNone();
        this.v2 = v2();
        this.bufferSize = bufferSize();
    }

    private int maxConcurrentUploadFiles() {
        return conf.getInt(namespace + ".maxConcurrentUploadFiles", 4);
    }

    private int bufferSize() {
        return conf.getInt(namespace + ".bufferSize", 16 * 1024 * 1024);
    }

    private V2Config v2() {
        return new V2Config(conf, namespace + ".v2");
    }

    private boolean useDefaultUpHostIfNone() {
        return conf.getBoolean(namespace + ".useDefaultUpHostIfNone", true);
    }

    private SignConfig sign() {
        return new SignConfig(conf, namespace + ".sign");
    }

    private int maxConcurrentTasks() {
        return conf.getInt(namespace + ".concurrentTasks", 1);
    }
    

    private boolean accUpHostFirst() {
        return conf.getBoolean(namespace + ".accUpHostFirst", true);
    }
}
