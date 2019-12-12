package com.wang.okhttpparamsget.builder;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.containers.ContainerUtil;
import com.wang.okhttpparamsget.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.asJava.LightClassUtilsKt;
import org.jetbrains.kotlin.asJava.classes.KtLightClass;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.idea.caches.resolve.ResolutionUtils;
import org.jetbrains.kotlin.idea.util.ImportInsertHelper;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.*;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wang on 2017/3/6.
 */
abstract class KotlinBuilder extends BaseBuilder {


    public KotlinBuilder(String methodName, String fieldName) {
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
            if (mouse == null) return;
            KtClass ktClass = Utils.getKtClassForElement(mouse);
            if (ktClass == null) return;

            if (ktClass.getNameIdentifier() == null) return;
            String className = ktClass.getNameIdentifier().getText();

            KtClassBody body = ktClass.getBody();
            if (body == null) return;

            KtPsiFactory elementFactory = KtPsiFactoryKt.KtPsiFactory(ktClass.getProject());

            KtLightClass lightClass = LightClassUtilsKt.toLightClass(ktClass);
            if (lightClass == null) return;
            build(editor, mouse, elementFactory, project, ktClass, body, lightClass, psiFile, className);

            String[] imports = getImports();
            if (imports != null) {
                for (String fqName : imports) {
                    final Collection<DeclarationDescriptor> descriptors = ResolutionUtils.resolveImportReference(ktClass.getContainingKtFile(), new FqName(fqName));
                    Iterator<DeclarationDescriptor> iterator = descriptors.iterator();
                    if (iterator.hasNext()) {
                        DeclarationDescriptor descriptor = iterator.next();
                        ImportInsertHelper.getInstance(project).importDescriptor(ktClass.getContainingKtFile(), descriptor, false);
                    }
                }
            }
            CodeStyleManager styleManager = CodeStyleManager.getInstance(ktClass.getProject());
            styleManager.reformatText(ktClass.getContainingFile(), ContainerUtil.newArrayList(ktClass.getTextRange()));
        });
    }


    private void build(Editor editor, PsiElement mouse, KtPsiFactory elementFactory, Project project, KtClass ktClass, KtClassBody body, KtLightClass lightClass, PsiFile psiFile, String className) {

        PsiClass superClass = lightClass.getSuperClass();
//        PsiMethod getParams;
        KtNamedFunction getParams;
        if (superClass != null) {
            PsiMethod[] methods = superClass.findMethodsByName(mMethodName, true);
            if (methods.length > 0) {
                boolean needAll = methods[0].getModifierList().hasModifierProperty("abstract");
                getParams = elementFactory.createFunction(buildMethod(ktClass, body, lightClass, true, needAll));
            } else {
                getParams = elementFactory.createFunction(buildMethod(ktClass, body, lightClass, false, true));
            }
        } else {
            getParams = elementFactory.createFunction(buildMethod(ktClass, body, lightClass, false, true));
        }
//        String nonull = NonNullFactory.findNonNullForPsiElement(project, psiFile);
//        if (nonull != null) {
//            getParams.addAnnotationEntry(elementFactory.createAnnotationEntry(nonull));
//        }
        PsiMethod[] methods = lightClass.findMethodsByName(mMethodName, false);
        if (methods.length > 0) {
            methods[0].delete();
        }
        body.addBefore(getParams, body.getRBrace());

    }

    @Nullable
    protected abstract String[] getImports();


    protected String buildMethod(KtClass ktClass, KtClassBody body, KtLightClass lightClass, boolean isOverride, boolean needAll) {
        StringBuilder sb = new StringBuilder();
        PsiField[] psiFields;
        if (isOverride) {
            sb.append("override fun ").append(mMethodName).append("(): ").append(getMethodType()).append("{");
        } else {
            sb.append("open fun ").append(mMethodName).append("(): ").append(getMethodType()).append("{");
        }
        if (needAll) {
            sb.append("val ").append(mFieldName).append(" = ").append(getParamsType()).append("()\n");
            psiFields = lightClass.getAllFields();
        } else {
            sb.append("val ").append(mFieldName).append("=super.").append(mMethodName).append("()\n");
            psiFields = lightClass.getFields();
        }
        buildMethodBody(ktClass, body, lightClass, psiFields, needAll, sb);
        sb.append("return ").append(mFieldName).append("}");
        return sb.toString();
    }

    protected abstract void buildMethodBody(KtClass ktClass, KtClassBody body, KtLightClass lightClass, PsiField[] fields, boolean needAll, StringBuilder sb);

    ;

    protected abstract void addNullableValue(PsiField field, StringBuilder sb, String defaultName);

    protected String toString(PsiField field, boolean nullable, String prefix) {
        if (prefix == null) {
            prefix = field.getName();
        }
        return toString(field.getType(), nullable, prefix);
    }

    private String toString(PsiType type, boolean nullable, String prefix) {
        if (type.getCanonicalText().equals(String.class.getCanonicalName())) {
            return prefix;
        } else if (nullable) {
            if (type instanceof PsiArrayType) {
                return prefix + "?.contentToString()";
            } else {
                return prefix + "?.toString()";
            }
        } else {
            if (type instanceof PsiArrayType) {
                return prefix + ".contentToString()";
            } else {
                return prefix + ".toString()";
            }
        }
    }

    @Override
    protected String toString(PsiType type, String name) {
        return toString(type, false, name);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected String getAnnotationText(@NotNull PsiElement element, @NotNull String name, @NotNull String valueName) {
        KtAnnotationEntry annotation = Utils.getAnnotation(((KtModifierListOwnerStub) element).getAnnotationEntries(), name);
        if (annotation == null) {
            return null;
        }
        List<ValueArgument> values = (List<ValueArgument>) annotation.getValueArguments();
        for (ValueArgument value : values) {
            ValueArgumentName argumentName = value.getArgumentName();
            if ((argumentName == null && valueName.equals("value")) || (argumentName != null && valueName.equals(argumentName.getAsName().asString()))) {
                String text;
                KtExpression expression = value.getArgumentExpression();
                text = expression == null ? null : expression.getText();
                if (text == null || text.length() == 0 || text.equals("\"\"")) {
                    return "";
                }
                return text;
            }
        }
        return "";
    }
}
