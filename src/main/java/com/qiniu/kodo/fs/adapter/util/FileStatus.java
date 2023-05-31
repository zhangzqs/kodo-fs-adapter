package com.qiniu.kodo.fs.adapter.util;

public class FileStatus {
    private final long length;
    private final boolean isdir;
    private final long blocksize;
    private final long putTime;
    private final Path path;

    public FileStatus(long length, boolean isdir, long blocksize, long putTime, Path path) {
        this.length = length;
        this.isdir = isdir;
        this.blocksize = blocksize;
        this.putTime = putTime;
        this.path = path;
    }

    public long getLen() {
        return length;
    }

    public boolean isDirectory() {
        return isdir;
    }

    public boolean isFile() {
        return !isdir;
    }
    public long getBlocksize() {
        return blocksize;
    }

    public long getPutTime() {
        return putTime;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "FileStatus{" +
                "length=" + length +
                ", isdir=" + isdir +
                ", blocksize=" + blocksize +
                ", putTime=" + putTime +
                ", path=" + path +
                '}';
    }
}
