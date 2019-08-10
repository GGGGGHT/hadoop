package com.ggggght.hadoop.wordcount;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.StringUtils;

import java.io.IOException;

/**
 * @author ght
 * Mapper<KEYIN,VALUEIN,KEYOUT,VALUEOUT>
 * 4个泛型中,前两个是指定mapper输入数据的类型,KEYIN是输入的key的类型,VALUEIN是输入的value的类型
 * map和reduce的数据输入输出都是以key-value对的形式封装的
 * 默认情况下,框架传递给我们的mapper的输入数据中,key是要处理的文本中一行的起始偏移量,这一行的内容作为value
 * 由于内容要在网络中传递 所以参数必须是可序列化的  同时Long String 都实现了jdk的Serializable接口
 * 但由于效率较低 所以hadoop封装了高性能的可序列化的对象
 * Long   ->  LongWritable
 * String ->  Text
 */
@SuppressWarnings("all")
public class WcMapper extends Mapper<LongWritable,Text,Text,LongWritable>{
    /**
     * mapreduce框架每读一行数据就调用一次该方法
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //具体的业务逻辑就写在该方法体内 而且我们业务要处理的数据已经被框架传递进来,在方法的参数中key-value
        //key是这一行数据的起始偏移量,value这一行的文本内容

        //将值先转换为string处理
        String line = value.toString();
        //对这一行的文本根据空格来切分单词
        String[] word = StringUtils.split(line, ' ');
        //遍历这个单词数组输出为KV形式 k:单词 v:1
        for (String s : word) {
            context.write(new Text(s),new LongWritable(1));
        }

    }
}
