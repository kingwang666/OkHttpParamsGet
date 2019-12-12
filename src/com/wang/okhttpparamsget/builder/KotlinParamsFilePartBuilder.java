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
                String defaultKey;
                if ((defaultKey = getPostFileKey(older == null ? field : older)) != null) {
                    boolean nullable = isNullable(field);
                    FileInfo fileInfo = getFileInfo(field, nullable ? FileInfo.KOTLIN_CHILD : field.getName(), false);
                    if (fileInfo == null) {
                        continue;
                    }

                    if (fileInfo.isNorm() && nullable) {
                        sb.append(field.getName()).append("?.also{\n");
                    } else if (fileInfo.isListOrArray()) {
                        sb.append(field.getName()).append(nullable ? "?." : ".").append("forEach{\n");
                    } else if (fileInfo.isMap()) {
                        sb.append(field.getName()).append(nullable ? "?." : ".").append("forEach{ (key, value) ->\n");
                    }

                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(").append(defaultKey.isEmpty() || fileInfo.isMap() ? fileInfo.key : defaultKey).append(",")
                            .append(fileInfo.filename).append(",")
                            .append(getRequestBody()).append(".create(").append(getMediaType()).append(".parse(").append(fileInfo.mimeType).append("),").append(fileInfo.data).append(")))\n");

                    if (nullable || !fileInfo.isNorm()) {
                        sb.append("}\n");
                    }
                } else if (isNullable(field)) {
                    defaultKey = getParamName(older == null ? field : older);
                    addNullableValue(field, sb, defaultKey);
                } else {
                    defaultKey = getParamName(older == null ? field : older);
                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(");
                    if (defaultKey == null) {
                        sb.append('"').append(field.getName()).append('"');
                    } else {
                        sb.append(defaultKey);
                    }
                    sb.append(", ").append(toString(field, false, null)).append("))\n");
                }
            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb, String defaultName) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add) {
            sb.append(field.getName()).append("?.also{\n");
            sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append(", ").append(toString(field, false, "it")).append("))\n}\n");
        } else {
            sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append(", ").append(toString(field, true, null)).append(" ?: \"\"))\n");
        }
    }
}
