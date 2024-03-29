package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.wang.okhttpparamsget.Constant;
import org.jetbrains.kotlin.asJava.classes.KtLightClass;
import org.jetbrains.kotlin.asJava.elements.KtLightField;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtClassBody;


/**
 * Created by wang on 2017/3/6.
 */
class KotlinParamsStringBuilder extends KotlinBuilder {

    public KotlinParamsStringBuilder() {
        super("getParams", "params");
    }

    @Override
    protected String getMethodType() {
        return "MutableMap<String, String> ";
    }

    @Override
    protected String getValueType() {
        return "String";
    }

    @Override
    protected String getParamsType() {
        if (!PropertiesComponent.getInstance().getBoolean(Constant.ARRAY_MAP, true)) {
            return "HashMap<String, String>";
        }
        return "ArrayMap<String, String>";
    }

    @Override
    protected String[] getImports() {
        if (!PropertiesComponent.getInstance().getBoolean(Constant.ARRAY_MAP, true)) {
            return null;
        }
        return new String[]{PropertiesComponent.getInstance().getBoolean(Constant.ANDROIDX, true) ? "androidx.collection.ArrayMap" : "android.support.v4.util.ArrayMap"};
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
            String defaultName = getParamName(realField);
            if (isNullable(field)) {
                addNullableValue(field, sb, defaultName);
            } else {
                sb.append(mFieldName).append("[");
                if (defaultName == null) {
                    sb.append('"').append(field.getName()).append('"');
                } else {
                    sb.append(defaultName);
                }
                sb.append("] = ").append(toString(field, false, null)).append("\n");
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
            sb.append("] = ").append(toString(field, false, "it")).append("\n}\n");
        } else {
            sb.append(mFieldName).append("[");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append("] = ").append(toString(field, true, null)).append("?: \"\"\n");
        }
    }
}
