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
class KotlinParamsObjectBuilder extends KotlinBuilder {

    public KotlinParamsObjectBuilder() {
        super("getParams", "params");
    }

    @Override
    protected String getMethodType() {
        return "MutableMap<String, Any> ";
    }

    @Override
    protected String getValueType() {
        return "Any";
    }

    @Override
    protected String getParamsType() {
        return "ArrayMap<String, Any>";
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
            if (!findIgnore(older == null ? field : older)) {
                if (isNullable(field)) {
                    addNullableValue(field, sb);
                } else {
                    sb.append(mFieldName).append("[\"").append(field.getName()).append("\"] = ").append(toString(field, false, null)).append("\n");
                }
            }
        }
    }

    @Override
    protected void addNullableValue(PsiField field, StringBuilder sb) {
        boolean add = PropertiesComponent.getInstance().getBoolean(Constant.VALUE_NULL, false);
        if (!add) {
            sb.append(field.getName()).append("?.also{\n");
            sb.append(mFieldName).append("[\"").append(field.getName()).append("\"] = ").append(toString(field, false, "it")).append("\n}\n");
        } else {
            sb.append(mFieldName).append("[\"").append(field.getName()).append("\"] = ").append(toString(field, true, null)).append("?: \"\"\n");
        }
    }

    @Override
    protected String toString(PsiField field, boolean nullable, String prefix) {
        return prefix != null ? prefix : field.getName();
    }
}
