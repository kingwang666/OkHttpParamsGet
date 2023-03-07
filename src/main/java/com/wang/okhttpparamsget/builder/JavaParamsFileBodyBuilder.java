package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.wang.okhttpparamsget.Constant;
import com.wang.okhttpparamsget.data.FileInfo;
import org.jetbrains.kotlin.asJava.elements.KtLightField;

import javax.annotation.Nullable;

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


    @Override
    protected void buildMethodBody(PsiClass psiClass, PsiField[] fields, boolean needAll, StringBuilder sb) {
        if (needAll) {
            sb.append(mFieldName).append(".setType(MultipartBody.FORM);");
        }
        for (PsiField field : fields) {
            PsiElement older = null;
            if (field instanceof KtLightField) {
                older = ((KtLightField) field).getKotlinOrigin();
            }
            if (!findIgnore(older == null ? field : older)) {
                String defaultKey;
                if ((defaultKey = getPostFileKey(older == null ? field : older)) != null) {
                    FileInfo fileInfo = getFileInfo(field, field.getName(), true);
                    if (fileInfo == null) {
                        continue;
                    }
                    boolean nullable;
                    if (nullable = isNullable(field)) {
                        sb.append("if (").append(field.getName()).append("!=null){");
                    }
                    if (fileInfo.isListOrArray()) {
                        sb.append("for (").append(fileInfo.className).append(" ").append(FileInfo.LIST_CHILD).append(" : ").append(field.getName()).append(") {");
                    } else if (fileInfo.isMap()) {
                        sb.append("for (").append(fileInfo.className).append(" ").append(FileInfo.MAP_CHILD).append(" : ").append(field.getName()).append(".entrySet()) {");
                    }

                    sb.append(mFieldName).append(".addFormDataPart(").append(defaultKey.isEmpty() || fileInfo.isMap() ? fileInfo.key : defaultKey).append(",")
                            .append(fileInfo.filename).append(",");
                    createRequestBody(sb, fileInfo.mimeType, fileInfo.data, true);
                    sb.append(");");

                    if (!fileInfo.isNorm()) {
                        sb.append("}");
                    }

                    if (nullable) {
                        sb.append("}");
                    }
                } else if (isNullable(field)) {
                    defaultKey = getParamName(older == null ? field : older);
                    addNullableValue(field, sb, defaultKey);
                } else {
                    defaultKey = getParamName(older == null ? field : older);
                    sb.append(mFieldName).append(".addFormDataPart(");
                    if (defaultKey == null) {
                        sb.append('"').append(field.getName()).append('"');
                    } else {
                        sb.append(defaultKey);
                    }
                    sb.append(", ").append(toString(field)).append(");");
                }
            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb, @Nullable String defaultName) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add) {
            sb.append("if (").append(field.getName()).append(" != null){");
            sb.append(mFieldName).append(".addFormDataPart(");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append(", ").append(toString(field)).append(");}");
        } else {
            sb.append(mFieldName).append(".addFormDataPart(");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append(", ").append(field.getName()).append(" == null ? \"\" : ").append(toString(field)).append(");");
        }
    }
}
