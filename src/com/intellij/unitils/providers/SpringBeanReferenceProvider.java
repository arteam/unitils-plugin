package com.intellij.unitils.providers;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.*;
import com.intellij.spring.SpringIcons;
import com.intellij.spring.SpringManager;
import com.intellij.spring.contexts.model.SpringModel;
import com.intellij.spring.model.xml.beans.SpringBaseBeanPointer;
import com.intellij.spring.model.xml.beans.SpringBeanPointer;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * spring bean reference provider
 *
 * @author linux_china@hotmail.com
 */
public class SpringBeanReferenceProvider extends PsiReferenceProvider {

    /**
     * get references by element
     *
     * @param psiElement psi element
     * @param context    context
     * @return references
     */
    @NotNull
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext context) {
        PsiLiteralExpression literalExpression = (PsiLiteralExpression) psiElement;
        return new PsiReference[]{new PsiReferenceBase<PsiLiteralExpression>(literalExpression, false) {
            public PsiElement resolve() {
                String beanName = getCanonicalText();
                SpringManager springManager = SpringManager.getInstance(getElement().getProject());
                SpringModel springModel = springManager.getCombinedModel(ModuleUtil.findModuleForPsiElement(getElement()));
                if (springModel != null && beanName != null) {
                    SpringBeanPointer springBeanPointer = springModel.findBeanByName(beanName);
                    if (springBeanPointer != null)
                        return springBeanPointer.getPsiElement();
                }
                return null;
            }

            @Override
            public boolean isSoft() {
                return false;
            }

            public Object[] getVariants() {
                List<Object> beanNames = new ArrayList<Object>();
                SpringManager springManager = SpringManager.getInstance(getElement().getProject());
                SpringModel springModel = springManager.getCombinedModel(ModuleUtil.findModuleForPsiElement(getElement()));
                if (springModel != null) {
                    Collection<? extends SpringBaseBeanPointer> allCommonBeans = springModel.getAllCommonBeans();
                    for (SpringBaseBeanPointer commonBean : allCommonBeans) {
                        String beanName = commonBean.getName();
                        if (beanName != null) {
                            beanNames.add(LookupValueFactory.createLookupValue(beanName, SpringIcons.SPRING_BEAN_ICON));
                        }
                    }
                }
                return beanNames.toArray();
            }
        }};
    }
}