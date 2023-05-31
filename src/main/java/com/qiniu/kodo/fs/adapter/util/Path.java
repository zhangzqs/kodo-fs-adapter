package com.qiniu.kodo.fs.adapter.util;

public class Path {
    public static final String PATH_SEPARATOR = "/";

    private final String path;
    public Path(String path) {
        this.path = path.trim();
    }

    public Path(String parent, String child) {
        this(new Path(parent), new Path(child));
    }
    public Path(Path parent, String child) {
        this(parent, new Path(child));
    }
    public Path(Path parent, Path child) {
        if (child.isAbsolute()) {
            this.path = child.path;
            return;
        }
        String result = parent.path;
        if (!result.endsWith(PATH_SEPARATOR)) {
            result += PATH_SEPARATOR;
        }
        result += child.path;
        this.path = result;
    }

    public Path makeQualified(Path workingDir) {
        return new Path(workingDir, this);
    }

    public boolean isAbsolute() {
        return path.startsWith(PATH_SEPARATOR);
    }

    public boolean isRoot() {
        return path.equals(PATH_SEPARATOR);
    }

    public Path getParent() {
        int index = path.lastIndexOf(PATH_SEPARATOR);
        if (index == path.length() - 1) {
            index = path.lastIndexOf(PATH_SEPARATOR, index - 1);
        }
        if (index == -1) {
            return null;
        }
        return new Path(path.substring(0, index));
    }

    public String getName() {
        int index = path.lastIndexOf(PATH_SEPARATOR);
        if (index == path.length() - 1) {
            index = path.lastIndexOf(PATH_SEPARATOR, index - 1);
        }
        if (index == -1) {
            return path;
        }
        return path.substring(index + 1);
    }
    @Override
    public String toString() {
        return path;
    }
}
