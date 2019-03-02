package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.wang.okhttpparamsget.Constant;
import org.jetbrains.kotlin.asJava.elements.KtLightField;

/**
 * Created by wang on 2017/3/7.
 */
class JavaParamsFileBodyBuilder extends JavaBuilder {

    public JavaParamsFileBodyBuilder() {
        super("getBody", "builder");
    }

    @Override
    protected String getMethodType() {
        return "okhttp3.MultipartBody.Builder ";
    }

    @Override
    protected String getValueType() {
        return "okhttp3.RequestBody";
    }

    @Override
    protected String getParamsType() {
        return "okhttp3.MultipartBody.Builder";
    }

    private String getMediaType() {
        return "okhttp3.MediaType";
    }

    @Override
    protected void buildMethodBody(PsiClass psiClass, PsiField[] fields, boolean needAll, StringBuilder sb) {
        if (needAll){
            sb.append(mFieldName).append(".setType(MultipartBody.FORM);");
        }
        for (PsiField field : fields) {
            PsiElement older = null;
            if (field instanceof KtLightField) {
                older = ((KtLightField) field).getKotlinOrigin();
            }
            if (!findIgnore(older == null ? field : older)) {
                if (findPostFiles(older == null ? field : older)) {
                    String prefix = "file";
                    String[] fileInfo = getFileInfo(field, prefix, true, true);
                    if (fileInfo == null) {
                        continue;
                    }
                    boolean nullable;
                    if (nullable = isNullable(field)) {
                        sb.append("if (").append(field.getName()).append("!=null){");
                    }
                    sb.append("for (").append(fileInfo[0]).append(" ").append(prefix).append(" : ").append(field.getName()).append(") {");
                    sb.append(mFieldName).append(".addFormDataPart(").append(fileInfo[1]).append(", ").append(fileInfo[2]).append(", ")
                            .append(getValueType()).append(".create(").append(getMediaType()).append(".parse(").append(fileInfo[3]).append("), ").append(fileInfo[4]).append("));}");
                    if (nullable) {
                        sb.append("}");
                    }

                } else if (findPostFile(older == null ? field : older)) {
                    String prefix = field.getName();
                    String[] fileInfo = getFileInfo(field, prefix, false, true);
                    if (fileInfo == null) {
                        continue;
                    }
                    boolean nullable;
                    if (nullable = isNullable(field)) {
                        sb.append("if (").append(field.getName()).append("!=null){");
                    }
                    sb.append(mFieldName).append(".addFormDataPart(").append(fileInfo[1]).append(",")
                            .append(fileInfo[2]).append(",").append(getValueType()).append(".create(").append(getMediaType()).append(".parse(")
                            .append(fileInfo[3]).append("),").append(fileInfo[4]).append("));");
                    if (nullable) {
                        sb.append("}");
                    }
                } else if (isNullable(field)) {
                    addNullableValue(field, sb);
                } else {
                    sb.append(mFieldName).append(".addFormDataPart(\"").append(field.getName()).append("\", ").append(toSting(field)).append(");");
                }
            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add){
            sb.append("if (").append(field.getName()).append(" != null){");
            sb.append(mFieldName).append(".addFormDataPart(\"").append(field.getName()).append("\", ").append(toSting(field)).append(");}");
        }else {
            sb.append(mFieldName).append(".addFormDataPart(\"").append(field.getName()).append("\", ").append(field.getName()).append(" == null ? \"\" : ").append(toSting(field)).append(");");
        }
    }
}
