package com.wang.okhttpparamsget;

import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.wang.okhttpparamsget.builder.ParamsFileMapBuilder;
import com.wang.okhttpparamsget.builder.ParamsFilePartBuilder;
import com.wang.okhttpparamsget.builder.ParamsStringBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by wang on 2017/3/7.
 */
public class GetParamsAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Project project = e.getProject();
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        TypePickDialog dialog = new TypePickDialog();
        dialog.setListener(type -> {build(type, psiFile, project, editor);});
        dialog.pack();
        dialog.setVisible(true);
    }

    private void build(int type, PsiFile psiFile, Project project, Editor editor) {
        switch (type){
            case 1:
                new ParamsStringBuilder().build(psiFile, project, editor);
                break;
            case 2:
                new ParamsFileMapBuilder().build(psiFile, project, editor);
                break;
            case 3:
                new ParamsFilePartBuilder().build(psiFile, project, editor);
                break;
        }
    }

}
