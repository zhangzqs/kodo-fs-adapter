import com.qiniu.kodo.fs.adapter.IQiniuKodoFileSystem;
import com.qiniu.kodo.fs.adapter.QiniuKodoFileSystem;
import com.qiniu.kodo.fs.adapter.config.IQiniuConfiguration;
import com.qiniu.kodo.fs.adapter.util.FileStatus;
import com.qiniu.kodo.fs.adapter.util.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

class QiniuKodoConfiguration implements IQiniuConfiguration {
    private final Map<String, String> map;
    public QiniuKodoConfiguration(Map<String, String> map) {
        this.map = map;
    }
    @Override
    public String get(String key, String defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }
}
public class TestQiniuKodoFileSystem {
    IQiniuConfiguration conf;
    IQiniuKodoFileSystem fs;
    @BeforeEach
    public void setup() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("fs.qiniu.auth.accessKey", "ak");
        map.put("fs.qiniu.auth.secretKey", "sk");
        conf = new QiniuKodoConfiguration(map);
        fs = new QiniuKodoFileSystem("hadoop-java", conf);
    }

    public void walk(String path, int depth) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(new Path(path));
        for (FileStatus fileStatus : fileStatuses) {
            for (int i = 0; i < depth; i++) {
                System.out.print("  ");
            }
            System.out.println(fileStatus);
            if (fileStatus.isDirectory()) {
                walk(fileStatus.getPath().toString(), depth + 1);
            }
        }
    }

    @Test
    public void testQiniuKodoFileSystem() throws IOException {
        OutputStream os = fs.create(new Path("/test.txt"), true);
        os.write(new byte[]{1,2,3});
        os.close();

        fs.create(new Path("/xxx/test.txt"), true).close();
        walk("/", 0);
    }
}
