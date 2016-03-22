package zjhmale.rainbow

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.JavaDocTokenType
import com.intellij.psi.JavaTokenType
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import zjhmale.rainbow.encode.HashFace
import zjhmale.rainbow.setting.RainbowSettings
import java.awt.Color
import java.awt.Font
import java.util.*

/**
 * Created by zjh on 16/3/22.
 */
class RainbowHighliter : Annotator {
    private val delimitersList = Arrays.asList("(", ")", "{", "}", "[", "]")
    //https://github.com/JetBrains/kotlin/blob/master/compiler/frontend/src/org/jetbrains/kotlin/kdoc/lexer/KDocTokens.java
    private val kotlinDocTokens = Arrays.asList(
            "KDOC_START", "KDOC_END", "KDOC_LEADING_ASTERISK",
            "KDOC_TEXT", "KDOC_TAG_NAME", "KDOC_MARKDOWN_LINK",
            "KDOC_MARKDOWN_ESCAPED_CHAR", "KDOC_MARKDOWN_INLINE_LINK")
    //https://github.com/JetBrains/intellij-scala/blob/idea16.x/src/org/jetbrains/plugins/scala/lang/scaladoc/lexer/ScalaDocTokenType.java
    private val scalaDocTokens = Arrays.asList(
            "DOC_COMMENT_START", "DOC_COMMENT_END", "DOC_COMMENT_DATA",
            "DOC_SPACE", "DOC_COMMENT_LEADING_ASTERISKS", "DOC_TAG_NAME",
            "DOC_INLINE_TAG_START", "DOC_INLINE_TAG_END", "DOC_TAG_VALUE_TOKEN",
            "DOC_TAG_VALUE_DOT", "DOC_TAG_VALUE_COMMA", "DOC_TAG_VALUE_LPAREN",
            "DOC_TAG_VALUE_RPAREN", "DOC_TAG_VALUE_SHARP_TOKEN")
    //https://github.com/JetBrains/intellij-community/blob/master/plugins/groovy/groovy-psi/src/org/jetbrains/plugins/groovy/lang/groovydoc/lexer/GroovyDocTokenTypes.java
    private val groovyDocTokens = Arrays.asList(
            "GDOC_COMMENT_START", "GDOC_COMMENT_END", "GDOC_COMMENT_DATA",
            "GDOC_TAG_NAME", "GDOC_WHITESPACE", "GDOC_TAG_VALUE_TOKEN",
            "GDOC_TAG_VALUE_LPAREN", "GDOC_TAG_VALUE_RPAREN", "GDOC_TAG_VALUE_GT",
            "GDOC_TAG_VALUE_LT", "GDOC_INLINE_TAG_END", "DOC_INLINE_TAG_START",
            "GDOC_TAG_VALUE_COMMA", "GDOC_TAG_VALUE_SHARP_TOKEN", "GDOC_LEADING_ASTERISKS",
            "DOC_COMMENT_BAD_CHARACTER")

    private fun getAttributesColor(selector: Int, background: Color): Color {
        val rainbowColor: Color
        if (background.red < 128 && background.green < 128 && background.blue < 128) {
            rainbowColor = RainbowColors.DARK_COMMON_COLORS[selector % RainbowColors.DARK_COMMON_COLORS.size]
        } else {
            rainbowColor = RainbowColors.LIGHT_COMMON_COLORS[selector % RainbowColors.LIGHT_COMMON_COLORS.size]
        }
        return rainbowColor
    }

    private fun getIdentifierAttributes(identifier: String, background: Color): TextAttributes {
        val rainbowColor = getAttributesColor(HashFace.rainbowIdentifierHash(identifier), background)
        return TextAttributes(rainbowColor, null, null, null, Font.PLAIN)
    }

    private fun getDelimiterAttributes(level: Int, background: Color): TextAttributes {
        val rainbowColor = getAttributesColor(level, background)
        return TextAttributes(rainbowColor, null, null, null, Font.PLAIN)
    }

    private fun containsDelimiters(text: String): Boolean {
        return text.contains("(")
                || text.contains(")")
                || text.contains("{")
                || text.contains("}")
                || text.contains("[")
                || text.contains("]")
    }

    //predicate #_ reader
    private fun ignoreNextFormParent(psiElement: PsiElement): Boolean {
        var predicate = false
        var eachParent: PsiElement? = psiElement
        while (eachParent != null) {
            if (eachParent.javaClass.toString() == "class cursive.psi.impl.ClSexpComment") {
                if (eachParent.text.startsWith("#_")) {
                    predicate = true
                }
            }
            eachParent = eachParent.parent
        }
        return predicate
    }

    //get delimiter level
    private fun getDelimiterLevel(psiElement: PsiElement): Int {
        var level = -1
        var eachParent: PsiElement? = psiElement
        while (eachParent != null) {
            if (containsDelimiters(eachParent.text)) {
                level++
            }
            eachParent = eachParent.parent
        }
        return level
    }

    private fun isString(t: String): Boolean {
        return t.startsWith("\"") && t.endsWith("\"") || t.startsWith("\'") && t.endsWith("\'")
    }

    //a special case predicate string token for kotlin
    private fun isStringForKotlin(psiElement: PsiElement): Boolean {
        var eachParent: PsiElement? = psiElement
        var isString = false
        while (eachParent != null) {
            if (isString(eachParent.text)) {
                isString = true
                break
            }
            eachParent = eachParent.parent
        }
        return isString
    }

    private fun isString(element: PsiElement, languageID: String): Boolean {
        if (languageID == "kotlin") {
            return isStringForKotlin(element)
        } else {
            return isString(element.text)
        }
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val settings = RainbowSettings.getInstance()

        val scheme = EditorColorsManager.getInstance().globalScheme
        val backgroundColor = scheme.defaultBackground

        val languageID = element.language.id

        if (element is LeafPsiElement
                && delimitersList.contains(element.text)
                && languageID != "Clojure"
                && settings.isRainbowDelimiter) {
            val level = getDelimiterLevel(element)
            val attrs = getDelimiterAttributes(level, backgroundColor)
            holder.createInfoAnnotation(element as PsiElement, null).setEnforcedTextAttributes(attrs)
        }

        if (element is LeafPsiElement && settings.isRainbowIdentifier) {
            val type = element.elementType
            val t = element.getNode().text

            val javaPredicate = languageID == "JAVA"
                    && type !== JavaTokenType.C_STYLE_COMMENT
                    && type !== JavaTokenType.END_OF_LINE_COMMENT
                    && !JavaDocTokenType.ALL_JAVADOC_TOKENS.contains(type)
            val kotlinPredicate = languageID == "kotlin"
                    && !t.startsWith("//")
                    && !(t.startsWith("/*") && t.endsWith("*/"))
                    && !kotlinDocTokens.contains(type.toString())
            val clojurePredicate = languageID == "Clojure"
                    && !t.startsWith(";")
                    && !t.startsWith("#_")
                    && !t.startsWith("#(")
                    && !t.startsWith("#{")
                    && !ignoreNextFormParent(element)
            val pythonPredicate = languageID == "Python" && !t.startsWith("#")
            //for Haskell and Agda
            val haskellLikePredicate = (languageID == "Haskell" || languageID == "Agda")
                    && !t.startsWith("--")
                    && !(t.startsWith("{-") && t.endsWith("-}"))
            val rustPredicate = languageID == "RUST"
                    && !t.startsWith("//")
                    && !(t.startsWith("/*") && t.endsWith("*/"))
            val jsPredicate = languageID == "JavaScript"
                    && !t.startsWith("//")
                    && !(t.startsWith("/*") && t.endsWith("*/"))
            val erlangPredicate = languageID == "Erlang" && !t.startsWith("%")
            val scalaPredicate = languageID == "Scala"
                    && !t.startsWith("//")
                    && !(t.startsWith("/*") && t.endsWith("*/"))
                    && !scalaDocTokens.contains(type.toString())
            val goPredicate = languageID == "go"
                    && !t.startsWith("//")
                    && !(t.startsWith("/*") && t.endsWith("*/"))
            val groovyPredicate = languageID == "Groovy"
                    && !t.startsWith("//")
                    && !(t.startsWith("/*") && t.endsWith("*/"))
                    && !groovyDocTokens.contains(type.toString())

            val isParentheses = delimitersList.contains(t)

            if ((javaPredicate
                    || kotlinPredicate
                    || clojurePredicate
                    || pythonPredicate
                    || haskellLikePredicate
                    || rustPredicate
                    || jsPredicate
                    || erlangPredicate
                    || scalaPredicate
                    || goPredicate
                    || groovyPredicate)
                    && !isParentheses
                    && !isString(element, languageID)) {
                val attrs = getIdentifierAttributes(t, backgroundColor)
                holder.createInfoAnnotation(element as PsiElement, null).setEnforcedTextAttributes(attrs)
            }
        }
    }
}