package com.intellij.unitils.providers;

import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;

/**
 * spring bean name contributor
 *
 * @author linux_china@hotmail.com
 */
public class SpringBeanNameContributor extends PsiReferenceContributor {
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PsiJavaPatterns.literalExpression().annotationParam("org.unitils.spring.annotation.SpringBean", "value"), new SpringBeanReferenceProvider(), PsiReferenceRegistrar.HIGHER_PRIORITY);
    }
}
