package com.wang.okhttpparamsget.builder;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/3/7.
 */
public class ParamsFileMapBuilder extends BaseBuilder {

    public ParamsFileMapBuilder() {
        super("getParams", "params");
    }

    @Override
    protected String getMethodType() {
        return "java.util.Map<String, RequestBody> ";
    }

    private String getParamsType() {
        return "java.util.HashMap<>";
    }

    @Override
    protected String getValueType() {
        return "okhttp3.RequestBody";
    }

    private String getMediaType() {
        return "okhttp3.MediaType";
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
                if (findPostFiles(modifiers)) {
                    sb.append("if (").append(field.getName()).append("!=null&&").append(field.getName()).append(".size()>0){");
                    sb.append("for (FileInput file : ").append(field.getName()).append(") {");
                    sb.append(mFieldName).append(".put(file.key + \"\\\"; filename=\\\"\" + file.filename,")
                            .append(getValueType()).append(".create(").append(getMediaType()).append(".parse(guessMimeType(file.filename)), file.file));}}");
                } else if (findPostFile(modifiers)) {
                    sb.append("if (").append(field.getName()).append("!=null){");
                    sb.append(mFieldName).append(".put(").append(field.getName()).append(".key + \"\\\"; filename=\\\"\" + ").append(field.getName())
                            .append(".filename, ").append(getValueType()).append(".create(").append(getMediaType()).append(".parse(guessMimeType(")
                            .append(field.getName()).append(".filename)),").append(field.getName()).append(".file));}");
                } else {
                    sb.append(mFieldName).append(".put(").append("\"").append(field.getName()).append("\", ").append(getValueType())
                            .append(".create(").append(getMediaType()).append(".parse(\"text/plain\"), String.valueOf(").append(field.getName()).append(")));");
                }
            }
        }
        sb.append("return ").append(mFieldName).append(";}");
        return sb.toString();
    }
}
