package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiClass;
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
class KotlinParamsFilePartBuilder extends KotlinBuilder {

    public KotlinParamsFilePartBuilder() {
        super("getParts", "parts");
    }

    @Override
    protected String getMethodType() {
        return "MutableList<MultipartBody.Part> ";
    }

    protected String getParamsType() {
        return "ArrayList<MultipartBody.Part>";
    }

    @Override
    protected String getValueType() {
        return "MultipartBody.Part";
    }

    private String getRequestBody() {
        return "RequestBody";
    }

    private String getMediaType() {
        return "MediaType";
    }

    @Nullable
    @Override
    protected String[] getImports() {
        return new String[]{"okhttp3.MultipartBody", "okhttp3.RequestBody", "okhttp3.MediaType"};
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
                    if (fileInfo == null) {
                        continue;
                    }
                    sb.append(field.getName()).append(isNullable(field) ? "?.forEach{\n" : ".forEach{\n");
                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(").append(fileInfo[1]).append(", ").append(fileInfo[2]).append(", ")
                            .append(getRequestBody()).append(".create(").append(getMediaType()).append(".parse(").append(fileInfo[3]).append("), ").append(fileInfo[4]).append(")))\n}\n");
                } else if (findPostFile(older == null ? field : older)) {
                    boolean nullable = isNullable(field);
                    String prefix = nullable ? "it" : field.getName();
                    String[] fileInfo = getFileInfo(field, prefix, false, false);
                    if (fileInfo == null) {
                        continue;
                    }
                    if (nullable) {
                        sb.append(field.getName()).append("?.also{\n");
                    }
                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(").append(fileInfo[1]).append(",")
                            .append(fileInfo[2]).append(",").append(getRequestBody()).append(".create(").append(getMediaType()).append(".parse(")
                            .append(fileInfo[3]).append("),").append(fileInfo[4]).append(")))\n");
                    if (nullable) {
                        sb.append("}\n");
                    }
                } else if (isNullable(field)) {
                    addNullableValue(field, sb);
                } else {
                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(\"").append(field.getName()).append("\", ").append(toSting(field, false, null)).append("))\n");
                }
            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add) {
            sb.append(field.getName()).append("?.also{\n");
            sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(\"").append(field.getName()).append("\", ").append(toSting(field, false, "it")).append("))\n}\n");
        } else {
            sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(\"").append(field.getName()).append("\", ").append(toSting(field, true, null)).append(" ?: \"\"))\n");
        }
    }
}
