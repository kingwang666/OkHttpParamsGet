package builder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/3/6.
 */
public class ParamsStringBuilder extends BaseBuilder {

    public ParamsStringBuilder() {
        super("getParams");
    }

    @Override
    protected String getMethodType() {
        return "Map<String, String>";
    }

    @Override
    protected String getValueType() {
        return "String";
    }


    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();
        imports.add("java.util.Map<String, String>");
        imports.add("java.util.HashMap<>");
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
        }
        else {
            sb.append(getMethodType()).append(mFieldName).append("=new HashMap<>();");
            fields = psiClass.getAllFields();
        }

        for (PsiField field : fields) {
            PsiModifierList modifiers = field.getModifierList();
            if (modifiers == null || modifiers.findAnnotation("Ignore") == null) {
                sb.append("params.put(").append("\"").append(field.getName()).append("\"").append(",String.valueOf(").append(field.getName()).append("));");
            }
        }
        sb.append("return ").append(mFieldName).append(";}");
        return sb.toString();
    }

}
