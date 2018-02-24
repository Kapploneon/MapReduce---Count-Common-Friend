package BigData;
import java.io.*;
import java.net.URI;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;


import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.ToolRunner;

import javax.lang.model.SourceVersion;
import javax.tools.Tool;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;

public  class IntArrayWritable extends ArrayWritable {

    public IntArrayWritable(){
        super(IntWritable.class);
    }

    public IntArrayWritable(IntWritable[] intWritables) {
        super(IntWritable.class,intWritables);
    //    IntWritable [] intarr = new IntWritable[intWritables.length];
      /*  for(int i=0; i<intWritables.length;i++){
            intarr[i] = new IntWritable();
        }
        set(intarr);*/
    }

    @Override
    public IntWritable[] get() {
        return (IntWritable[]) super.get();
    }

    @Override
    public void write(DataOutput arg0) throws IOException{
        super.write(arg0);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : super.toStrings())
        {
            sb.append(s).append(" ");
        }
        return sb.toString();
    }


}
