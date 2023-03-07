package com.wang.okhttpparamsget;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.wang.okhttpparamsget.builder.BuilderFactory;
import com.wang.okhttpparamsget.builder.IBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Created by wang on 2017/3/7.
 */
public class GetParamsAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Project project = e.getProject();
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        TypePickDialog dialog = new TypePickDialog();
        dialog.setListener(type -> {build(type, psiFile, project, editor);});
        dialog.pack();
        dialog.setVisible(true);
    }

    private void build(int type, PsiFile psiFile, Project project, Editor editor) {
        IBuilder builder = BuilderFactory.getParamsBuilder(type, psiFile);
        if (builder != null){
            builder.build(psiFile, project, editor);
        }
    }

}
