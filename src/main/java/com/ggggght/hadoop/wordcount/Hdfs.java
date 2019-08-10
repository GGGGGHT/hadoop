package com.ggggght.hadoop.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.Before;
import org.junit.Test;
import sun.misc.IOUtils;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("all")
public class Hdfs {
    Configuration config = null;
    FileSystem fs = null;

    
    public static void main(String[] args) throws Exception {
        //upload file to hdfs
        Configuration cfg = new Configuration();
        cfg.set("uri", "http://192.168.41.225");
        FileSystem fs = FileSystem.get(cfg);
        Path src = new Path("hdfs://192.168.41.225:9000/hadoop-2.6.5.tar.gz");
        FSDataInputStream in = fs.open(src);

        FileOutputStream out = new FileOutputStream("C:\\Users\\root\\Desktop");

        org.apache.commons.io.IOUtils.copy(in, out);
        System.out.println("ok");
    }



    @Test
    public void uploadFile() throws IOException {
        Path path = new Path("hdfs://192.168.41.225:9000/ght/");
        FSDataOutputStream out = fs.create(path);
        FileInputStream in = new FileInputStream
                ("C:\\Users\\root\\AppData\\Roaming\\Typora\\draftsRecover\\2019-8-6 linux 193925.md");
        org.apache.commons.io.IOUtils.copy(in, out);
    }

    @Test
    public void downloadFile() {
    }

    @Before
    public void before() throws IOException {
        config = new Configuration();
        config.set("fs.default.name", "hdfs://192.168.41.225:9000");
        // config.set("fs.defaultFS","hdfs://192.168.41.225:9000/");
        fs = FileSystem.get(config);

    }

    @Test
    public void uploadTest() throws IOException {
        fs.copyFromLocalFile(new Path
                ("C:\\Users\\root\\AppData\\Roaming\\Typora\\draftsRecover\\2019-8-6 " +
                        "linux 193925.md"), new Path
                ("hdfs://192.168.41.225:9000/user/gggg/"));
    }

    public void delFileTest() throws IOException {
        fs.delete(new Path("/user/gggg"),true);
    }

    @Test
    public void listFiles() throws IOException {
        RemoteIterator<LocatedFileStatus> list = fs.listFiles(new Path("/"), true);
        // while (list.hasNext()) {
        //     LocatedFileStatus file = list.next();
        //     System.out.println(file.getPath().getName());
        // }

        FileStatus[] fileStatuses = fs.listStatus(new Path("/"));
        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isDirectory())  {
                System.out.println(fileStatus.getPath().getName());
            }

        }
    }
}

