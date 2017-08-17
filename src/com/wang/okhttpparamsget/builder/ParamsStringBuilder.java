package com.wang.okhttpparamsget.builder;

import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/3/6.
 */
public class ParamsStringBuilder extends BaseBuilder {

    public ParamsStringBuilder() {
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

    private String getParamsType(){
        return "java.util.HashMap<>";
    }


    @Override
    protected String buildMethod(PsiClass psiClass, boolean isOverride, boolean needAll) {
        StringBuilder sb = new StringBuilder();
        sb.append("public ").append(getMethodType()).append(mMethodName).append("(){");
        PsiField[] fields;
        if (isOverride && !needAll) {
            sb.append(getMethodType()).append(mFieldName).append("=super.").append(mMethodName).append("();");
            fields = psiClass.getFields();
        } else {
            sb.append(getMethodType()).append(mFieldName).append("=new ").append(getParamsType()).append("();");
            fields = psiClass.getAllFields();
        }
        for (PsiField field : fields) {
            PsiModifierList modifiers = field.getModifierList();
            if (!findIgnore(modifiers)) {
                sb.append(mFieldName).append(".put(").append("\"").append(field.getName()).append("\"").append(",String.valueOf(").append(field.getName()).append("));");
            }
        }
        sb.append("return ").append(mFieldName).append(";}");
        return sb.toString();
    }

}
