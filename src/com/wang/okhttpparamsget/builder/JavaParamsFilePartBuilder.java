package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.wang.okhttpparamsget.Constant;
import org.jetbrains.kotlin.asJava.elements.KtLightField;

/**
 * Created by wang on 2017/3/7.
 */
class JavaParamsFilePartBuilder extends JavaBuilder {

    public JavaParamsFilePartBuilder() {
        super("getParts", "parts");
    }

    @Override
    protected String getMethodType() {
        return "java.util.List<MultipartBody.Part> ";
    }

    protected String getParamsType() {
        return "java.util.ArrayList<>";
    }

    @Override
    protected String getValueType() {
        return "okhttp3.MultipartBody.Part";
    }

    private String getRequestBody() {
        return "okhttp3.RequestBody";
    }

    private String getMediaType() {
        return "okhttp3.MediaType";
    }

    @Override
    protected void buildMethodBody(PsiClass psiClass, PsiField[] fields, boolean needAll, StringBuilder sb) {
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
                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(").append(fileInfo[1]).append(", ").append(fileInfo[2]).append(", ")
                            .append(getRequestBody()).append(".create(").append(getMediaType()).append(".parse(").append(fileInfo[3]).append("), ").append(fileInfo[4]).append(")));}");
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
                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(").append(fileInfo[1]).append(",")
                            .append(fileInfo[2]).append(",").append(getRequestBody()).append(".create(").append(getMediaType()).append(".parse(")
                            .append(fileInfo[3]).append("),").append(fileInfo[4]).append(")));");
                    if (nullable) {
                        sb.append("}");
                    }
                } else if (isNullable(field)) {
                    addNullableValue(field, sb);
                } else {
                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(\"").append(field.getName()).append("\", ").append(toSting(field)).append("));");
                }
            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add) {
            sb.append("if (").append(field.getName()).append(" != null){");
            sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(\"").append(field.getName()).append("\", ").append(toSting(field)).append("));}");
        } else {
            sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(\"").append(field.getName()).append("\", ")
                    .append(field.getName()).append(" == null ? \"\" : ").append(toSting(field)).append("));");
        }
    }
}
