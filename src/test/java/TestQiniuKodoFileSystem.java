import com.qiniu.kodo.fs.adapter.IQiniuKodoFileSystem;
import com.qiniu.kodo.fs.adapter.QiniuKodoFileSystem;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;
import com.qiniu.kodo.fs.adapter.util.FileStatus;
import com.qiniu.kodo.fs.adapter.util.KVConfigurationMapImpl;
import com.qiniu.kodo.fs.adapter.util.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public class TestQiniuKodoFileSystem {
    public static void walk(IQiniuKodoFileSystem fs, String path, int depth) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(new Path(path));
        for (FileStatus fileStatus : fileStatuses) {
            for (int i = 0; i < depth; i++) {
                System.out.print("  ");
            }
            System.out.println(fileStatus);
            if (fileStatus.isDirectory()) {
                walk(fs, fileStatus.getPath().toString(), depth + 1);
            }
        }
    }
    public static void main(String[] args) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("fs.qiniu.auth.accessKey", "ak");
        map.put("fs.qiniu.auth.secretKey", "sk");
        IKVConfiguration conf = new KVConfigurationMapImpl(map);
        IQiniuKodoFileSystem fs = new QiniuKodoFileSystem("hadoop-java", conf);

        OutputStream os = fs.create(new Path("/test.txt"), true);
        os.write(new byte[]{1,2,3});
        os.close();

        fs.create(new Path("/xxx/test.txt"), true).close();
        walk(fs, "/", 0);
    }
}
