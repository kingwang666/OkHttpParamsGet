package com.wang.okhttpparamsget.builder;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.psi.PsiFile;
import com.wang.okhttpparamsget.Constant;
import org.jetbrains.kotlin.idea.KotlinFileType;

import javax.annotation.Nullable;

public class BuilderFactory {

    @Nullable
    public static IBuilder getParamsBuilder(int type, PsiFile psiFile) {
        if (psiFile == null) return null;
        if (psiFile.getFileType() instanceof JavaFileType) {
            return getJavaBuilder(type);
        } else if (psiFile.getFileType() instanceof KotlinFileType) {
            return getKotlinBuilder(type);
        }
        return null;
    }

    @Nullable
    private static IBuilder getJavaBuilder(int type) {
        switch (type) {
            case Constant.TYPE_MAP_STRING:
                return new JavaParamsStringBuilder();
            case Constant.TYPE_MAP_OBJECT:
                return new JavaParamsObjectBuilder();
            case Constant.TYPE_MAP_BODY:
                return new JavaParamsFileMapBuilder();
            case Constant.TYPE_LIST_PART:
                return new JavaParamsFilePartBuilder();
            case Constant.TYPE_BODY_BUILDER:
                return new JavaParamsFileBodyBuilder();
        }
        return null;
    }

    @Nullable
    private static IBuilder getKotlinBuilder(int type) {
        switch (type) {
            case Constant.TYPE_MAP_STRING:
                return new KotlinParamsStringBuilder();
            case Constant.TYPE_MAP_OBJECT:
                return new KotlinParamsObjectBuilder();
            case Constant.TYPE_MAP_BODY:
                return new KotlinParamsFileMapBuilder();
            case Constant.TYPE_LIST_PART:
                return new KotlinParamsFilePartBuilder();
            case Constant.TYPE_BODY_BUILDER:
                return new KotlinParamsFileBodyBuilder();
        }
        return null;
    }
}
