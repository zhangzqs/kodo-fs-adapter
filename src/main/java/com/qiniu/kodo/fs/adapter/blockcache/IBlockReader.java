package com.qiniu.kodo.fs.adapter.blockcache;

import java.io.Closeable;
import java.io.IOException;

public interface IBlockReader extends Closeable {
    int getBlockSize();

    byte[] readBlock(String key, int blockId) throws IOException;
    
    // 删除该存储块
    void deleteBlocks(String key);
}
