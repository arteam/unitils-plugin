package com.intellij.unitils.providers;

import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.URIReferenceProvider;

/**
 * spring bean name provider
 *
 * @author linux_china@hotmail.com
 */
public class SpringApplicationContextContributor extends PsiReferenceContributor {
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PsiJavaPatterns.literalExpression().annotationParam("org.unitils.spring.annotation.SpringApplicationContext", "value"), new URIReferenceProvider(), PsiReferenceRegistrar.HIGHER_PRIORITY);
    }
}