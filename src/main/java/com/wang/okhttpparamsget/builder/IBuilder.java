package com.wang.okhttpparamsget.builder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

public interface IBuilder {

    void build(PsiFile psiFile, Project project1, Editor editor);

}
