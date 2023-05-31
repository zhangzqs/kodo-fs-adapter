package com.qiniu.kodo.fs.adapter.util;

import java.util.Map;

public interface OnLRUCacheRemoveListener<K, V> {
    void onRemove(Map.Entry<K, V> entry);
}