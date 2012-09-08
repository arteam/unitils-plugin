package com.intellij.unitils.annotators;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.FileContentUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * dataset file creation renderer
 *
 * @author linux_china@hotmail.com
 */
public class DataSetCreationIconRenderer extends GutterIconRenderer {
    private PsiDirectory psiDirectory;
    private String fileName;
    private final Icon icon;

    /**
     * default construction
     *
     * @param psiDirectory directory
     * @param fileName     file Name
     * @param icon         icon
     */
    public DataSetCreationIconRenderer(PsiDirectory psiDirectory, String fileName, Icon icon) {
        this.psiDirectory = psiDirectory;
        this.fileName = fileName;
        this.icon = icon;
    }

    /**
     * gutter icon for template
     *
     * @return icon
     */
    @NotNull
    public Icon getIcon() {
        return icon;
    }

    /**
     * navigation enable mark
     *
     * @return mark
     */
    public boolean isNavigateAction() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSetCreationIconRenderer that = (DataSetCreationIconRenderer) o;

        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;
        if (icon != null ? !icon.equals(that.icon) : that.icon != null) return false;
        if (psiDirectory != null ? !psiDirectory.equals(that.psiDirectory) : that.psiDirectory != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = psiDirectory != null ? psiDirectory.hashCode() : 0;
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        return result;
    }

    /**
     * get click action
     *
     * @return click action
     */
    @Nullable
    public AnAction getClickAction() {
        return new AnAction() {
            /**
             * action performed
             * @param e        event
             */
            public void actionPerformed(AnActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        if (psiDirectory != null) {
                            PsiFile dataSetFile = psiDirectory.findFile(fileName);
                            if (dataSetFile == null) {
                                dataSetFile = psiDirectory.createFile(fileName);
                                try {
                                    VirtualFile virtualFile = dataSetFile.getVirtualFile();
                                    if (virtualFile != null) {
                                        FileContentUtil.setFileText(psiDirectory.getProject(), virtualFile, "<?xml version='1.0' encoding='UTF-8'?>\n" +
                                                "<dataset>\n" +
                                                "  \n" +
                                                "</dataset>");
                                    }
                                    dataSetFile.navigate(true);
                                } catch (Exception e2) {

                                }
                            }

                        }
                    }
                });

            }
        };
    }
}
