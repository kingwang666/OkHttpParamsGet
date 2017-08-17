package com.wang.okhttpparamsget.builder;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/3/7.
 */
public class ParamsFilePartBuilder extends BaseBuilder {

    public ParamsFilePartBuilder() {
        super("getParts", "parts");
    }

    @Override
    protected String getMethodType() {
        return "java.util.List<MultipartBody.Part> ";
    }

    private String getParamsType(){
        return "java.util.ArrayList<>";
    }

    @Override
    protected String getValueType() {
        return "okhttp3.MultipartBody.Part";
    }

    private String getRequestBody() {
        return "okhttp3.RequestBody";
    }

    private String getMediaType(){
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
                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(file.key, file.filename, ")
                            .append(getRequestBody()).append(".create(").append(getMediaType()).append(".parse(guessMimeType(file.filename)), file.file)));}}");
                } else if (findPostFile(modifiers)) {
                    sb.append("if (").append(field.getName()).append("!=null){");
                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(").append(field.getName()).append(".key,")
                            .append(field.getName()).append(".filename,").append(getRequestBody()).append(".create(").append(getMediaType()).append(".parse(guessMimeType(")
                            .append(field.getName()).append(".filename)),").append(field.getName()).append(".file)));}");
                } else {
                    sb.append(mFieldName).append(".add(").append(getValueType()).append(".createFormData(\"").append(field.getName()).append("\", String.valueOf(").append(field.getName()).append(")));");
                }
            }
        }
        sb.append("return ").append(mFieldName).append(";}");
        return sb.toString();
    }
}
