package com.qiniu.kodo.fs.adapter.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class AConfigBase {
    protected final IQiniuConfiguration conf;
    protected final String namespace;

    public AConfigBase(IQiniuConfiguration conf, String namespace) {
        this.conf = conf;
        this.namespace = namespace;
    }

    @Override
    public String toString() {
        try {
            return toHashMap().toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

    public Map<String, Object> toHashMap() throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj = field.get(this);
            if (obj instanceof AConfigBase) {
                obj = ((AConfigBase) obj).toHashMap();
            }
            result.put(field.getName(), obj);
        }
        return result;
    }
}
