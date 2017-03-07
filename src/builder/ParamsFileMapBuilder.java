package builder;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;

/**
 * Created by wang on 2017/3/7.
 */
public class ParamsFileMapBuilder extends BaseBuilder {

    public ParamsFileMapBuilder() {
        super("getParams");
    }

    @Override
    protected String getMethodType() {
        return "java.util.Map<String, okhttp3.RequestBody>";
    }

    @Override
    protected String getValueType() {
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
            sb.append(getMethodType()).append(mFieldName).append("=new java.util.HashMap<>();");
            fields = psiClass.getAllFields();
        }
        sb.append(getValueType()).append(" requestBody;");
        for (PsiField field : fields) {
            PsiModifierList modifiers = field.getModifierList();
            if (modifiers == null || modifiers.findAnnotation("Ignore") == null) {
                if (modifiers != null && modifiers.findAnnotation("PostFiles") != null) {
                    sb.append("if (").append(field.getName()).append("!=null&&").append(field.getName()).append(".size()>0){");
                    sb.append("for (FileInput file : ").append(field.getName()).append(") {");
                    sb.append("requestBody = ").append(getValueType()).append(".create(okhttp3.MediaType.parse(guessMimeType(file.filename)), file.file);");
                    sb.append("params.put(file.key + \"\\\"; filename=\\\"\" + file.filename, requestBody);").append("}}");

                }else if (modifiers != null && modifiers.findAnnotation("PostFile") != null){
                    sb.append("if (").append(field.getName()).append("!=null){");
                    sb.append("requestBody = ").append(getValueType()).append(".create(okhttp3.MediaType.parse(guessMimeType(").append(field.getName()).append(".filename)),").append(field.getName()).append(".file);");
                    sb.append("params.put(").append(field.getName()).append(".key + \"\\\"; filename=\\\"\" + ").append(field.getName()).append(".filename, requestBody);").append("}");
                }
                else {
                    sb.append("requestBody = ").append(getValueType()).append(".create(okhttp3.MediaType.parse(\"text/plain\"), String.valueOf(").append(field.getName()).append("));");
                    sb.append("params.put(").append("\"").append(field.getName()).append("\"").append(", requestBody);");
                }
            }
        }
        sb.append("return ").append(mFieldName).append(";}");
        return sb.toString();
    }
}
