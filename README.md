# Qiniu Kodo FileSystem Adapter

基于七牛对象存储 Kodo，实现的模拟文件系统兼容层。

## 特性

基于七牛对象存储 Kodo，实现了文件系统的基本操作，
目前已实现的接口有：
```java
public interface IQiniuKodoFileSystem extends Closeable {
    SeekableInputStream open(Path path) throws IOException;
    OutputStream create(Path path, boolean overwrite) throws IOException;
    boolean rename(Path srcPath, Path dstPath) throws IOException;
    boolean delete(Path path, boolean recursive) throws IOException;
    FileStatus[] listStatus(Path path) throws IOException;
    RemoteIterator<FileStatus> listStatusIterator(Path path) throws IOException;
    void setWorkingDirectory(Path newPath);
    Path getWorkingDirectory();
    boolean mkdirs(Path path) throws IOException;
    boolean exists(Path path) throws IOException;
    FileStatus getFileStatus(Path path) throws IOException;
}
```

## 使用

以下代码演示了基本的使用方法：
1. 根据七牛的 AK/SK 构造配置对象
2. 创建文件系统对象并指明 Bucket 名称和配置对象
3. 使用文件系统在路径 `/` 下以`overwrite`的方式创建文件 `test.txt`，并写入三字节数据 [1, 2, 3]，关闭文件的输出流
4. 使用文件系统在路径 `/xxx` 下以`overwrite`的方式创建空文件 `test.txt`，并关闭文件的输出流
5. 使用文件系统遍历路径 `/` 下的所有文件
```java
public class Main {
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
        IQiniuKodoFileSystem fs = new QiniuKodoFileSystem("example-bucket", conf);

        OutputStream os = fs.create(new Path("/test.txt"), true);
        os.write(new byte[]{1,2,3});
        os.close();

        fs.create(new Path("/xxx/test.txt"), true).close();
        walk(fs, "/", 0);
    }
}
```