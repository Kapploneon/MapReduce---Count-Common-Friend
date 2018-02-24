package BigData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.output   .TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;

public class TopPairs extends Configured implements org.apache.hadoop.util.Tool {
    @Override
    public int run(String args[]) throws Exception{
        Configuration conf = new Configuration();
        Job job = null;
        try {
            job = Job.getInstance(conf,"FriendList");
        } catch (IOException e) {
            e.printStackTrace();
        }
        job.setJarByClass(TopPairs.class);

        Path in = new Path(args[0]);
        Path out = new Path(args[1]);

        FileInputFormat.setInputPaths(job,in);
        FileOutputFormat.setOutputPath(job,out);

        job.setNumReduceTasks(1);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);


        job.setMapOutputKeyClass(PairKey.class);
        job.setMapOutputValueClass(IntArrayWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.waitForCompletion(true);
      //  -------------------------------------------------

        Configuration conf2 = new Configuration();

        Job job2 = null;
        try {
            job2 = Job.getInstance(conf2,"TopPairs");
        } catch (IOException e) {
            e.printStackTrace();
        }
        job2.setJarByClass(TopPairs.class);

        Path secondInput = new Path(args[2]);
     //   Path finalOutput = new Path(args[3]);

        MultipleInputs.addInputPath(job2, out, KeyValueTextInputFormat.class, TopPairs.Map2.class);
      //  FileInputFormat.setInputPaths(job2,secondInput);
        FileOutputFormat.setOutputPath(job2,secondInput);

        job2.setMapperClass(TopPairs.Map2.class);
        job2.setReducerClass(TopPairs.Reduce2.class);

        job2.setOutputFormatClass(TextOutputFormat.class);

        job2.setMapOutputKeyClass(BigData.CustomInt.class);
        job2.setMapOutputValueClass(PairKey.class);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);

        System.exit(job2.waitForCompletion(true)?0:1);

        return 0;
    }

    public static class Map2 extends Mapper<Text, Text, CustomInt, PairKey>{

        public void map(Text key, Text value, Context context) throws IOException, InterruptedException{
            String[] line=key.toString().split("\\s+");
         //   String[] num = line.split
         //   System.out.println("First: "+line[0]+"Second: "+line[1]+"Count: "+value.toString());
            System.out.println("First: "+line[0]);
            System.out.println("Count: "+value.toString());

            CustomInt cInt = new CustomInt();
            cInt.setNum(Integer.parseInt(value.toString()));
            PairKey pair = new PairKey(Integer.parseInt(line[0]),Integer.parseInt(line[1]));

                context.write(cInt,pair);
            }
        }

    public static class Reduce2 extends Reducer<CustomInt, PairKey, Text, Text>{
        int count=0;
        public void reduce(CustomInt key, Iterable<PairKey> values, Context context) throws IOException, InterruptedException {
            for (PairKey pair:values
                 ) {
                if(count<10){
                    String str1 = pair.getFirst() + "\t" +pair.getSecond();
                    String str2 = String.valueOf(key.getNum());
                    System.out.println("Pair: "+str1+"Count: "+str2);
                    context.write(new Text(str1), new Text(str2));
                }
               count++;
            }
        }
    }

            public static class Map extends Mapper<LongWritable, Text, PairKey, IntArrayWritable>{

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
            String[] line=value.toString().split("\\s+");
            if(line.length>1){
            String[] friendList=line[1].split(",");
            IntWritable [] tFriendList = new IntWritable[friendList.length];

            for(int i=0; i<friendList.length;i++){

                tFriendList[i] = new IntWritable(Integer.parseInt(friendList[i]));
                System.out.println("Tuple Array member" +tFriendList[i]);
            }
            // tFriendList.clone(friendList);

            //  Writable[] flist = new Writable(friendList);
            IntArrayWritable clist = new IntArrayWritable(tFriendList);
            // for (IntWritable a:clist.values
            //       ) {
            //       System.out.println("Tuple Values "+ a);
            // }

            for (String friend:friendList
                    ) {
                PairKey pair = new PairKey(Integer.parseInt(line[0]),Integer.parseInt(friend));
                System.out.println(" Mapper- pair :"+pair.getFirst()+" "+pair.getSecond());
                context.write(pair,clist);
            }
          }
        }
    }

    public static class Reduce extends Reducer<PairKey, IntArrayWritable, Text, Text>{
        int count =0;
        IntWritable[] firstList;
        public void reduce(PairKey key, Iterable<IntArrayWritable> values, Context context) throws IOException, InterruptedException{
            for (IntArrayWritable arr:values
                    ) {
                //    Object [] objectArray = (Object[]) arr.toArray();
            //    System.out.println("Hello Reducer :"+count);
                if(count==0){

                    //   firstList = Arrays.copyOf(objectArray, objectArray.length, IntWritable[].class);
                    firstList = (IntWritable[]) arr.toArray();
                    count++;
                }
                else{
                    count=0;
                    IntWritable[] secondList = (IntWritable[]) arr.toArray();

                    Set<IntWritable> s1 = new HashSet<IntWritable>(Arrays.asList(firstList));
                    Set<IntWritable> s2 = new HashSet<IntWritable>(Arrays.asList(secondList));
                    s1.retainAll(s2);
                    IntWritable[] val = new IntWritable[s1.size()];
                    //     IntWritable[] val = (IntWritable[]) s1.toArray();
                    int i=0;
                    String temp ="";
                    for (IntWritable a:s1
                            ) {
                        if(i==0)
                            temp = String.valueOf(a);
                        else
                            temp = temp +","+a;
                        val[i++]=a;
                      //  System.out.println("Value "+val[i-1].toString());
                    }

                    int size = val.length;
                    String sizeStr = String.valueOf(size);
                    IntArrayWritable   finalResult  = new IntArrayWritable(val);
                    //    Text tresult = new Text(finalResult.toString());
                    Text tresult= new Text(temp);


                    String str = key.getFirst() + " " +key.getSecond();
                    System.out.println(str+"Common Friend Count"+sizeStr);
                    context.write(new Text(str),new Text(sizeStr));
                }

            }

        }

    }
    //  String res = finalResult.toString();
    //   Text tres = new Text(res);







    public static void main (String[] args) throws Exception{
        int res = ToolRunner.run(new Configuration(), new TopPairs(),args);
        System.exit(res);
    }
}
