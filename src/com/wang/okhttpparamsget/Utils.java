package com.wang.okhttpparamsget;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.asJava.elements.KtLightElement;
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor;
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor;
import org.jetbrains.kotlin.incremental.components.NoLookupLocation;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.resolve.lazy.ResolveSession;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static boolean findAnnotation(@NotNull PsiAnnotation[] annotations, @NotNull String name) {
        for (PsiAnnotation psiAnnotation : annotations) {
            String allName = psiAnnotation.getQualifiedName();
            if (!TextUtils.isEmpty(allName) && allName.endsWith(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchAnnotation(@NotNull PsiAnnotation[] annotations, @NotNull String name){
        for (PsiAnnotation psiAnnotation : annotations) {
            String allName = psiAnnotation.getQualifiedName();
            if (name.equals(allName)){
                return true;
            }
        }
        return false;
    }

    public static boolean findAnnotation(@NotNull List<KtAnnotationEntry> annotations, @NotNull String name) {
        for (KtAnnotationEntry annotation : annotations) {
            Name allName = annotation.getShortName();
            if (allName != null && name.equals(allName.asString())) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchAnnotation(@NotNull List<KtAnnotationEntry> annotations, @NotNull String name){
        for (KtAnnotationEntry annotation : annotations) {
            Name allName = annotation.getShortName();
            if (allName != null && name.equals(allName.asString())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Check whether classpath of a module that corresponds to a {@link PsiElement} contains given class.
     *
     * @param project    Project
     * @param psiElement Element for which we check the class
     * @param className  Class name of the searched class
     * @return True if the class is present on the classpath
     * @since 1.3
     */
    public static boolean isClassAvailableForPsiFile(@NotNull Project project, @NotNull PsiElement psiElement, @NotNull String className) {
        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        if (module == null) {
            return false;
        }
        GlobalSearchScope moduleScope = module.getModuleWithDependenciesAndLibrariesScope(false);
        PsiClass classInModule = JavaPsiFacade.getInstance(project).findClass(className, moduleScope);
        return classInModule != null;
    }

    /**
     * Check whether classpath of a the whole project contains given class.
     * This is only fallback for wrongly setup projects.
     *
     * @param project   Project
     * @param className Class name of the searched class
     * @return True if the class is present on the classpath
     * @since 1.3.1
     */
    public static boolean isClassAvailableForProject(@NotNull Project project, @NotNull String className) {
        PsiClass classInModule = JavaPsiFacade.getInstance(project).findClass(className,
                new EverythingGlobalScope(project));
        return classInModule != null;
    }

    public static List<ValueParameterDescriptor> findParams(KtClass ktClass) {
        List<KtElement> list = new ArrayList<KtElement>();
        list.add(ktClass);

        ResolveSession resolveSession = KotlinCacheService.Companion.getInstance(ktClass.getProject()).
                getResolutionFacade(list).getFrontendService(ResolveSession.class);
        ClassDescriptor classDescriptor = resolveSession.getClassDescriptor(ktClass, NoLookupLocation.FROM_IDE);

        List<ValueParameterDescriptor> valueParameters = new ArrayList<ValueParameterDescriptor>();
//        if (classDescriptor.isData()) {
        ConstructorDescriptor constructorDescriptor = classDescriptor.getUnsubstitutedPrimaryConstructor();

        if (constructorDescriptor != null) {
            List<ValueParameterDescriptor> allParameters = constructorDescriptor.getValueParameters();

            for(ValueParameterDescriptor parameter : allParameters) {
                valueParameters.add(parameter);
            }
        }
//        }

        return valueParameters;
    }

    public static KtClass getKtClassForElement(@NotNull PsiElement psiElement) {
        if (psiElement instanceof KtLightElement) {
            PsiElement origin = ((KtLightElement) psiElement).getKotlinOrigin();
            if (origin != null) {
                return getKtClassForElement(origin);
            } else {
                return null;
            }

        } else if (psiElement instanceof KtClass && !((KtClass) psiElement).isEnum() &&
                !((KtClass) psiElement).isInterface() &&
                !((KtClass) psiElement).isAnnotation() &&
                !((KtClass) psiElement).isSealed()) {
            return (KtClass) psiElement;

        } else {
            PsiElement parent = psiElement.getParent();
            if (parent == null) {
                return null;
            } else {
                return getKtClassForElement(parent);
            }
        }
    }

}
