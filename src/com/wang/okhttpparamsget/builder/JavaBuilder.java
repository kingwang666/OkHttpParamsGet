package com.wang.okhttpparamsget.builder;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.wang.okhttpparamsget.nonull.NonNullFactory;

/**
 * Created by wang on 2017/3/6.
 */
abstract class JavaBuilder extends BaseBuilder {


    public JavaBuilder(String methodName, String fieldName) {
        super(methodName, fieldName);
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


    protected abstract void addNullableValue(PsiField field, StringBuilder sb);

    protected String toSting(PsiField field) {
        if (field.getType() instanceof PsiPrimitiveType) {
            return "String.valueOf(" + field.getName() + ")";
        } else if (field.getType().getCanonicalText().equals(String.class.getCanonicalName())) {
            return field.getName();
        } else {
            return field.getName() + ".toString()";
        }
    }
}
