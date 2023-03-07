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

    @Nullable
    @Override
    protected String[] getImports() {
        boolean version4 = PropertiesComponent.getInstance().getBoolean(Constant.OKHTTP_VERSION, true);
        if (version4){
            if (!PropertiesComponent.getInstance().getBoolean(Constant.ARRAY_MAP, true)) {
                return new String[]{"okhttp3.MediaType",
                        "okhttp3.RequestBody",
                        "okhttp3.MediaType.Companion.toMediaTypeOrNull",
                        "okhttp3.RequestBody.Companion.asRequestBody",
                        "okhttp3.RequestBody.Companion.toRequestBody"};
            }
            return new String[]{"okhttp3.MediaType",
                    "okhttp3.RequestBody",
                    PropertiesComponent.getInstance().getBoolean(Constant.ANDROIDX, true) ? "androidx.collection.ArrayMap" : "android.support.v4.util.ArrayMap",
                    "okhttp3.MediaType.Companion.toMediaTypeOrNull",
                    "okhttp3.RequestBody.Companion.asRequestBody",
                    "okhttp3.RequestBody.Companion.toRequestBody"};
        }
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
                String defaultKey;
                if ((defaultKey = getPostFileKey(older == null ? field : older)) != null) {
                    boolean nullable = isNullable(field);
                    FileInfo fileInfo = getFileInfo(field, nullable ? FileInfo.KOTLIN_CHILD : field.getName(), false);

                    if (fileInfo == null) {
                        continue;
                    }
                    if (defaultKey.isEmpty() || fileInfo.isMap()) {
                        defaultKey = fileInfo.key.replace(".toString()", "");
                    }

                    if (fileInfo.isNorm() && nullable) {
                        sb.append(field.getName()).append("?.also{\n");
                    } else if (fileInfo.isListOrArray()) {
                        sb.append(field.getName()).append(nullable ? "?." : ".").append("forEach{\n");
                    } else if (fileInfo.isMap()) {
                        sb.append(field.getName()).append(nullable ? "?." : ".").append("forEach{ (key, value) ->\n");
                    }
                    boolean string = defaultKey.matches("\".+?\"");
                    int index = defaultKey.indexOf('.');

                    sb.append(mFieldName).append("[");
                    if (string) {
                        sb.append(defaultKey, 0, defaultKey.length() - 1).append("\\\"");
                    } else if (index > 0) {
                        sb.append("\"${").append(defaultKey).append("}\\\"");
                    } else {
                        sb.append("\"$").append(defaultKey).append("\\\"");
                    }
                    sb.append("; filename=\\\"${").append(fileInfo.filename).append("}\"] = ");
                    createRequestBody(sb, fileInfo.mimeType, fileInfo.data, true);
                    sb.append('\n');

                    if (nullable || !fileInfo.isNorm()) {
                        sb.append("}\n");
                    }
                } else if (isNullable(field)) {
                    defaultKey = getParamName(older == null ? field : older);
                    addNullableValue(field, sb, defaultKey);
                } else {
                    defaultKey = getParamName(older == null ? field : older);
                    sb.append(mFieldName).append("[");
                    if (defaultKey == null) {
                        sb.append('"').append(field.getName()).append('"');
                    } else {
                        sb.append(defaultKey);
                    }
                    sb.append("] = ");
                    createRequestBody(sb, "\"text/plain\"", toString(field, false, null));
                    sb.append('\n');
                }
            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb, String defaultName) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add) {
            sb.append(field.getName()).append("?.also{\n");
            sb.append(mFieldName).append("[");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append("] = ");
            createRequestBody(sb, "\"text/plain\"", toString(field, false, "it"));
            sb.append("\n}\n");
        } else {
            sb.append(mFieldName).append("[");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append("] = ");
            createRequestBody(sb, "\"text/plain\"", "(" + toString(field, true, null) + " ?: \"\")");
            sb.append('\n');
        }
    }
}
