package smarthrms;

import javafx.scene.text.Font;
import java.io.InputStream;

public class AmharicFontLoader {

    private static String FONT_PATH = "/fonts/NotoSansEthiopic-VariableFont_wdth,wght.ttf";
    private static Font baseFont;

    public static Font getAmharicFont(double size) {
        if (baseFont == null) {
            try (InputStream is = AmharicFontLoader.class.getResourceAsStream(FONT_PATH)) {
                if (is == null) {
                    System.err.println("❌ Font not found at " + FONT_PATH);
                } else {
                    baseFont = Font.loadFont(is, size);
                    if (baseFont != null) {
                        System.out.println("✅ Loaded Amharic font: " + baseFont.getName());
                    } else {
                        System.err.println("⚠️ Font.loadFont returned null!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return baseFont != null ? Font.font(baseFont.getFamily(), size) : Font.font("System", size);
    }
}
