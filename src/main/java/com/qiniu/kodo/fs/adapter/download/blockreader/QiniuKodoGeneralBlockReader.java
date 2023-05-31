package com.qiniu.kodo.fs.adapter.download.blockreader;

import com.qiniu.kodo.fs.adapter.blockcache.DiskCacheBlockReader;
import com.qiniu.kodo.fs.adapter.blockcache.IBlockReader;
import com.qiniu.kodo.fs.adapter.blockcache.MemoryCacheBlockReader;
import com.qiniu.kodo.fs.adapter.client.IQiniuKodoClient;
import com.qiniu.kodo.fs.adapter.config.QiniuKodoFsConfig;
import com.qiniu.kodo.fs.adapter.config.download.cache.DiskCacheConfig;
import com.qiniu.kodo.fs.adapter.config.download.cache.MemoryCacheConfig;

import java.io.IOException;

public class QiniuKodoGeneralBlockReader implements IBlockReader {

    private IBlockReader finalReader;
    private final int blockSize;

    public QiniuKodoGeneralBlockReader(
            QiniuKodoFsConfig fsConfig,
            IQiniuKodoClient client
    ) throws IOException {
        int blockSize = fsConfig.download.blockSize;
        DiskCacheConfig diskCache = fsConfig.download.cache.disk;
        MemoryCacheConfig memoryCache = fsConfig.download.cache.memory;

        // 构造原始数据获取器
        this.finalReader = new QiniuKodoSourceBlockReader(blockSize, client);

        if (diskCache.enable) {
            // 添加磁盘缓存层
            this.finalReader = new DiskCacheBlockReader(
                    this.finalReader,
                    diskCache.blocks,
                    diskCache.dir,
                    diskCache.expires
            );
        }

        if (memoryCache.enable) {
            // 添加内存缓存
            this.finalReader = new MemoryCacheBlockReader(
                    this.finalReader,
                    memoryCache.blocks
            );
        }
        this.blockSize = finalReader.getBlockSize();
    }

    @Override
    public int getBlockSize() {
        return blockSize;
    }

    @Override
    public byte[] readBlock(String key, int blockId) throws IOException {
        return finalReader.readBlock(key, blockId);
    }

    @Override
    public void close() throws IOException {
        this.finalReader.close();
    }

    @Override
    public void deleteBlocks(String key) {
        finalReader.deleteBlocks(key);
    }
}
