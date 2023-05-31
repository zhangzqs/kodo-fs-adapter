package com.qiniu.kodo.fs.adapter.blockcache;

import java.io.IOException;

public interface IDataFetcher {
    byte[] fetch(String key, long offset, int size) throws IOException;
}