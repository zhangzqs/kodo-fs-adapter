package com.qiniu.kodo.fs.adapter.client;


import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.http.ProxyConfiguration;
import com.qiniu.http.Response;
import com.qiniu.kodo.fs.adapter.client.batch.ListingProducer;
import com.qiniu.kodo.fs.adapter.client.batch.operator.BatchOperator;
import com.qiniu.kodo.fs.adapter.client.batch.operator.CopyOperator;
import com.qiniu.kodo.fs.adapter.client.batch.operator.DeleteOperator;
import com.qiniu.kodo.fs.adapter.client.batch.operator.RenameOperator;
import com.qiniu.kodo.fs.adapter.config.MissingConfigFieldException;
import com.qiniu.kodo.fs.adapter.config.client.base.ListAndBatchBaseConfig;
import com.qiniu.kodo.fs.adapter.util.RemoteIterators;
import com.qiniu.storage.*;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.StringUtils;
import com.qiniu.kodo.fs.adapter.client.batch.BatchOperationConsumer;
import com.qiniu.kodo.fs.adapter.config.QiniuKodoFsConfig;
import com.qiniu.kodo.fs.adapter.config.client.base.ListProducerConfig;
import com.qiniu.kodo.fs.adapter.util.RemoteIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QiniuKodoClient implements IQiniuKodoClient {
    private static final Logger LOG = LoggerFactory.getLogger(QiniuKodoClient.class);

    private final String bucket;

    private final Auth auth;

    private final Client client;

    public final UploadManager uploadManager;
    public final BucketManager bucketManager;

    private final boolean useDownloadHttps;

    private final String downloadDomain;
    private final boolean downloadUseSign;
    private final int downloadSignExpires;

    private final int uploadSignExpires;
    private final QiniuKodoFsConfig fsConfig;
    private final ExecutorService service;
    private final DownloadHttpClient downloadHttpClient;


    public QiniuKodoClient(
            String bucket,
            QiniuKodoFsConfig fsConfig
    ) throws IOException {
        this.bucket = bucket;

        this.fsConfig = fsConfig;
        this.auth = getAuth(fsConfig);
        this.service = Executors.newFixedThreadPool(fsConfig.client.nThread);

        Configuration configuration = buildQiniuConfiguration(fsConfig);
        this.useDownloadHttps = fsConfig.download.useHttps;
        this.client = new Client(configuration);
        this.uploadManager = new UploadManager(configuration);
        this.bucketManager = new BucketManager(auth, configuration, this.client);
        this.downloadDomain = buildDownloadHost(fsConfig, bucketManager, bucket);
        this.downloadUseSign = fsConfig.download.sign.enable;
        this.downloadSignExpires = fsConfig.download.sign.expires;
        this.uploadSignExpires = fsConfig.upload.sign.expires;
        this.downloadHttpClient = new DownloadHttpClient(configuration, fsConfig.download.useNoCacheHeader);
    }

    private static Configuration buildQiniuConfiguration(QiniuKodoFsConfig fsConfig) throws QiniuException {
        Configuration configuration = new Configuration();
        configuration.region = buildRegion(fsConfig);
        if (fsConfig.upload.v2.enable) {
            configuration.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
            configuration.resumableUploadAPIV2BlockSize = fsConfig.upload.v2.blockSize();
        } else {
            configuration.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V1;
        }
        configuration.resumableUploadMaxConcurrentTaskCount = fsConfig.upload.maxConcurrentTasks;
        configuration.useHttpsDomains = fsConfig.useHttps;
        configuration.accUpHostFirst = fsConfig.upload.accUpHostFirst;
        configuration.useDefaultUpHostIfNone = fsConfig.upload.useDefaultUpHostIfNone;
        configuration.proxy = buildQiniuProxyConfiguration(fsConfig);
        return configuration;
    }

    private static ProxyConfiguration buildQiniuProxyConfiguration(QiniuKodoFsConfig fsConfig) {
        if (!fsConfig.proxy.enable) {
            return null;
        }
        return new ProxyConfiguration(
                fsConfig.proxy.hostname,
                fsConfig.proxy.port,
                fsConfig.proxy.username,
                fsConfig.proxy.password,
                fsConfig.proxy.type
        );
    }

    private static String buildDownloadHost(
            QiniuKodoFsConfig fsConfig,
            BucketManager bucketManager,
            String bucket
    ) throws QiniuException {
        // 优先走用户显式设置的下载域名
        if (fsConfig.download.domain != null) {
            return fsConfig.download.domain;
        }
        // 当未配置下载域名时，走源站下载域名
        return bucketManager.getDefaultIoSrcHost(bucket);
    }

    private static Region buildRegion(QiniuKodoFsConfig fsConfig) throws QiniuException {
        if (fsConfig.customRegion.id != null) {
            // 私有云环境
            try {
                return fsConfig.customRegion.getCustomRegion();
            } catch (MissingConfigFieldException e) {
                throw new QiniuException(e);
            }

        }
        // 公有云环境
        return Region.autoRegion();
    }

    private static Auth getAuth(QiniuKodoFsConfig fsConfig) throws IOException {
        String ak = fsConfig.auth.accessKey;
        String sk = fsConfig.auth.secretKey;
        if (StringUtils.isNullOrEmpty(ak)) {
            throw new IOException(String.format(
                    "Qiniu access key can't empty, you should set it with %s in core-site.xml",
                    fsConfig.auth.ACCESS_KEY
            ));
        }
        if (StringUtils.isNullOrEmpty(sk)) {
            throw new IOException(String.format(
                    "Qiniu secret key can't empty, you should set it with %s in core-site.xml",
                    fsConfig.auth.SECRET_KEY
            ));
        }
        return Auth.create(ak, sk);
    }

    /**
     * 根据key和overwrite生成上传token
     */
    public String getUploadToken(String key, boolean overwrite) {
        StringMap policy = new StringMap();
        policy.put("insertOnly", overwrite ? 0 : 1);
        return auth.uploadToken(bucket, key, uploadSignExpires, policy);
    }

    private class QiniuUploader {
        private final boolean overwrite;

        private QiniuUploader(boolean overwrite) {
            this.overwrite = overwrite;
        }

        void upload(String key, InputStream stream) throws IOException {
            if (!uploadManager.put(stream, key, getUploadToken(key, overwrite),
                    null, null).isOK()) {
                throw new IOException("Upload failed"
                        + " bucket: " + bucket
                        + " key: " + key
                        + " overwrite: " + overwrite);
            }
        }

        void uploadArray(String key, byte[] data) throws IOException {
            if (!uploadManager.put(data, key, getUploadToken(key, overwrite)).isOK()) {
                throw new IOException("Upload failed"
                        + " bucket: " + bucket
                        + " key: " + key
                        + " overwrite: " + overwrite);
            }
        }

        void uploadEmpty(String key) throws IOException {
            uploadArray(key, new byte[0]);
        }
    }

    /**
     * 给定一个输入流将读取并上传对应文件
     */
    @Override
    public void upload(InputStream stream, String key, boolean overwrite) throws IOException {
        QiniuUploader uploader = new QiniuUploader(overwrite);
        if (stream.available() > 0) {
            uploader.upload(key, stream);
            return;
        }
        int b = stream.read();
        if (b == -1) {
            // 空流
            uploader.uploadEmpty(key);
            return;
        }
        // 有内容，还得拼回去
        SequenceInputStream sis = new SequenceInputStream(new ByteArrayInputStream(new byte[]{(byte) b}), stream);
        uploader.upload(key, sis);
    }


    /**
     * 通过HEAD来获取指定的key大小
     */
    @Override
    public long getLength(String key) throws IOException {
        try {
            Response response = client.head(getFileUrlByKey(key),
                    new StringMap().put("Accept-Encoding", "identity"));
            String len = response.header("content-length", null);

            if (len == null) {
                throw new IOException(String.format("Cannot get object length by key: %s", key));
            }

            return Integer.parseInt(len);
        } catch (QiniuException e) {
            if (e.response == null) {
                throw e;
            }
            switch (e.response.statusCode) {
                case 612:
                case 404:
                    throw new FileNotFoundException("key: " + key);
                default:
                    throw e;
            }
        }
    }


    @Override
    public boolean exists(String key) throws IOException {
        // 这里如果使用head判断404，会导致某些场景下命中缓存，导致文件被删除但是返回200
        return getFileStatus(key) != null;
    }

    /**
     * 根据指定的key和文件大小获取一个输入流
     */
    @Override
    public InputStream fetch(String key, long offset, int size) throws IOException {
        return downloadHttpClient.fetch(getFileUrlByKey(key), offset, size);
    }

    /**
     * 获取一个指定前缀的对象
     */
    @Override
    public QiniuKodoFileInfo listOneStatus(String keyPrefix) throws IOException {
        List<QiniuKodoFileInfo> ret = listNStatus(keyPrefix, 1);
        if (ret.isEmpty()) {
            return null;
        }
        return ret.get(0);
    }

    /**
     * 获取指定前缀的最多前n个对象
     */
    public List<QiniuKodoFileInfo> listNStatus(String keyPrefix, int n) throws IOException {
        FileListing listing = bucketManager.listFiles(bucket, keyPrefix, null, n, "");
        if (listing.items == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(listing.items).map(QiniuKodoClient::qiniuFileInfoToMyFileInfo).collect(Collectors.toList());
    }

    /**
     * 列举出指定前缀的所有对象
     *
     * @param prefixKey    前缀
     * @param useDirectory 是否使用路径分割
     * @return 迭代器
     */
    public RemoteIterator<QiniuKodoFileInfo> listStatusIterator(String prefixKey, boolean useDirectory) {
        ListProducerConfig listConfig = fsConfig.client.list;
        // 消息队列
        BlockingQueue<FileInfo> fileInfoQueue = new LinkedBlockingQueue<>(listConfig.bufferSize);

        // 生产者
        ListingProducer producer = new ListingProducer(
                fileInfoQueue, bucketManager, bucket, prefixKey, false,
                listConfig.singleRequestLimit,
                useDirectory, listConfig.useListV2,
                listConfig.offerTimeout
        );

        // 生产者线程
        Future<Exception> future = service.submit(producer);
        return new RemoteIterator<QiniuKodoFileInfo>() {
            @Override
            public boolean hasNext() throws IOException {
                while (true) {
                    // 如果队列不为空，返回 true 表示有下一个
                    if (!fileInfoQueue.isEmpty()) {
                        return true;
                    }

                    // 若已完成且队列为空，表示没有下一个了
                    if (future.isDone() && fileInfoQueue.isEmpty()) {
                        try {
                            Exception e = future.get();
                            // 若生产者线程抛出异常，这里抛出IOException
                            if (e != null) {
                                throw new IOException(e);
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            throw new IOException(e);
                        }
                        return false;
                    }
                    // 若未完成，且队列为空，则等待一段时间
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return false;
                    }
                }
            }

            @Override
            public QiniuKodoFileInfo next() throws IOException {
                if (!hasNext()) {
                    return null;
                }

                try {
                    return qiniuFileInfoToMyFileInfo(fileInfoQueue.poll(Long.MAX_VALUE, TimeUnit.SECONDS));
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
            }
        };
    }

    /**
     * 若 useDirectory 为 true, 则列举出分级的目录效果
     * 否则，将呈现出所有前缀为key的对象
     */
    @Override
    public List<QiniuKodoFileInfo> listStatus(String prefixKey, boolean useDirectory) throws IOException {
        return RemoteIterators.toList(listStatusIterator(prefixKey, useDirectory));
    }


    /**
     * 复制对象
     */
    @Override
    public void copyKey(String oldKey, String newKey) throws IOException {
        bucketManager.copy(bucket, oldKey, bucket, newKey);
    }

    /**
     * 列举并批处理
     *
     * @param config    生产消费相关的配置
     * @param prefixKey 生产列举的key前缀
     * @param f         消费操作函数
     */
    private void listAndBatch(
            ListAndBatchBaseConfig config,
            String prefixKey,
            Function<FileInfo, BatchOperator> f
    ) throws IOException {
        // 消息队列
        // 对象列举生产者队列
        BlockingQueue<FileInfo> fileInfoQueue = new LinkedBlockingQueue<>(config.listProducer.bufferSize);
        // 批处理队列
        BlockingQueue<BatchOperator> operatorQueue = new LinkedBlockingQueue<>(config.batchConsumer.bufferSize);

        // 对象列举生产者
        ListingProducer producer = new ListingProducer(
                fileInfoQueue, bucketManager, bucket, prefixKey, true,
                config.listProducer.singleRequestLimit, false,
                config.listProducer.useListV2,
                config.listProducer.offerTimeout
        );
        // 生产者线程
        Future<Exception> producerFuture = service.submit(producer);

        // 消费者线程
        int consumerCount = config.batchConsumer.count;
        BatchOperationConsumer[] consumers = new BatchOperationConsumer[consumerCount];
        Future<?>[] futures = new Future[consumerCount];

        // 多消费者共享一个队列
        for (int i = 0; i < consumerCount; i++) {
            consumers[i] = new BatchOperationConsumer(
                    operatorQueue, bucketManager,
                    config.batchConsumer.singleBatchRequestLimit,
                    config.batchConsumer.pollTimeout
            );
            futures[i] = service.submit(consumers[i]);
        }

        // 从生产者队列取出产品并加工后放入消费者队列
        while (!producerFuture.isDone() || !fileInfoQueue.isEmpty()) {
            FileInfo product = fileInfoQueue.poll();
            // 缓冲区队列为空
            if (product == null) {
                continue;
            }

            boolean success;
            do {
                success = operatorQueue.offer(f.apply(product));
            } while (!success);
        }

        // 生产完毕
        try {
            if (producerFuture.get() != null) {
                throw producerFuture.get();
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        LOG.debug("生产者生产完毕");

        // 等待消费队列为空
        while (true) {
            if (operatorQueue.isEmpty()) {
                break;
            }
        }

        // 向所有消费者发送关闭信号
        for (int i = 0; i < consumerCount; i++) {
            consumers[i].stop();
        }

        // 等待所有的消费者消费完毕
        for (int i = 0; i < consumerCount; i++) {
            try {
                if (futures[i].get() != null) {
                    throw (Exception) futures[i].get();
                }
                LOG.debug("消费者{}号消费完毕", i);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void copyKeys(String oldPrefix, String newPrefix) throws IOException {
        listAndBatch(
                fsConfig.client.copy,
                oldPrefix,
                (FileInfo fileInfo) -> {
                    String fromFileKey = fileInfo.key;
                    String toFileKey = fromFileKey.replaceFirst(oldPrefix, newPrefix);
                    return new CopyOperator(bucket, fromFileKey, bucket, toFileKey);
                }
        );
    }

    /**
     * 重命名指定 key 的对象
     */
    @Override
    public void renameKey(String oldKey, String newKey) throws IOException {
        if (Objects.equals(oldKey, newKey)) {
            return;
        }
        Response response = bucketManager.rename(bucket, oldKey, newKey);
        incrementOneReadOps();
    }

    /**
     * 批量重命名 key 为指定前缀的对象
     */
    @Override
    public void renameKeys(String oldPrefix, String newPrefix) throws IOException {
        listAndBatch(
                fsConfig.client.rename,
                oldPrefix,
                (FileInfo fileInfo) -> {
                    String fromFileKey = fileInfo.key;
                    String toFileKey = fromFileKey.replaceFirst(oldPrefix, newPrefix);
                    return new RenameOperator(bucket, fromFileKey, toFileKey);
                }
        );
    }

    /**
     * 仅删除一层 key
     */
    @Override
    public void deleteKey(String key) throws IOException {
        Response response = bucketManager.delete(bucket, key);
        incrementOneReadOps();
    }

    @Override
    public void deleteKeys(String prefix) throws IOException {
        listAndBatch(
                fsConfig.client.delete,
                prefix,
                e -> new DeleteOperator(bucket, e.key)
        );
    }

    private void incrementOneReadOps() {

    }

    /**
     * 使用对象存储模拟文件系统，文件夹只是作为一个空白文件，仅用于表示文件夹的存在性与元数据的存储
     * 该 makeEmptyObject 仅创建一层空文件
     */
    @Override
    public void makeEmptyObject(String key) throws IOException {
        QiniuUploader uploader = new QiniuUploader(false);
        uploader.uploadEmpty(key);
    }

    /**
     * 不存在不抛异常，返回为空，只有在其他错误时抛异常
     */
    @Override
    public QiniuKodoFileInfo getFileStatus(String key) throws IOException {
        try {
            FileInfo fileInfo = bucketManager.stat(bucket, key);
            if (fileInfo != null) {
                fileInfo.key = key;
            }
            return qiniuFileInfoToMyFileInfo(fileInfo);
        } catch (QiniuException e) {
            if (e.response != null && e.response.statusCode == 612) {
                return null;
            }
            throw e;
        }
    }


    /**
     * 构造某个文件的下载url
     */
    private String getFileUrlByKey(String key) throws IOException {
        DownloadUrl downloadUrl = new DownloadUrl(downloadDomain, useDownloadHttps, key);
        String url = downloadUrl.buildURL();
        if (downloadUseSign) {
            return auth.privateDownloadUrl(url, downloadSignExpires);
        }
        return url;
    }

    public static QiniuKodoFileInfo qiniuFileInfoToMyFileInfo(FileInfo fileInfo) {
        if (fileInfo == null) return null;
        return new QiniuKodoFileInfo(
                fileInfo.key,
                fileInfo.fsize,
                fileInfo.putTime / 10000
        );
    }
}
