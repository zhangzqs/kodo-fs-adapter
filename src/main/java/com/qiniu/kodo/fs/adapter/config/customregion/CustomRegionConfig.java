package com.qiniu.kodo.fs.adapter.config.customregion;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.storage.Region;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;
import com.qiniu.kodo.fs.adapter.config.MissingConfigFieldException;

public class CustomRegionConfig extends AConfigBase {
    public final String id;

    public final CustomRegionItemsConfig custom;


    public CustomRegionConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.id = id();
        this.custom = new CustomRegionItemsConfig(conf, namespace + ".custom");
    }

    private String id() {
        return conf.get(namespace + ".id");
    }

    public Region getCustomRegion() throws MissingConfigFieldException {
        return this.custom.buildCustomSdkRegion(this.id);
    }
}
