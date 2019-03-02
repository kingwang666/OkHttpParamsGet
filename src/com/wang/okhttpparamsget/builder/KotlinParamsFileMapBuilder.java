package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.wang.okhttpparamsget.Constant;
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
        return "HashMap<String, RequestBody>";
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
        return new String[]{"okhttp3.MediaType", "okhttp3.RequestBody"};
    }

    @Override
    protected void buildMethodBody(KtClass ktClass, KtClassBody body, KtLightClass lightClass, PsiField[] fields, boolean needAll, StringBuilder sb) {
        for (PsiField field : fields) {
            PsiElement older = null;
            if (field instanceof KtLightField) {
                older = ((KtLightField) field).getKotlinOrigin();
            }
            if (!findIgnore(older == null ? field : older)) {
                if (findPostFiles(older == null ? field : older)) {
                    String prefix = "it";
                    String[] fileInfo = getFileInfo(field, prefix, true, false);
                    if (fileInfo == null){
                        continue;
                    }
                    sb.append(field.getName()).append(isNullable(field) ? "?.forEach{\n" : ".forEach{\n");
                    sb.append(mFieldName).append("[").append(fileInfo[1]).append(" + \"\\\"; filename=\\\"\" + ").append(fileInfo[2]).append("] = ")
                            .append(getValueType()).append(".create(").append(getMediaType()).append(".parse(").append(fileInfo[3]).append("), ").append(fileInfo[4]).append(")\n");
                    sb.append("}\n");
                } else if (findPostFile(older == null ? field : older)) {
                    boolean nullable = isNullable(field);
                    String prefix = nullable ? "it" : field.getName();
                    String[] fileInfo = getFileInfo(field, prefix, false, false);
                    if (fileInfo == null){
                        continue;
                    }
                    if (nullable) {
                        sb.append(field.getName()).append("?.also{\n");
                    }
                    sb.append(mFieldName).append("[").append(fileInfo[1]).append(" + \"\\\"; filename=\\\"\" + ").append(fileInfo[2]).append("] = ")
                            .append(getValueType()).append(".create(").append(getMediaType()).append(".parse(").append(fileInfo[3]).append("),").append(fileInfo[4]).append(")\n");
                    if (nullable){
                        sb.append("}\n");
                    }
                } else if (isNullable(field)){
                    addNullableValue(field, sb);
                }else {
                    sb.append(mFieldName).append("[").append("\"").append(field.getName()).append("\"] = ").append(getValueType())
                            .append(".create(").append(getMediaType()).append(".parse(\"text/plain\"), ").append(toSting(field, false, null)).append(")\n");
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
                    .append(".create(").append(getMediaType()).append(".parse(\"text/plain\"), ").append(toSting(field, false, "it")).append(")\n}\n");
        }else {
            sb.append(mFieldName).append("[").append("\"").append(field.getName()).append("\"] = ").append(getValueType())
                    .append(".create(").append(getMediaType()).append(".parse(\"text/plain\"), ").append(toSting(field, true, null)).append(" ?: \"\" )\n");
        }
    }
}
