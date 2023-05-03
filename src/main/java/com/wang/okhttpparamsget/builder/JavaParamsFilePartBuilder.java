package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.wang.okhttpparamsget.Constant;
import com.wang.okhttpparamsget.data.FileInfo;
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
        return "java.util.List<" + getValueType() + "> ";
    }

    protected String getParamsType() {
        return "java.util.ArrayList<>";
    }

    @Override
    protected String getValueType() {
        return "okhttp3.MultipartBody.Part";
    }


    @Override
    protected void buildMethodBody(PsiClass psiClass, PsiField[] fields, boolean needAll, StringBuilder sb) {
        for (PsiField field : fields) {
            PsiElement older = null;
            if (field instanceof KtLightField) {
                older = ((KtLightField) field).getKotlinOrigin();
            }
            PsiElement realField = older == null ? field : older;
            if (isStatic(field) || findIgnore(realField)) {
                continue;
            }
            String defaultKey;
            if ((defaultKey = getPostFileKey(realField)) != null) {
                FileInfo fileInfo = getFileInfo(field, field.getName(), true);
                if (fileInfo == null) {
                    continue;
                }
                boolean nullable = isNullable(field);
                if (nullable) {
                    sb.append("if (").append(field.getName()).append("!=null){");
                }
                if (fileInfo.isListOrArray()) {
                    sb.append("for (").append(fileInfo.className).append(" ").append(FileInfo.LIST_CHILD).append(" : ").append(field.getName()).append(") {");
                } else if (fileInfo.isMap()) {
                    sb.append("for (").append(fileInfo.className).append(" ").append(FileInfo.MAP_CHILD).append(" : ").append(field.getName()).append(".entrySet()) {");
                }
                sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(")
                        .append(defaultKey.isEmpty() || fileInfo.isMap() ? fileInfo.key : defaultKey).append(",")
                        .append(fileInfo.filename).append(",");
                createRequestBody(sb, fileInfo.mimeType, fileInfo.data, true);
                sb.append("));");

                if (!fileInfo.isNorm()) {
                    sb.append("}");
                }

                if (nullable) {
                    sb.append("}");
                }
            } else if (isNullable(field)) {
                defaultKey = getParamName(realField);
                addNullableValue(field, sb, defaultKey);
            } else {
                defaultKey = getParamName(realField);
                sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(");
                if (defaultKey == null) {
                    sb.append('"').append(field.getName()).append('"');
                } else {
                    sb.append(defaultKey);
                }
                sb.append(", ").append(toString(field)).append("));");
            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb, String defaultName) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add) {
            sb.append("if (").append(field.getName()).append(" != null){");
            sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append(", ").append(toString(field)).append("));}");
        } else {
            sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append(", ").append(field.getName()).append(" == null ? \"\" : ").append(toString(field)).append("));");
        }
    }
}
