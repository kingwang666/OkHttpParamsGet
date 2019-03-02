package com.wang.okhttpparamsget.builder;

import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.wang.okhttpparamsget.Utils;
import com.wang.okhttpparamsget.nonull.NonNullFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.asJava.elements.KtLightField;
import org.jetbrains.kotlin.asJava.elements.KtLightMethod;
import org.jetbrains.kotlin.psi.*;

public abstract class BaseBuilder implements IBuilder {

    protected final String mMethodName;

    protected final String mFieldName;

    public BaseBuilder(String methodName, String fieldName) {
        mMethodName = methodName;
        mFieldName = fieldName;
    }

    protected abstract String getMethodType();

    protected abstract String getValueType();

    protected abstract String getParamsType();

    protected boolean isNullable(@NotNull PsiElement element) {
        if (element instanceof PsiField) {
            PsiField field = (PsiField) element;
            if (field.getType() instanceof PsiPrimitiveType) {
                return false;
            } else return !NonNullFactory.hasNonNull(field.getAnnotations());
        } else if (element instanceof KtProperty) {
            KtProperty property = (KtProperty) element;
            KtTypeReference reference = property.getTypeReference();
            return reference != null && reference.getTypeElement() instanceof KtNullableType;
        }
        return false;
    }

    protected boolean findIgnore(@NotNull PsiElement element) {
        if (element instanceof PsiField) {
            return Utils.findAnnotation(((PsiField) element).getAnnotations(), "Ignore");
        } else if (element instanceof KtProperty) {
            return Utils.findAnnotation(((KtProperty) element).getAnnotationEntries(), "Ignore");
        }
        return false;
    }

    protected boolean findPostFile(@NotNull PsiElement element) {
        if (element instanceof PsiField) {
            return Utils.findAnnotation(((PsiField) element).getAnnotations(), "PostFile");
        } else if (element instanceof KtProperty) {
            return Utils.findAnnotation(((KtProperty) element).getAnnotationEntries(), "PostFile");
        }
        return false;
    }

    protected boolean findPostFiles(@NotNull PsiElement element) {
        if (element instanceof PsiField) {
            return Utils.findAnnotation(((PsiField) element).getAnnotations(), "PostFiles");
        } else if (element instanceof KtProperty) {
            return Utils.findAnnotation(((KtProperty) element).getAnnotationEntries(), "PostFiles");
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean findAnnotation(@NotNull PsiElement element, String name) {
        if (element instanceof PsiModifierListOwner) {
            return Utils.findAnnotation(((PsiModifierListOwner) element).getAnnotations(), name);
        } else if (element instanceof KtModifierListOwnerStub) {
            return Utils.findAnnotation(((KtModifierListOwnerStub) element).getAnnotationEntries(), name);
        }
        return false;
    }

    protected String[] getFileInfo(PsiField psiField, String prefix, boolean list, boolean forJava) {
        PsiClass psiClass;
        if (list && psiField.getType() instanceof PsiClassType) {
            psiClass = JavaPsiFacade.getInstance(psiField.getProject()).findClass(((PsiClassType) psiField.getType()).getParameters()[0].getCanonicalText(), GlobalSearchScope.projectScope(psiField.getProject()));
        } else {
            psiClass = JavaPsiFacade.getInstance(psiField.getProject()).findClass(psiField.getType().getCanonicalText(), GlobalSearchScope.projectScope(psiField.getProject()));
        }
        if (psiClass == null) {
            return null;
        }
        if (psiClass.getNameIdentifier() == null) {
            return null;
        }
        String className = psiClass.getNameIdentifier().getText();
        String key = "key";
        String filename = "filename";
        String mimeType = null;
        String data = "file";
        for (PsiField field : psiClass.getAllFields()) {
            PsiElement older = null;
            if (field instanceof KtLightField) {
                older = ((KtLightField) field).getKotlinOrigin();
            }
            if (findAnnotation(older == null ? field : older, "Key")) {
                key = prefix + "." + field.getName();
            }
            if (findAnnotation(older == null ? field : older, "Filename")) {
                filename = prefix + "." + field.getName();
            }
            if (findAnnotation(older == null ? field : older, "MimeType")) {
                mimeType = prefix + "." + field.getName();
            }
            if (findAnnotation(older == null ? field : older, "Data")) {
                data = prefix + "." + field.getName();
            }
        }

        for (PsiMethod method : psiClass.getAllMethods()) {
            KtDeclaration function = null;
            if (PsiType.VOID.equals(method.getReturnType())) {
                continue;
            }
            if (method instanceof KtLightMethod) {
                function = ((KtLightMethod) method).getKotlinOrigin();
            }
            String name;
            if (!forJava && method.getNameIdentifier() != null && !method.getNameIdentifier().getText().equals(method.getName())) {
                name = prefix + "." + method.getNameIdentifier().getText();
            } else {
                name = prefix + "." + method.getName() + "()";
            }
            if (findAnnotation(function == null ? method : function, "Key")) {
                key = name;
            }
            if (findAnnotation(function == null ? method : function, "Filename")) {
                filename = name;
            }
            if (findAnnotation(function == null ? method : function, "MimeType")) {
                mimeType = name;
            }
            if (findAnnotation(function == null ? method : function, "Data")) {
                data = name;
            }
        }
        if (mimeType == null) {
            mimeType = "guessMimeType(" + filename + ")";
        }
        return new String[]{className, key, filename, mimeType, data};
    }


}
