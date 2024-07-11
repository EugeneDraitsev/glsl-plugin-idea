package glsl.plugin.reference

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import glsl.GlslTypes
import glsl.plugin.psi.GlslIdentifierImpl


/**
 *
 */
class GlslReferenceContributor : PsiReferenceContributor() {
    private val numeric = StandardPatterns.or(
        psiElement(GlslTypes.INTCONSTANT),
        psiElement(GlslTypes.UINTCONSTANT),
        psiElement(GlslTypes.FLOATCONSTANT),
        psiElement(GlslTypes.DOUBLECONSTANT),
    )

    /**
    *
    */
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val identifierPattern = psiElement(GlslIdentifierImpl::class.java)
            .andNot(psiElement().afterSibling(psiElement(GlslTypes.TYPE_QUALIFIER)))
            .andNot(psiElement().afterSibling(psiElement(GlslTypes.TYPE_SPECIFIER)))
            .andNot(psiElement().afterLeaf("struct"))
            .andNot(psiElement().afterLeaf(numeric))
        registrar.registerReferenceProvider(identifierPattern, GlslReferenceProvider())
    }

    /**
     *
     */
    inner class GlslReferenceProvider : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            if (element !is GlslIdentifierImpl) return emptyArray()
            val range = TextRange(0, element.name.length)
            return arrayOf(GlslReference(element, range))
        }
    }
}


