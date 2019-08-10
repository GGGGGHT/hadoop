package com.ggggght.hadoop.wordcount;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 *
 */
public class WcReduce extends Reducer<Text,LongWritable,Text,LongWritable>{
    /**
     *   框架在map处理完成之后,将所有kv对缓存起来,进行分组,然后传递一个组<key,values{}>,
     * 调用一次reduce方法
     * @param key
     * @param values
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
        long count = 0;
        //遍历value的list,进行累加求和
        for (LongWritable value : values) {
            count += value.get();
        }
        //输出这一个单词的统计结果
        context.write(key, new LongWritable(count));
    }
}
