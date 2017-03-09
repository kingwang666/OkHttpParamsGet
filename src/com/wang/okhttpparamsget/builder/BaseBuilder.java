package com.wang.okhttpparamsget.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by wang on 2017/3/6.
 */
public abstract class BaseBuilder {

    protected String mMethodName;

    protected final String mFieldName = "params";

    public BaseBuilder(String methodName) {
        mMethodName = methodName;
    }

    public void build(PsiFile psiFile, Project project1, Editor editor) {
        if (psiFile == null) return;
        WriteCommandAction.runWriteCommandAction(project1, () -> {
            if (editor == null) return;
            Project project = editor.getProject();
            if (project == null) return;

            PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (psiClass == null) return;

            if (psiClass.getNameIdentifier() == null) return;
            String className = psiClass.getNameIdentifier().getText();

            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);

            build(editor, elementFactory, project, psiClass, className);
        });
    }

    public void build(Editor editor, PsiElementFactory elementFactory, Project project, PsiClass psiClass, String className) {

        PsiClass superClass = psiClass.getSuperClass();
        PsiMethod getParams;
        if (superClass != null) {
            PsiMethod[] methods = superClass.findMethodsByName(mMethodName, true);
            if (methods.length > 0) {
                boolean needAll = methods[0].getModifierList().hasModifierProperty("abstract");
                getParams = elementFactory.createMethodFromText(buildMethod(psiClass, true, needAll), psiClass);
                getParams.getModifierList().addAnnotation("Override");
            } else {
                getParams = elementFactory.createMethodFromText(buildMethod(psiClass, false, false), psiClass);
            }
        } else {
            getParams = elementFactory.createMethodFromText(buildMethod(psiClass, false, false), psiClass);
        }
        PsiMethod[] methods = psiClass.findMethodsByName(mMethodName, false);
        if (methods.length > 0) {
            methods[0].delete();
        }
        psiClass.add(getParams);

//        setImports(psiClass, elementFactory, project);
    }

    private void setImports(PsiClass psiClass, PsiElementFactory elementFactory, Project project) {
        List<String> imports = getImports();
        if (imports != null) {
            for (String type : imports) {
                PsiType psiType = PsiType.getTypeByName(type, project, GlobalSearchScope.EMPTY_SCOPE);
                PsiField psiField = elementFactory.createField("tempImport212", psiType);
                psiClass.add(psiField);
                psiClass.findFieldByName("tempImport212", false).delete();
            }
        }

    }


    protected boolean containFiled(PsiClass psiClass, PsiField psiField) {
        return psiClass.findFieldByName(psiField.getName(), true) != null;
    }

    protected boolean containMethod(PsiClass psiClass, PsiMethod psiMethod) {
        return psiClass.findMethodsByName(psiMethod.getName(), true).length > 0;
    }

    protected boolean containMethod(PsiClass psiClass, String name) {
        return psiClass.findMethodsByName(name, true).length > 0;
    }

    protected boolean containClass(PsiClass psiClass, PsiClass innerClass) {
        return psiClass.findInnerClassByName(innerClass.getName(), true) != null;
    }

    protected boolean findIgnore(PsiModifierList modifiers) {
        return findAnnotation(modifiers, "Ignore");
    }

    protected boolean findPostFile(PsiModifierList modifiers) {
        return findAnnotation(modifiers, "PostFile");
    }

    protected boolean findPostFiles(PsiModifierList modifiers) {
        return findAnnotation(modifiers, "PostFiles");
    }

    protected boolean findAnnotation(PsiModifierList modifiers, @NotNull String name) {
        if (modifiers != null) {
            PsiAnnotation[] annotations = modifiers.getAnnotations();
            for (PsiAnnotation psiAnnotation : annotations) {
                String allName = psiAnnotation.getQualifiedName();
                if (!TextUtils.isEmpty(allName) && allName.endsWith(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected abstract String getMethodType();

    protected abstract String getValueType();

    protected abstract String buildMethod(PsiClass psiClass, boolean isOverride, boolean needAll);

    protected abstract List<String> getImports();
}
