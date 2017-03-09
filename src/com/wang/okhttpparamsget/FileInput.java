package com.wang.okhttpparamsget;

import java.io.File;

/**
 * Created by wang
 * on 2016/3/9
 */
public class FileInput {
    public String key;
    public String filename;
    public File file;

    public FileInput(String name, String filename, File file)
    {
        this.key = name;
        this.filename = filename;
        this.file = file;
    }

    @Override
    public String toString()
    {
        return "FileInput{" +
                "key='" + key + '\'' +
                ", filename='" + filename + '\'' +
                ", file=" + file +
                '}';
    }
}
