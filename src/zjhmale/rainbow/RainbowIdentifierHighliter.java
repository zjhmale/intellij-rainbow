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
    private static int rainbowIdentifierFaceCount = 15;

    private static String[] parentheses = {"(", ")", "{", "}", "[", "]"};
    private static List<String> parenthesesList = Arrays.asList(parentheses);

    private static final Color[] LIGHT_COLORS = {
            new Color(120,104,63),
            new Color(67,120,63),
            new Color(63,113,120),
            new Color(81,63,120),
            new Color(120,63,90),
            new Color(112,126,79),
            new Color(79,126,103),
            new Color(79,92,126),
            new Color(122,79,126),
            new Color(126,84,79),
            new Color(120,55,120),
            new Color(120,68,55),
            new Color(94,120,55),
            new Color(55,120,94),
            new Color(55,68,120)
    };

    private static final Color[] DARK_COLORS = {
            new Color(153,153,187),
            new Color(187,153,180),
            new Color(187,166,153),
            new Color(166,187,153),
            new Color(153,187,180),
            new Color(224,208,160),
            new Color(163,224,160),
            new Color(160,214,224),
            new Color(182,160,224),
            new Color(224,160,188),
            new Color(167,192,185),
            new Color(167,170,192),
            new Color(192,167,189),
            new Color(192,175,167),
            new Color(179,192,167)
    };

    /**
     *
     * @param identifier
     * @param background
     * @return
     */
    public static TextAttributes getBraceAttributes(String identifier, Color background) {
        Color rainbowColor;
        if (background.getRed() < 128 && background.getGreen() < 128 && background.getBlue() < 128) {
            rainbowColor = DARK_COLORS[HashFace.rainbowIdentifierHash(identifier) % rainbowIdentifierFaceCount];
        } else {
            rainbowColor = LIGHT_COLORS[HashFace.rainbowIdentifierHash(identifier) % rainbowIdentifierFaceCount];
        }
        return new TextAttributes(rainbowColor, null, null, null, Font.PLAIN);
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        System.out.println(RainbowIdentifierSettings.getInstance().isRainbowIdentifier);
        if (element instanceof LeafPsiElement && RainbowIdentifierSettings.getInstance().isRainbowIdentifier) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            String t = element.getNode().getText();

            boolean javaPredicate = element.getLanguage().getID().equals("JAVA")
                    && type != JavaTokenType.C_STYLE_COMMENT
                    && type != JavaTokenType.END_OF_LINE_COMMENT
                    && !JavaDocTokenType.ALL_JAVADOC_TOKENS.contains(type);
            boolean clojurePredicate = element.getLanguage().getID().equals("Clojure")
                    && !t.startsWith(";")
                    && !t.startsWith("#_")
                    && !t.startsWith("#(")
                    && !t.startsWith("#{");
            boolean isParentheses = parenthesesList.contains(t);
            boolean isString = t.startsWith("\"") && t.endsWith("\"");

            if ((javaPredicate || clojurePredicate) && !isParentheses && !isString) {
                final EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
                TextAttributes attrs = getBraceAttributes(t, scheme.getDefaultBackground());
                holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(attrs);
            }
        }
    }
}
