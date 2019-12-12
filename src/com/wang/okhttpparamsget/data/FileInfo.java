package com.wang.okhttpparamsget.data;

public class FileInfo {

    public static final int NORMAL = 0;
    public static final int ARRAY = 1;
    public static final int LIST = 2;
    public static final int MAP = 3;

    public static final String MAP_CHILD = "entry";
    public static final String LIST_CHILD = "file";
    public static final String KOTLIN_CHILD = "it";

    public final int type;

    public final String className;

    public String key;

    public String filename;

    public String mimeType;

    public String data;

    public FileInfo(String className, String key, String filename, String mimeType, String data) {
        this(NORMAL, className, key, filename, mimeType, data);
    }

    public FileInfo(int type, String className, String key, String filename, String mimeType, String data) {
        this.type = type;
        this.className = className;
        this.key = key;
        this.filename = filename;
        this.mimeType = mimeType;
        this.data = data;
    }

    public boolean isMap() {
        return type == MAP;
    }

    public boolean isListOrArray() {
        return type == LIST || type == ARRAY;
    }

    public boolean isNorm() {
        return type == NORMAL;
    }
}
