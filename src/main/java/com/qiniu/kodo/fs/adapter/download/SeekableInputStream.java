package com.qiniu.kodo.fs.adapter.download;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public abstract class SeekableInputStream extends InputStream {
    abstract void seek(long pos) throws IOException;

    abstract long getPos() throws IOException ;
}
