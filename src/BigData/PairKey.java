package BigData;


import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.conf.Configuration;
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
import org.apache.hadoop.util.GenericOptionsParser;

public class PairKey implements WritableComparable<PairKey>{
    private int first;
    private int second;

    public PairKey(){}
    public PairKey(int a, int b){
        set(a,b);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(first);
        dataOutput.writeInt(second);

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        first = dataInput.readInt();
        second = dataInput.readInt();
    }

    public int getFirst(){
        return first;
    }

    public int getSecond(){
        return second;
    }

    public void set(int a, int b){
        if(a<b){
            this.first =a;
            this.second=b;
        }
        else{
            this.first =b;
            this.second=a;
        }
    }

    @Override
    public int compareTo(PairKey o) {
       if( this.first==o.first)
       {
           return this.second - o.second;
       }
       else return this.first - o.first;
    }
}
