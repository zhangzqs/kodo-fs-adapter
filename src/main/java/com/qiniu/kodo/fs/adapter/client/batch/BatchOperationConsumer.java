package com.qiniu.kodo.fs.adapter.client.batch;

import com.qiniu.common.QiniuException;
import com.qiniu.kodo.fs.adapter.client.batch.operator.BatchOperator;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.BucketManager.BatchOperations;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class BatchOperationConsumer implements Callable<Exception> {
    private final BlockingQueue<BatchOperator> queue;
    private final BucketManager bucketManager;
    private final int singleBatchRequestLimit;
    private final int pollTimeout;

    private BatchOperations batchOperations = null;
    private int batchOperationsSize = 0;
    private volatile boolean isRunning = true;

    /**
     * 批处理消费者
     *
     * @param queue                   从队列中读取批处理操作
     * @param bucketManager           bucketManager
     * @param singleBatchRequestLimit 单次批处理的limit值
     * @param pollTimeout             每次poll的超时时间
     */
    public BatchOperationConsumer(
            BlockingQueue<BatchOperator> queue,
            BucketManager bucketManager,
            int singleBatchRequestLimit,
            int pollTimeout
    ) {
        this.queue = queue;
        this.bucketManager = bucketManager;
        this.singleBatchRequestLimit = singleBatchRequestLimit;
        this.pollTimeout = pollTimeout;
    }

    private void submitBatchOperations() throws QiniuException {
        if (batchOperations == null) {
            return;
        }
        bucketManager.batch(batchOperations);
        batchOperations = null;
        batchOperationsSize = 0;
    }

    private void loop() throws InterruptedException, QiniuException {
        BatchOperator operator = queue.poll(pollTimeout, TimeUnit.MILLISECONDS);

        // poll失败了，等待下一次循环轮询
        if (operator == null) {
            return;
        }
        if (batchOperations == null) {
            batchOperations = new BatchOperations();
        }

        operator.addTo(batchOperations);
        batchOperationsSize++;

        // 批处理数目不够，直接等待下一次poll
        if (batchOperationsSize < singleBatchRequestLimit) {
            return;
        }
        // 批处理到到达一定数目时提交
        submitBatchOperations();
    }

    @Override
    public Exception call() {
        try {
            // is Running == true or
            // queue非空
            while (isRunning || !queue.isEmpty()) {
                loop();
            }
            // isRunning is false && queue is empty
            // 提交剩余的批处理
            if (batchOperationsSize > 0) {
                try {
                    submitBatchOperations();
                } catch (QiniuException e) {
                    return e;
                }
            }
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    public void stop() {
        isRunning = false;
    }
}
