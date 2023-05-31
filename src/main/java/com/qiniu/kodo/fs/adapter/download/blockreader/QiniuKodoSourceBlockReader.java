package com.qiniu.kodo.fs.adapter.download.blockreader;

import com.qiniu.kodo.fs.adapter.blockcache.DataFetcherBlockReader;
import com.qiniu.kodo.fs.adapter.client.IQiniuKodoClient;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class QiniuKodoSourceBlockReader extends DataFetcherBlockReader {
    private final IQiniuKodoClient client;

    public QiniuKodoSourceBlockReader(
            int blockSize,
            IQiniuKodoClient client) {
        super(blockSize);
        this.client = client;
    }

    @Override
    public byte[] fetch(String key, long offset, int size) throws IOException {
        try (InputStream is = client.fetch(key, offset, size)) {
            byte[] buf = new byte[size];
            int cnt = IOUtils.read(is, buf);
            return Arrays.copyOf(buf, cnt);
        }
    }
}
