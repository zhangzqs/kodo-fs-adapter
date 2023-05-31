package com.qiniu.kodo.fs.adapter.download;

import com.qiniu.kodo.fs.adapter.blockcache.IBlockReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class QiniuKodoInputStream extends SeekableInputStream {
    private static final Logger LOG = LoggerFactory.getLogger(QiniuKodoInputStream.class);
    private final boolean useRandomReader;
    private final SeekableInputStream generalStrategy;
    private final SeekableInputStream randomStrategy;
    private SeekableInputStream currentStrategy;
    private final String key;

    public QiniuKodoInputStream(
            String key,
            boolean useRandomReader,
            IBlockReader generalReader,
            IBlockReader randomReader,
            long contentLength
    ) {
        this.key = key;

        this.useRandomReader = useRandomReader;
        this.generalStrategy = new QiniuKodoCommonInputStream(key, generalReader, contentLength);
        this.randomStrategy = new QiniuKodoCommonInputStream(key, randomReader, contentLength);

        // 默认通用策略
        this.currentStrategy = generalStrategy;
        LOG.trace("File {} read strategy is general reader", key);
    }

    @Override
    public int available() throws IOException {
        return currentStrategy.available();
    }

    @Override
    public void seek(long pos) throws IOException {
        if (useRandomReader) {
            this.currentStrategy = randomStrategy;
            LOG.info("File {} read strategy switch to random reader", key);
        }
        currentStrategy.seek(pos);
    }

    @Override
    public long getPos() throws IOException {
        return currentStrategy.getPos();
    }
    @Override
    public int read() throws IOException {
        return currentStrategy.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return currentStrategy.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        generalStrategy.close();
        randomStrategy.close();
    }
}
