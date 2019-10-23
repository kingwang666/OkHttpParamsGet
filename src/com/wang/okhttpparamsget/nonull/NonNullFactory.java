package com.wang.okhttpparamsget.nonull;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.wang.okhttpparamsget.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NonNullFactory {

    private static String[] sSupportedNonNull = new String[]{
            "androidx.annotation.NonNull",
            "android.support.annotation.NonNull",
            "android.annotation.NonNull",
            "org.jetbrains.annotations.NotNull"
    };

    private static String[] sSupportedNullable = new String[]{
            "androidx.annotation.Nullable",
            "android.support.annotation.Nullable",
            "android.annotation.Nullable",
            "org.jetbrains.annotations.Nullable"
    };

    private NonNullFactory() {

    }

    public static boolean hasNonNull(@NotNull PsiAnnotation[] annotations){
        for (PsiAnnotation annotation : annotations){
            for (String name: sSupportedNonNull){
                if (name.equals(annotation.getQualifiedName())){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasNullable(@NotNull PsiAnnotation[] annotations){
        for (PsiAnnotation annotation : annotations){
            for (String name: sSupportedNullable){
                if (name.equals(annotation.getQualifiedName())){
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public static String findNonNullForPsiElement(@NotNull Project project, @NotNull PsiElement psiElement) {
        for (String nonull : sSupportedNonNull) {
            if (Utils.isClassAvailableForPsiFile(project, psiElement, nonull)) {
                return nonull;
            }
        }
        return findNonNullForPsiForProject(project);
    }

    @Nullable
    private static String findNonNullForPsiForProject(@NotNull Project project) {
        for (String nonull : sSupportedNonNull) {
            if (Utils.isClassAvailableForProject(project, nonull)) {
                return nonull;
            }
        }
        return null;
    }
}
