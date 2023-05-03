package com.wang.okhttpparamsget.builder;

import ai.grazie.text.TextRange;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.wang.okhttpparamsget.Constant;
import com.wang.okhttpparamsget.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.asJava.LightClassUtilsKt;
import org.jetbrains.kotlin.asJava.classes.KtLightClass;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.idea.caches.resolve.ResolutionUtils;
import org.jetbrains.kotlin.idea.util.ImportInsertHelper;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.*;

import java.util.*;

/**
 * Created by wang on 2017/3/6.
 */
abstract class KotlinBuilder extends BaseBuilder {


    public KotlinBuilder(String methodName, String fieldName) {
        super(methodName, fieldName);
    }

    @Override
    protected String getMediaType() {
        return "MediaType";
    }

    @Override
    protected String getRequestBody() {
        return "RequestBody";
    }

    @Override
    public void build(PsiFile psiFile, Project project1, Editor editor) {
        if (psiFile == null) return;
        WriteCommandAction.runWriteCommandAction(project1, () -> {
            if (editor == null) return;
            Project project = editor.getProject();
            if (project == null) return;
            PsiElement mouse = psiFile.findElementAt(editor.getCaretModel().getOffset());
            KtClass ktClass = null;
            if (mouse != null){
                ktClass = Utils.getParentOfKtClass(mouse);
            }
            if (ktClass == null){
                ktClass = Utils.getChildOfKtClass(psiFile);
            }
            if (ktClass == null) return;

            if (ktClass.getNameIdentifier() == null) return;
            String className = ktClass.getNameIdentifier().getText();

            KtClassBody body = ktClass.getBody();
            if (body == null) return;

            KtPsiFactory elementFactory = new KtPsiFactory(ktClass.getProject());

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
            styleManager.reformatText(ktClass.getContainingFile(), Collections.singletonList(ktClass.getTextRange()));
        });
    }


    private void build(Editor editor, PsiElement mouse, KtPsiFactory elementFactory, Project project, KtClass ktClass, KtClassBody body, KtLightClass lightClass, PsiFile psiFile, String className) {

        PsiClass superClass = lightClass.getSuperClass();
        PsiClass[] interfaces = lightClass.getInterfaces();
        KtNamedFunction getParams;
        if (superClass != null || interfaces.length > 0) {
            PsiMethod[] methods;
            if (superClass != null && (methods = superClass.findMethodsByName(mMethodName, true)).length > 0) {
                boolean needAll = methods[0].getModifierList().hasModifierProperty(PsiModifier.ABSTRACT);
                getParams = elementFactory.createFunction(buildMethod(ktClass, body, lightClass, true, needAll));
            } else if (interfaces.length > 0) {
                boolean haveMethod = false;
                for (PsiClass interfaceClass : interfaces) {
                    if ((interfaceClass.findMethodsByName(mMethodName, true)).length > 0) {
                        haveMethod = true;
                        break;
                    }
                }
                getParams = elementFactory.createFunction(buildMethod(ktClass, body, lightClass, haveMethod, true));
            } else {
                getParams = elementFactory.createFunction(buildMethod(ktClass, body, lightClass, false, true));
            }
        } else {
            getParams = elementFactory.createFunction(buildMethod(ktClass, body, lightClass, false, true));
        }
        PsiMethod[] methods = lightClass.findMethodsByName(mMethodName, false);
        if (methods.length > 0) {
            methods[0].delete();
        }
        if (mouse == null || mouse.getParent() != body) {
            body.addBefore(getParams, body.getRBrace());
            return;
        }
        body.addAfter(getParams, mouse);

    }

    @Nullable
    protected abstract String[] getImports();


    protected String buildMethod(KtClass ktClass, KtClassBody body, KtLightClass lightClass, boolean isOverride, boolean needAll) {
        StringBuilder sb = new StringBuilder();
        PsiField[] psiFields;
        if (isOverride) {
            sb.append("override fun ").append(mMethodName).append("(): ").append(getMethodType()).append("{");
        } else if (lightClass.hasModifierProperty(PsiModifier.FINAL)) {
            sb.append("fun ").append(mMethodName).append("(): ").append(getMethodType()).append("{");
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

    @Override
    protected StringBuilder createRequestBody(StringBuilder builder, String contentType, String content, boolean file) {
        boolean version4 = PropertiesComponent.getInstance().getBoolean(Constant.OKHTTP_VERSION, true);
        if (version4) {
            builder.append(content).append(file ? ".as" : ".to").append("RequestBody(").append(contentType).append(".toMediaTypeOrNull()").append(")");
        } else {
            builder.append(getRequestBody()).append(".create(");
            builder.append(getMediaType()).append(".parse(").append(contentType).append(")");
            builder.append(",").append(content).append(")");
        }
        return builder;
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
