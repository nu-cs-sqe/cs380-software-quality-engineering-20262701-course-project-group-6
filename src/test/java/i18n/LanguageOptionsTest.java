package i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class LanguageOptionsTest {
    private static final Locale SPANISH = Locale.forLanguageTag("es");

    @Test
    public void GetLocales_OneLanguage_ReturnsThatLanguage() {
        LanguageOptions options = new LanguageOptions(languageFile("en"));

        List<Locale> locales = options.getLocales();

        assertEquals(List.of(Locale.ENGLISH), locales);
    }

    @Test
    public void GetLocales_TwoLanguages_ReturnsBothInFileOrder() {
        LanguageOptions options = new LanguageOptions(languageFile("en,es"));

        List<Locale> locales = options.getLocales();

        assertEquals(List.of(Locale.ENGLISH, SPANISH), locales);
    }

    private static Properties languageFile(String languages) {
        Properties file = new Properties();
        file.setProperty("languages", languages);
        return file;
    }
}
