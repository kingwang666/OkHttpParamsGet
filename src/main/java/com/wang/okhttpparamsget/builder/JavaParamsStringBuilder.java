package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.wang.okhttpparamsget.Constant;
import org.jetbrains.kotlin.asJava.elements.KtLightField;


/**
 * Created by wang on 2017/3/6.
 */
class JavaParamsStringBuilder extends JavaBuilder {

    public JavaParamsStringBuilder() {
        super("getParams", "params");
    }

    @Override
    protected String getMethodType() {
        return "java.util.Map<String, String> ";
    }

    @Override
    protected String getValueType() {
        return "String";
    }

    @Override
    protected String getParamsType() {
        if (!PropertiesComponent.getInstance().getBoolean(Constant.ARRAY_MAP, true)) {
            return "java.util.HashMap<>";
        }
        if (PropertiesComponent.getInstance().getBoolean(Constant.ANDROIDX, true)) {
            return "androidx.collection.ArrayMap<>";
        } else {
            return "android.support.v4.util.ArrayMap<>";
        }
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
            String defaultName = getParamName(realField);
            if (isNullable(field)) {
                addNullableValue(field, sb, defaultName);
            } else {
                sb.append(mFieldName).append(".put(");
                if (defaultName == null) {
                    sb.append('"').append(field.getName()).append('"');
                } else {
                    sb.append(defaultName);
                }
                sb.append(", ").append(toString(field)).append(");");
            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb, String defaultName) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add) {
            sb.append("if (").append(field.getName()).append(" != null){");
            sb.append(mFieldName).append(".put(");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append(", ").append(toString(field)).append(");}");
        } else {
            sb.append(mFieldName).append(".put(");
            if (defaultName == null) {
                sb.append('"').append(field.getName()).append('"');
            } else {
                sb.append(defaultName);
            }
            sb.append(", ").append(field.getName()).append(" == null ? \"\" : ").append(toString(field)).append(");");
        }
    }

}
