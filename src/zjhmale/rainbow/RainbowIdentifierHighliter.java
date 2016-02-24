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
import zjhmale.rainbow.setting.RainbowIdentifierSettings;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zjh on 2016/2/13.
 */
public class RainbowIdentifierHighliter implements Annotator {
    private static String[] parentheses = {"(", ")", "{", "}", "[", "]"};
    private static List<String> parenthesesList = Arrays.asList(parentheses);

    /**
     * @param identifier
     * @param background
     * @return
     */
    public static TextAttributes getBraceAttributes(String identifier, Color background) {
        Color rainbowColor;
        if (background.getRed() < 128 && background.getGreen() < 128 && background.getBlue() < 128) {
            rainbowColor = RainbowColors.DARK_COMMON_COLORS[HashFace.rainbowIdentifierHash(identifier) % RainbowColors.DARK_COMMON_COLORS.length];
        } else {
            rainbowColor = RainbowColors.LIGHT_COMMON_COLORS[HashFace.rainbowIdentifierHash(identifier) % RainbowColors.LIGHT_COMMON_COLORS.length];
        }
        return new TextAttributes(rainbowColor, null, null, null, Font.PLAIN);
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

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof LeafPsiElement && RainbowIdentifierSettings.getInstance().isRainbowIdentifier) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            String t = element.getNode().getText();

            String languageID = element.getLanguage().getID();

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
                    && !t.startsWith("--");
            boolean isParentheses = parenthesesList.contains(t);
            boolean isString = (t.startsWith("\"") && t.endsWith("\"")) || (t.startsWith("\'") && t.endsWith("\'"));

            if ((javaLikePredicate || clojurePredicate || pythonPredicate || haskellLikePredicate) && !isParentheses && !isString) {
                final EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
                TextAttributes attrs = getBraceAttributes(t, scheme.getDefaultBackground());
                holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(attrs);
            }
        }
    }
}
