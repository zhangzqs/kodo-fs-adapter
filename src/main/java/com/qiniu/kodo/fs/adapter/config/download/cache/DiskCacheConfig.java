package com.qiniu.kodo.fs.adapter.config.download.cache;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.config.IQiniuConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DiskCacheConfig extends AConfigBase {
    public final boolean enable;
    public final int blocks;
    public final Path dir;
    public final int expires;

    public DiskCacheConfig(IQiniuConfiguration conf, String namespace) {
        super(conf, namespace);
        this.enable = enable();
        this.blocks = blocks();
        this.dir = dir();
        this.expires = expires();
    }

    /**
     * 是否启用磁盘缓存
     */
    private boolean enable() {
        return conf.getBoolean(namespace + ".enable", false);
    }

    /**
     * 读取文件时磁盘LRU缓冲区的最大块数量
     */
    private int blocks() {
        return conf.getInt(namespace + ".blocks", 120);
    }

    /**
     * 读取下载缓冲区的文件夹路径
     */
    private Path dir() {
        String dir = conf.get(namespace + ".dir");
        if (dir != null) {
            return Paths.get(dir);
        }

        String tempDir = System.getProperty("java.io.tmpdir");
        return Paths.get(tempDir, "qiniu", "download");
    }

    /**
     * 磁盘缓存过期时间
     */
    private int expires() {
        return conf.getInt(namespace + ".expires", 24 * 3600);
    }
}
