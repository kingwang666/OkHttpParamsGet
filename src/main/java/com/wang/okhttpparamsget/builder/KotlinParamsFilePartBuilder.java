package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.wang.okhttpparamsget.Constant;
import com.wang.okhttpparamsget.data.FileInfo;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.asJava.classes.KtLightClass;
import org.jetbrains.kotlin.asJava.elements.KtLightField;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtClassBody;


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

    @Nullable
    @Override
    protected String[] getImports() {
        boolean version4 = PropertiesComponent.getInstance().getBoolean(Constant.OKHTTP_VERSION, true);
        if (version4) {
            return new String[]{"okhttp3.MultipartBody",
                    "okhttp3.RequestBody",
                    "okhttp3.MediaType",
                    "okhttp3.MediaType.Companion.toMediaTypeOrNull",
                    "okhttp3.RequestBody.Companion.asRequestBody"};
        }
        return new String[]{"okhttp3.MultipartBody",
                "okhttp3.RequestBody",
                "okhttp3.MediaType"};
    }

    @Override
    protected void buildMethodBody(KtClass ktClass, KtClassBody body, KtLightClass lightClass, PsiField[] fields, boolean needAll, StringBuilder sb) {
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
                        .append(fileInfo.filename).append(",");
                createRequestBody(sb, fileInfo.mimeType, fileInfo.data, true);
                sb.append("))\n");

                if (nullable || !fileInfo.isNorm()) {
                    sb.append("}\n");
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
                sb.append(", ").append(toString(field, false, null)).append("))\n");
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
