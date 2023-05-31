package com.qiniu.kodo.fs.adapter.upload;

import com.qiniu.common.QiniuException;
import com.qiniu.kodo.fs.adapter.client.IQiniuKodoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class QiniuKodoOutputStream extends OutputStream {

    private static final Logger LOG = LoggerFactory.getLogger(QiniuKodoOutputStream.class);

    private final String key;
    private final PipedOutputStream pos;
    private final PipedInputStream pis;
    private final Future<IOException> future;

    public QiniuKodoOutputStream(
            IQiniuKodoClient client,
            String key,
            boolean overwrite,
            int bufferSize,
            ExecutorService executorService
    ) throws IOException {
        this.key = key;
        this.pos = new PipedOutputStream();
        this.pis = new PipedInputStream(pos, bufferSize);
        this.future = executorService.submit(() -> {
            try {
                // 这里抛出的异常基本都是 QiniuException 是属于 IOException
                // 正常情况上传后应当返回 null
                client.upload(pis, key, overwrite);
                return null;
            } catch (IOException e) {
                return e;
            }
        });
    }

    @Override
    public void write(int b) throws IOException {
        pos.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        pos.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        pos.close();
        try {
            IOException uploadException = future.get();
            if (uploadException == null) {
                // 无异常退出
                return;
            }
            if (uploadException instanceof QiniuException &&
                    ((QiniuException) uploadException).response.statusCode == 614) {
                throw new FileAlreadyExistsException("key exists " + key);
            }
            throw uploadException;
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException(e);
        }
    }
}
