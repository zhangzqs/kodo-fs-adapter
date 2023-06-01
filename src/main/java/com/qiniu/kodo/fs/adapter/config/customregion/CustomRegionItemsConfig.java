package com.qiniu.kodo.fs.adapter.config.customregion;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.storage.Region;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;
import com.qiniu.kodo.fs.adapter.config.MissingConfigFieldException;

public class CustomRegionItemsConfig extends AConfigBase {

    public CustomRegionItemsConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
    }

    private String getCustomRegionStringFieldString(String customId, String field) throws MissingConfigFieldException {
        String key = String.format("%s.%s.%s", namespace, customId, field);
        String value = conf.get(key);
        if (value != null) return value;
        throw new MissingConfigFieldException(key);
    }

    public Region buildCustomSdkRegion(String customId) throws MissingConfigFieldException {
        // uc服务器地址，如果设置了则直接走autoRegion
        String ucServer = conf.get(String.format("%s.%s.%s", namespace, customId, "ucServer"));
        if (ucServer != null) {
            return Region.autoRegion(ucServer);
        }

        // 资源管理，资源列表，资源处理类域名
        String rsHost = getCustomRegionStringFieldString(customId, "rsHost");
        String rsfHost = getCustomRegionStringFieldString(customId, "rsfHost");
        String apiHost = getCustomRegionStringFieldString(customId, "apiHost");

        // 源站上传，加速上传，源站下载
        String[] srcUpHosts = conf.getStrings(String.format("%s.%s.srcUpHosts", namespace, customId), new String[0]);
        String[] accUpHosts = conf.getStrings(String.format("%s.%s.accUpHosts", namespace, customId), new String[0]);
        String iovipHost = getCustomRegionStringFieldString(customId, "iovipHost");
        String ioSrcHost = getCustomRegionStringFieldString(customId, "ioSrcHost");

        return new Region.Builder()
                .apiHost(apiHost)
                .rsfHost(rsfHost)
                .rsHost(rsHost)
                .iovipHost(iovipHost)
                .ioSrcHost(ioSrcHost)
                .srcUpHost(srcUpHosts)
                .accUpHost(accUpHosts)
                .build();
    }

}
