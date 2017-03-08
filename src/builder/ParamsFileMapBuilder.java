package builder;

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
        super("getParams");
    }

    @Override
    protected String getMethodType() {
        return "Map<String, RequestBody>";
    }

    @Override
    protected String getValueType() {
        return "RequestBody";
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();
        imports.add("java.util.Map<String, okhttp3.RequestBody>");
        imports.add("java.util.HashMap<>");
        imports.add("okhttp3.MediaType");
        return imports;
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
            sb.append(getMethodType()).append(mFieldName).append("=new HashMap<>();");
            fields = psiClass.getAllFields();
        }
        for (PsiField field : fields) {
            PsiModifierList modifiers = field.getModifierList();
            if (modifiers == null || modifiers.findAnnotation("Ignore") == null) {
                if (modifiers != null && modifiers.findAnnotation("PostFiles") != null) {
                    sb.append("if (").append(field.getName()).append("!=null&&").append(field.getName()).append(".size()>0){");
                    sb.append("for (FileInput file : ").append(field.getName()).append(") {");
                    sb.append("params.put(file.key + \"\\\"; filename=\\\"\" + file.filename,")
                            .append(getValueType()).append(".create(MediaType.parse(guessMimeType(file.filename)), file.file));}}");

                }else if (modifiers != null && modifiers.findAnnotation("PostFile") != null){
                    sb.append("if (").append(field.getName()).append("!=null){");
                    sb.append("params.put(").append(field.getName()).append(".key + \"\\\"; filename=\\\"\" + ").append(field.getName())
                            .append(".filename, ").append(getValueType()).append(".create(MediaType.parse(guessMimeType(")
                            .append(field.getName()).append(".filename)),").append(field.getName()).append(".file));}");
                }
                else {
                    sb.append("params.put(").append("\"").append(field.getName()).append("\", ").append(getValueType())
                            .append(".create(MediaType.parse(\"text/plain\"), String.valueOf(").append(field.getName()).append(")));");
                }
            }
        }
        sb.append("return ").append(mFieldName).append(";}");
        return sb.toString();
    }
}
