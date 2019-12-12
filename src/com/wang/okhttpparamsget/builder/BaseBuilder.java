package com.wang.okhttpparamsget.builder;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.wang.okhttpparamsget.Constant;
import com.wang.okhttpparamsget.Utils;
import com.wang.okhttpparamsget.data.FileInfo;
import com.wang.okhttpparamsget.nonull.NonNullFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.asJava.elements.KtLightField;
import org.jetbrains.kotlin.asJava.elements.KtLightMethod;
import org.jetbrains.kotlin.psi.*;

import java.util.regex.Pattern;

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
            }
            boolean defaultNullable = PropertiesComponent.getInstance().getBoolean(Constant.NULLABLE, true);
            if (defaultNullable) {
                return !NonNullFactory.hasNonNull(field.getAnnotations());
            }
            return NonNullFactory.hasNullable(field.getAnnotations());
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


    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean findAnnotation(@NotNull PsiElement element, String name) {
        if (element instanceof PsiModifierListOwner) {
            return Utils.findAnnotation(((PsiModifierListOwner) element).getAnnotations(), name);
        } else if (element instanceof KtModifierListOwnerStub) {
            return Utils.findAnnotation(((KtModifierListOwnerStub) element).getAnnotationEntries(), name);
        }
        return false;
    }

    @Nullable
    protected String getPostFileKey(@NotNull PsiElement element){
        return getAnnotationText(element, "PostFile", "key");
    }

    @Nullable
    protected String getParamName(@NotNull PsiElement element) {
        String name = getAnnotationText(element, "ParamName", "value");
        return name == null || name.isEmpty() ? null : name;
    }

    /**
     * 获取注解的值
     *
     * @return null -> not found annotation; "" -> this value is null or "";
     */
    protected abstract String getAnnotationText(@NotNull PsiElement element, @NotNull String name, @NotNull String valueName);


    protected FileInfo getFileInfo(PsiField psiField, String prefix, boolean forJava) {
        PsiClass psiClass;
        int type = FileInfo.NORMAL;
        PsiType psiType = psiField.getType();
        JudgeResult result = null;
        if (psiType instanceof PsiArrayType) {
            type = FileInfo.ARRAY;
            int dimensions = psiType.getArrayDimensions();
            if (dimensions > 1) {
                Notifications.Bus.notify(new Notification(Constant.NOTIFICATION_GROUP_ID, "", "the " + psiField.getName() + " array dimensions must is 1. but it is " + dimensions, NotificationType.ERROR));
                return null;
            }
            psiType = ((PsiArrayType) psiType).getComponentType();

        } else if ((result = judgeType(psiType, null, null)) != null) {
            type = result.type;
            if (type == FileInfo.LIST) {
                psiType = result.parameter1;
            } else {
                psiType = result.parameter2;
            }
        }

        if (psiType == null) {
            return null;
        }


        String className = null;
        String key = null;
        String filename;
        String mimeType = null;
        String data;

        if (type == FileInfo.MAP) {
            String keyType = result.parameter1 == null ? "String" : result.parameter1.getPresentableText();
            className = "Map.Entry<" + keyType + ", " + psiType.getPresentableText() + ">";
            if (forJava) {
                prefix = FileInfo.MAP_CHILD;
                key = toString(result.parameter1, prefix + ".getKey()");
                prefix = prefix + ".getValue()";
            } else {
                key = toString(result.parameter1, "key");
                prefix = "value";
            }
        } else if (type == FileInfo.ARRAY || type == FileInfo.LIST) {
            if (forJava) {
                prefix = FileInfo.LIST_CHILD;
            } else {
                prefix = FileInfo.KOTLIN_CHILD;
            }

        }

        if (psiType.getCanonicalText().equals("java.io.File")) {
            if (type != FileInfo.MAP) {
                className = "File";
                key = "\"" + psiField.getName() + "\"";
            }
            if (forJava) {
                filename = prefix + ".getName()";
            } else {
                filename = prefix + ".name";
            }
            mimeType = "guessMimeType(" + filename + ")";
            data = prefix;
            return new FileInfo(type, className, key, filename, mimeType, data);
        }

        if (psiType instanceof PsiClassType) {
            psiClass = ((PsiClassType) psiType).resolveGenerics().getElement();
        } else {
            psiClass = JavaPsiFacade.getInstance(psiField.getProject()).findClass(psiType.getCanonicalText(), GlobalSearchScope.projectScope(psiField.getProject()));
        }

        if (psiClass == null) {
            return null;
        }

        if (psiClass.getNameIdentifier() == null) {
            return null;
        }

        if (className == null) {
            className = psiClass.getNameIdentifier().getText();
        }

        filename = prefix + ".filename";
        data = prefix + ".file";
        FileInfo info = new FileInfo(type, className, key == null ? prefix + ".key" : key, filename, null, data);
        key = null;

        for (PsiField field : psiClass.getAllFields()) {
            PsiElement older = null;
            if (field instanceof KtLightField) {
                older = ((KtLightField) field).getKotlinOrigin();
            }
            if (findAnnotation(older == null ? field : older, "Key")) {
                if (!info.isMap()) {
                    key = prefix + "." + field.getName();
                }
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
                if (!info.isMap()) {
                    key = name;
                }
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
        if (key != null) {
            info.key = key;
        }
        info.filename = filename;
        info.mimeType = mimeType;
        info.data = data;
        return info;
    }

    private JudgeResult judgeType(PsiType psiType, Pattern iterable, Pattern map) {
        if (psiType == null || psiType.getCanonicalText().equals("java.lang.Object")) {
            return null;
        }
        iterable = iterable == null ? Pattern.compile("java[.]lang[.]Iterable<.*>|java[.]util[.]Set<.*>|java[.]util[.]List<.*>|java[.]util[.]Collection<.*>|java[.]util[.]SortedSet<.*>") : iterable;
        map = map == null ? Pattern.compile("java[.]util[.]NavigableMap<.*,.*>|java[.]util[.]SortedMap<.*,.*>|java[.]util[.]Map<.*,.*>") : map;
        if (iterable.matcher(psiType.getCanonicalText()).matches()) {
            PsiType[] parameters = ((PsiClassType) psiType).getParameters();
            PsiType result = parameters[0];
            if (result instanceof PsiWildcardType) {
                result = ((PsiWildcardType) result).getBound();
            }
            return new JudgeResult(FileInfo.LIST, result, null);
        } else if (map.matcher(psiType.getCanonicalText()).matches()) {
            PsiType[] parameters = ((PsiClassType) psiType).getParameters();
            PsiType result = parameters[0];
            if (result instanceof PsiWildcardType) {
                result = ((PsiWildcardType) result).getBound();
            }
            JudgeResult judgeResult = new JudgeResult(FileInfo.MAP, result, null);
            result = parameters[1];
            if (result instanceof PsiWildcardType) {
                result = ((PsiWildcardType) result).getBound();
            }
            judgeResult.parameter2 = result;
            return judgeResult;
        }
        PsiType[] supers = psiType.getSuperTypes();
        for (PsiType type : supers) {
            JudgeResult result = judgeType(type, iterable, map);
            if (result != null) {
                return result;
            }
        }
        return null;
    }


    protected abstract String toString(PsiType type, String name);

    private static class JudgeResult {

        int type;

        @Nullable
        PsiType parameter1;

        @Nullable
        PsiType parameter2;

        public JudgeResult(int type, @Nullable PsiType parameter1, @Nullable PsiType parameter2) {
            this.type = type;
            this.parameter1 = parameter1;
            this.parameter2 = parameter2;
        }

        @Override
        public String toString() {
            return "JudgeResult{" +
                    "type=" + type +
                    ", parameter1=" + parameter1 +
                    ", parameter2=" + parameter2 +
                    '}';
        }
    }

}
