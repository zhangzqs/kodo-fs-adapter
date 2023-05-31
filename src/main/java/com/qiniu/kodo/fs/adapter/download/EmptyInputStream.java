package com.qiniu.kodo.fs.adapter.download;

import java.io.IOException;

public class EmptyInputStream extends SeekableInputStream {
    @Override
    public int available() throws IOException {
        return 0;
    }

    @Override
    public void seek(long pos) throws IOException {

    }

    @Override
    public long getPos() throws IOException {
        return 0;
    }

    @Override
    public int read() throws IOException {
        return -1;
    }
}
