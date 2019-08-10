package com.ggggght.hadoop.wordcount;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 用来描述一个特定的作业
 * 比如,该作业使用哪个类作为逻辑处理中的map,哪个作为reduce
 * 还可以指定该作业要处理的数据处在的路径
 * 还可以指定该作业输出的结果放到哪个路径
 */
public class WCRunner {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        //设置整个job所用的那些类在哪个jar包中
        job.setJarByClass(WCRunner.class);

        //描述处理mapper的类
        job.setMapperClass(WcMapper.class);
        //描述处理reduce的类
        job.setReducerClass(WcReduce.class);
        //此处设置的是reduce的输出数据的KV类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        //此处设置的是mapper的输出数据的KV类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        //指定原始数据存放路径
        FileInputFormat.setInputPaths(job, new Path("/wc/srcdata/"));
        //指定处理结果的输入数据存放路径
        FileOutputFormat.setOutputPath(job,new Path("/wc/output/"));

        //提交job运行
        job.waitForCompletion(true);
    }
}
