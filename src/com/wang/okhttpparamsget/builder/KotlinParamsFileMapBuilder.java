package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.wang.okhttpparamsget.Constant;
import com.wang.okhttpparamsget.data.FileInfo;
import org.jetbrains.kotlin.asJava.classes.KtLightClass;
import org.jetbrains.kotlin.asJava.elements.KtLightField;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtClassBody;

import javax.annotation.Nullable;

/**
 * Created by wang on 2017/3/7.
 */
class KotlinParamsFileMapBuilder extends KotlinBuilder {

    public KotlinParamsFileMapBuilder() {
        super("getParams", "params");
    }

    @Override
    protected String getMethodType() {
        return "MutableMap<String, RequestBody> ";
    }

    @Override
    protected String getParamsType() {
        if (!PropertiesComponent.getInstance().getBoolean(Constant.ARRAY_MAP, true)) {
            return "HashMap<String, RequestBody>";
        }
        return "ArrayMap<String, RequestBody>";
    }

    @Override
    protected String getValueType() {
        return "RequestBody";
    }

    private String getMediaType() {
        return "MediaType";
    }


    @Nullable
    @Override
    protected String[] getImports() {
        if (!PropertiesComponent.getInstance().getBoolean(Constant.ARRAY_MAP, true)) {
            return new String[]{"okhttp3.MediaType",
                    "okhttp3.RequestBody"};
        }
        return new String[]{"okhttp3.MediaType",
                "okhttp3.RequestBody",
                PropertiesComponent.getInstance().getBoolean(Constant.ANDROIDX, true) ? "androidx.collection.ArrayMap" : "android.support.v4.util.ArrayMap"};
    }

    @Override
    protected void buildMethodBody(KtClass ktClass, KtClassBody body, KtLightClass lightClass, PsiField[] fields, boolean needAll, StringBuilder sb) {
        for (PsiField field : fields) {
            PsiElement older = null;
            if (field instanceof KtLightField) {
                older = ((KtLightField) field).getKotlinOrigin();
            }
            if (!findIgnore(older == null ? field : older)) {
                if (findPostFile(older == null ? field : older)) {
                    boolean nullable = isNullable(field);
                    FileInfo fileInfo = getFileInfo(field, nullable ? FileInfo.KOTLIN_CHILD : field.getName(), false);

                    if (fileInfo == null) {
                        continue;
                    }

                    fileInfo.key = fileInfo.key.replace(".toString()", "");

                    if (fileInfo.isNorm() && nullable) {
                        sb.append(field.getName()).append("?.also{\n");
                    } else if (fileInfo.isListOrArray()) {
                        sb.append(field.getName()).append(nullable ? "?." : ".").append("forEach{\n");
                    } else if (fileInfo.isMap()) {
                        sb.append(field.getName()).append(nullable ? "?." : ".").append("forEach{ (key, value) ->\n");
                    }
                    boolean string = fileInfo.key.matches("\".+?\"");
                    int index = fileInfo.key.indexOf('.');

                    sb.append(mFieldName).append("[");
                    if (string) {
                        sb.append(fileInfo.key, 0, fileInfo.key.length() - 1).append("\\\"");
                    } else if (index > 0) {
                        sb.append("\"${").append(fileInfo.key).append("}\\\"");
                    } else {
                        sb.append("\"$").append(fileInfo.key).append("\\\"");
                    }
                    sb.append("; filename=\\\"${").append(fileInfo.filename).append("}\"] = ")
                            .append(getValueType()).append(".create(").append(getMediaType()).append(".parse(").append(fileInfo.mimeType).append("),").append(fileInfo.data).append(")\n");

                    if (nullable || !fileInfo.isNorm()) {
                        sb.append("}\n");
                    }
                } else if (isNullable(field)) {
                    addNullableValue(field, sb);
                } else {
                    sb.append(mFieldName).append("[").append("\"").append(field.getName()).append("\"] = ").append(getValueType())
                            .append(".create(").append(getMediaType()).append(".parse(\"text/plain\"), ").append(toString(field, false, null)).append(")\n");
                }
            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add) {
            sb.append(field.getName()).append("?.also{\n");
            sb.append(mFieldName).append("[").append("\"").append(field.getName()).append("\"] = ").append(getValueType())
                    .append(".create(").append(getMediaType()).append(".parse(\"text/plain\"), ").append(toString(field, false, "it")).append(")\n}\n");
        } else {
            sb.append(mFieldName).append("[").append("\"").append(field.getName()).append("\"] = ").append(getValueType())
                    .append(".create(").append(getMediaType()).append(".parse(\"text/plain\"), ").append(toString(field, true, null)).append(" ?: \"\" )\n");
        }
    }
}
