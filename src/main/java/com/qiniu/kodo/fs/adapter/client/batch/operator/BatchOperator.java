package com.qiniu.kodo.fs.adapter.client.batch.operator;

import com.qiniu.storage.BucketManager.BatchOperations;

public interface BatchOperator {
    void addTo(BatchOperations batchOperations);
}

