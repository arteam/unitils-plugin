package com.intellij.unitils.annotators;

import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.spring.SpringIcons;
import com.intellij.spring.SpringManager;
import com.intellij.spring.contexts.model.SpringModel;
import com.intellij.spring.model.xml.beans.SpringBaseBeanPointer;
import com.intellij.spring.model.xml.beans.SpringBeanPointer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.intellij.openapi.diagnostic.Logger;

/**
 * spring bean reference annotator
 *
 * @author linux_china@hotmail.com
 */
public class SpringBeanReferenceAnnotator implements Annotator {

    private static final Logger log = Logger.getInstance(SpringBeanReferenceAnnotator.class);

    /**
     * spring bean reference annotator
     *
     * @param psiElement psi element
     * @param holder     annotation holder
     */
    public void annotate(PsiElement psiElement, AnnotationHolder holder) {
        if (!(psiElement instanceof PsiJavaFile)) {
            return;
        }

        // Get java classes
        for (PsiClass javaClass : ((PsiJavaFile) psiElement).getClasses()) {

            PsiClass superClass = javaClass.getSuperClass();
            log("Java class: " + javaClass.getText());
            log("Super class: " + (superClass != null ? superClass.getText() : ""));
            PsiField[] superClassFields = superClass != null ? superClass.getAllFields() : new PsiField[]{};
            log("Super class fields: " + Arrays.asList(superClassFields));
            for (PsiField field : javaClass.getAllFields()) {
                // We don't need scan super class fields
                log("Field: " + field.getNameIdentifier().getText());
                if (isContain(superClassFields, field)) {
                    log("Field: " + field.getNameIdentifier().getText() + " isn't needed");
                    continue;
                }

                PsiModifierList modifierList = field.getModifierList();
                if (modifierList == null) {
                    continue;
                }
                for (PsiAnnotation psiAnnotation : modifierList.getAnnotations()) {
                    String qcName = psiAnnotation.getQualifiedName();
                    if (qcName == null || !qcName.startsWith("org.unitils.spring.annotation.")) {
                        continue;
                    }

                    SpringManager springManager = SpringManager.getInstance(psiElement.getProject());
                    SpringModel springModel = springManager.getCombinedModel(ModuleUtil.findModuleForPsiElement(psiElement));
                    if (springModel == null) {
                        continue;
                    }
                    //by type
                    if (qcName.endsWith("SpringBeanByType")) {
                        final PsiType type = field.getType();
                        if (!(type instanceof PsiClassType)) {
                            continue;
                        }
                        final PsiClass typeClass = ((PsiClassType) type).resolve();
                        if (typeClass == null) {
                            continue;
                        }
                        List<SpringBaseBeanPointer> springBaseBeanPointerList = springModel.findBeansByPsiClass(typeClass);
                        if (springBaseBeanPointerList.size() > 0) {
                            List<PsiElement> elements = new ArrayList<PsiElement>();
                            for (SpringBaseBeanPointer springBaseBeanPointer : springBaseBeanPointerList) {
                                elements.add(springBaseBeanPointer.getPsiElement());
                            }
                            log("Target elements: " + elements);
                            NavigationGutterIconBuilder.create(SpringIcons.SPRING_BEAN_ICON)
                                    .setTooltipText("Navigate to the spring bean declaration(s)")
                                    .setTargets(elements)
                                    .install(holder, field.getNameIdentifier());
                        }
                    }
                    //by name
                    else {
                        String beanName = field.getName();
                        if (qcName.endsWith("SpringBean")) {
                            PsiAnnotationMemberValue attributeValue = psiAnnotation.findAttributeValue("value");
                            if (attributeValue != null && attributeValue.getText() != null) {
                                beanName = attributeValue.getText().replace("\"", "");
                            }
                        }
                        if (beanName != null && StringUtil.isNotEmpty(beanName)) {
                            SpringBeanPointer springBeanPointer = springModel.findBeanByName(beanName);
                            if (springBeanPointer != null) {
                                NavigationGutterIconBuilder.create(SpringIcons.SPRING_BEAN_ICON)
                                        .setTooltipText("Navigate to the spring bean declaration(s)")
                                        .setTarget(springBeanPointer.getPsiElement())
                                        .install(holder, field.getNameIdentifier());
                            }
                        }

                    }
                }

            }
        }
    }

    private boolean isContain(PsiField[] array, PsiField element) {
        for (PsiField t : array) {
            if (t != null &&
                    (t.equals(element) ||
                            t.getNameIdentifier().getText().equals(element.getNameIdentifier().getText()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Write fixed content to the given file.
     */
    void log(String text) {
        //log.info(text);
    }

}
