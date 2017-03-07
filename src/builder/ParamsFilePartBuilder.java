package builder;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;

/**
 * Created by wang on 2017/3/7.
 */
public class ParamsFilePartBuilder extends BaseBuilder {

    public ParamsFilePartBuilder() {
        super("getParts");
    }

    @Override
    protected String getMethodType() {
        return "java.util.List<okhttp3.MultipartBody.Part>";
    }

    @Override
    protected String getValueType() {
        return "okhttp3.MultipartBody.Part";
    }

    private String getRequestBody(){
        return "okhttp3.RequestBody";
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
            sb.append(getMethodType()).append(mFieldName).append("=new java.util.ArrayList<>();");
            fields = psiClass.getAllFields();
        }
        sb.append(getRequestBody()).append(" requestBody;");
        for (PsiField field : fields) {
            PsiModifierList modifiers = field.getModifierList();
            if (modifiers == null || modifiers.findAnnotation("Ignore") == null) {
                if (modifiers != null && modifiers.findAnnotation("PostFiles") != null) {
                    sb.append("if (").append(field.getName()).append("!=null&&").append(field.getName()).append(".size()>0){");
                    sb.append("for (FileInput file : ").append(field.getName()).append(") {");
                    sb.append("requestBody = ").append(getRequestBody()).append(".create(okhttp3.MediaType.parse(guessMimeType(file.filename)), file.file);");
                    sb.append("params.add(").append(getValueType()).append(".createFormData(file.key, file.filename, requestBody));").append("}}");

                }else if (modifiers != null && modifiers.findAnnotation("PostFile") != null){
                    sb.append("if (").append(field.getName()).append("!=null){");
                    sb.append("requestBody = ").append(getRequestBody()).append(".create(okhttp3.MediaType.parse(guessMimeType(").append(field.getName()).append(".filename)),").append(field.getName()).append(".file);");
                    sb.append("params.add(").append(getValueType()).append(".createFormData(").append(field.getName()).append(".key,").append(field.getName()).append(".filename,").append("requestBody));").append("}");
                }
                else {
                    sb.append("params.add(").append(getValueType()).append(".createFormData(\"").append(field.getName()).append("\", String.valueOf(").append(field.getName()).append(")));");
                }
            }
        }
        sb.append("return ").append(mFieldName).append(";}");
        return sb.toString();
    }
}
