package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.wang.okhttpparamsget.Constant;
import com.wang.okhttpparamsget.Utils;
import com.wang.okhttpparamsget.nonull.NonNullFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by wang on 2017/3/6.
 */
abstract class JavaBuilder extends BaseBuilder {


    public JavaBuilder(String methodName, String fieldName) {
        super(methodName, fieldName);
    }

    protected String getMediaType() {
        return "okhttp3.MediaType";
    }

    @Override
    protected String getRequestBody() {
        return "okhttp3.RequestBody";
    }

    @Override
    public void build(PsiFile psiFile, Project project1, Editor editor) {
        if (psiFile == null) return;
        WriteCommandAction.runWriteCommandAction(project1, () -> {
            if (editor == null) return;
            Project project = editor.getProject();
            if (project == null) return;
            PsiElement mouse = psiFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass psiClass = PsiTreeUtil.getParentOfType(mouse, PsiClass.class);
            if (psiClass == null) return;

            if (psiClass.getNameIdentifier() == null) return;
            String className = psiClass.getNameIdentifier().getText();

            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);

            build(editor, mouse, elementFactory, project, psiClass, psiFile, className);

            JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
            styleManager.optimizeImports(psiFile);
            styleManager.shortenClassReferences(psiClass);

        });
    }


    private void build(Editor editor, PsiElement mouse, PsiElementFactory elementFactory, Project project, PsiClass psiClass, PsiFile psiFile, String className) {

        PsiClass superClass = psiClass.getSuperClass();
        PsiMethod getParams;
        if (superClass != null) {
            PsiMethod[] methods = superClass.findMethodsByName(mMethodName, true);
            if (methods.length > 0) {
                boolean needAll = methods[0].getModifierList().hasModifierProperty("abstract");
                getParams = elementFactory.createMethodFromText(buildMethod(psiClass, true, needAll), psiClass);
                getParams.getModifierList().addAnnotation("Override");
            } else {
                getParams = elementFactory.createMethodFromText(buildMethod(psiClass, false, true), psiClass);
            }
        } else {
            getParams = elementFactory.createMethodFromText(buildMethod(psiClass, false, true), psiClass);
        }
        String nonull = NonNullFactory.findNonNullForPsiElement(project, psiFile);
        if (nonull != null) {
            getParams.getModifierList().addAnnotation(nonull);
        }
        PsiMethod[] methods = psiClass.findMethodsByName(mMethodName, false);
        if (methods.length > 0) {
            methods[0].delete();
        }
        psiClass.add(getParams);
    }


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
        buildMethodBody(psiClass, fields, needAll, sb);
        sb.append("return ").append(mFieldName).append(";}");
        return sb.toString();
    }

    protected abstract void buildMethodBody(PsiClass psiClass, PsiField[] fields, boolean needAll, StringBuilder sb);


    protected abstract void addNullableValue(PsiField field, StringBuilder sb, @Nullable String defaultName);

    protected String toString(PsiField field) {
        return toString(field.getType(), field.getName());
    }

    @Override
    protected String toString(PsiType type, String name) {
        if (type instanceof PsiPrimitiveType) {
            return "String.valueOf(" + name + ")";
        } else if (type instanceof PsiArrayType) {
            return "java.util.Arrays.toString(" + name + ")";
        } else if (type.getCanonicalText().equals(String.class.getCanonicalName())) {
            return name;
        } else {
            return name + ".toString()";
        }
    }

    @Override
    protected StringBuilder createRequestBody(StringBuilder builder, String contentType, String content, boolean file) {
        boolean version4 = PropertiesComponent.getInstance().getBoolean(Constant.OKHTTP_VERSION, true);
        builder.append(getRequestBody()).append(".create(");
        if (version4) {
            builder.append(content).append(',');
            builder.append(getMediaType()).append(".parse(").append(contentType).append(")");
        } else {
            builder.append(getMediaType()).append(".parse(").append(contentType).append(")");
            builder.append(',').append(content);
        }
        builder.append(")");
        return builder;
    }


    @Override
    protected String getAnnotationText(@NotNull PsiElement element, @NotNull String name, @NotNull String valueName) {

        PsiAnnotation annotation = Utils.getAnnotation(((PsiModifierListOwner) element).getAnnotations(), name);
        if (annotation == null) {
            return null;
        }
        PsiAnnotationMemberValue value;
        if ((value = annotation.findAttributeValue(valueName)) != null) {
            String text = value.getText();
            if (text == null || text.length() == 0 || text.equals("\"\"")) {
                return "";
            }
            return text;
        }
        return "";
    }
}
