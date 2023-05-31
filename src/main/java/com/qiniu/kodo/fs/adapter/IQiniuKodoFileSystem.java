package com.qiniu.kodo.fs.adapter;

import com.qiniu.kodo.fs.adapter.download.SeekableInputStream;
import com.qiniu.kodo.fs.adapter.util.FileStatus;
import com.qiniu.kodo.fs.adapter.util.Path;
import com.qiniu.kodo.fs.adapter.util.RemoteIterator;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public interface IQiniuKodoFileSystem extends Closeable {
    SeekableInputStream open(Path path) throws IOException;
    OutputStream create(Path path, boolean overwrite) throws IOException;
    boolean rename(Path srcPath, Path dstPath) throws IOException;
    boolean delete(Path path, boolean recursive) throws IOException;
    FileStatus[] listStatus(Path path) throws IOException;
    RemoteIterator<FileStatus> listStatusIterator(Path path) throws IOException;
    void setWorkingDirectory(Path newPath);
    Path getWorkingDirectory();
    boolean mkdirs(Path path) throws IOException;
    boolean exists(Path path) throws IOException;
    FileStatus getFileStatus(Path path) throws IOException;
}
