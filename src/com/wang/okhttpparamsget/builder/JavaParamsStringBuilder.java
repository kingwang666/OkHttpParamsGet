package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.*;
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
    protected String getParamsType(){
        if (PropertiesComponent.getInstance().getBoolean(Constant.ANDROIDX, true)){
            return "androidx.collection.ArrayMap<>";
        }else {
            return "android.support.v4.util.ArrayMap<>";
        }
//        return "java.util.HashMap<>";
    }

    @Override
    protected void buildMethodBody(PsiClass psiClass, PsiField[] fields, boolean needAll, StringBuilder sb) {
        for (PsiField field : fields) {
            PsiElement older = null;
            if (field instanceof KtLightField) {
                older = ((KtLightField) field).getKotlinOrigin();
            }
            if (!findIgnore(older == null ? field : older)){
                if (isNullable(field)){
                    addNullableValue(field, sb);
                }else {
                    sb.append(mFieldName).append(".put(").append("\"").append(field.getName()).append("\"").append(", ").append(toSting(field)).append(");");
                }

            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add) {
            sb.append("if (").append(field.getName()).append(" != null){");
            sb.append(mFieldName).append(".put(").append("\"").append(field.getName()).append("\"").append(", ").append(toSting(field)).append(");}");
        }else {
            sb.append(mFieldName).append(".put(").append("\"").append(field.getName()).append("\"").append(", ").append(field.getName()).append(" == null ? \"\" : ").append(toSting(field)).append(");");
        }
    }

}
