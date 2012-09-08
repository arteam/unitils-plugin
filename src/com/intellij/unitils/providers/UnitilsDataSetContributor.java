package com.intellij.unitils.providers;

import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.URIReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * dataset path contributor
 *
 * @author linux_china@hotmail.com
 */
public class UnitilsDataSetContributor extends PsiReferenceContributor {
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PsiJavaPatterns.literalExpression().annotationParam("org.unitils.dbunit.annotation.DataSet", "value"), new URIReferenceProvider(), PsiReferenceRegistrar.HIGHER_PRIORITY);
        registrar.registerReferenceProvider(
                PsiJavaPatterns.literalExpression().annotationParam("org.unitils.dbunit.annotation.ExpectedDataSet", "value"), new URIReferenceProvider(), PsiReferenceRegistrar.HIGHER_PRIORITY);
        registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().insideAnnotationParam(
                PlatformPatterns.string().with(new PatternCondition<String>("DataSet") {
                    public boolean accepts(@NotNull final String annotationFQN, final ProcessingContext context) {
                        return annotationFQN.equals("org.unitils.dbunit.annotation.DataSet");
                    }
                }), "value"), new URIReferenceProvider());
    }
}