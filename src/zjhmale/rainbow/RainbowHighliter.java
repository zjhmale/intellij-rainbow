package zjhmale.rainbow;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.JavaDocTokenType;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import zjhmale.rainbow.encode.HashFace;
import zjhmale.rainbow.setting.RainbowSettings;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zjh on 2016/2/13.
 */
public class RainbowHighliter implements Annotator {
    private static String[] delimiters = {"(", ")", "{", "}", "[", "]"};
    private static List<String> delimitersList = Arrays.asList(delimiters);

    private static Color getAttributesColor(int selector, Color background) {
        Color rainbowColor;
        if (background.getRed() < 128 && background.getGreen() < 128 && background.getBlue() < 128) {
            rainbowColor = RainbowColors.DARK_COMMON_COLORS[selector % RainbowColors.DARK_COMMON_COLORS.length];
        } else {
            rainbowColor = RainbowColors.LIGHT_COMMON_COLORS[selector % RainbowColors.LIGHT_COMMON_COLORS.length];
        }
        return rainbowColor;
    }

    private static TextAttributes getIdentifierAttributes(String identifier, Color background) {
        Color rainbowColor = getAttributesColor(HashFace.rainbowIdentifierHash(identifier), background);
        return new TextAttributes(rainbowColor, null, null, null, Font.PLAIN);
    }

    private static TextAttributes getDelimiterAttributes(int level, Color background) {
        Color rainbowColor = getAttributesColor(level, background);
        return new TextAttributes(rainbowColor, null, null, null, Font.PLAIN);
    }

    private static boolean containsDelimiters(String text) {
        return text.contains("(")
                || text.contains(")")
                || text.contains("{")
                || text.contains("}")
                || text.contains("[")
                || text.contains("]");
    }

    //predicate #_ reader
    private static boolean ignoreNextFormParent(PsiElement psiElement) {
        boolean predicate = false;
        PsiElement eachParent = psiElement;
        while (eachParent != null) {
            if (eachParent.getClass().toString().equals("class cursive.psi.impl.ClSexpComment")) {
                if (eachParent.getText().startsWith("#_")) {
                    predicate = true;
                }
            }
            eachParent = eachParent.getParent();
        }
        return predicate;
    }

    //get delimiter level
    private static int getDelimiterLevel(PsiElement psiElement) {
        int level = -1;
        PsiElement eachParent = psiElement;
        while (eachParent != null) {
            if (containsDelimiters(eachParent.getText())) {
                level++;
            }
            eachParent = eachParent.getParent();
        }
        return level;
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        RainbowSettings settings = RainbowSettings.getInstance();

        final EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        Color backgroundColor = scheme.getDefaultBackground();

        String languageID = element.getLanguage().getID();

        if (element instanceof LeafPsiElement
                && delimitersList.contains(element.getText())
                && !languageID.equals("Clojure")
                && settings.isRainbowDelimiter) {
            int level = getDelimiterLevel(element);
            TextAttributes attrs = getDelimiterAttributes(level, backgroundColor);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(attrs);
        }

        if (element instanceof LeafPsiElement && settings.isRainbowIdentifier) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            String t = element.getNode().getText();

            //for JAVA and Kotlin
            boolean javaLikePredicate = (languageID.equals("JAVA") || languageID.equals("kotlin"))
                    && type != JavaTokenType.C_STYLE_COMMENT
                    && type != JavaTokenType.END_OF_LINE_COMMENT
                    && !JavaDocTokenType.ALL_JAVADOC_TOKENS.contains(type);
            boolean clojurePredicate = languageID.equals("Clojure")
                    && !t.startsWith(";")
                    && !t.startsWith("#_")
                    && !t.startsWith("#(")
                    && !t.startsWith("#{")
                    && !ignoreNextFormParent(element);
            boolean pythonPredicate = languageID.equals("Python")
                    && !t.startsWith("#");
            //for Haskell and Agda
            boolean haskellLikePredicate = (languageID.equals("Haskell") || languageID.equals("Agda"))
                    && !t.startsWith("--")
                    && !(t.startsWith("{-") && t.endsWith("-}"));
            boolean rustPredicate = languageID.equals("RUST")
                    && !t.startsWith("//")
                    && !(t.startsWith("/*") && t.endsWith("*/"));
            boolean jsPredicate = languageID.equals("JavaScript")
                    && !t.startsWith("//")
                    && !(t.startsWith("/*") && t.endsWith("*/"));
            boolean isParentheses = delimitersList.contains(t);
            boolean isString = (t.startsWith("\"") && t.endsWith("\"")) || (t.startsWith("\'") && t.endsWith("\'"));

            if ((javaLikePredicate
                    || clojurePredicate
                    || pythonPredicate
                    || haskellLikePredicate
                    || rustPredicate
                    || jsPredicate)
                    && !isParentheses
                    && !isString) {
                TextAttributes attrs = getIdentifierAttributes(t, backgroundColor);
                holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(attrs);
            }
        }
    }
}
