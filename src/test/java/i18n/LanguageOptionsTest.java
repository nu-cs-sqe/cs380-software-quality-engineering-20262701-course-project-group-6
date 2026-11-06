package i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class LanguageOptionsTest {

    @Test
    public void GetLocales_OneLanguage_ReturnsThatLanguage() {
        LanguageOptions options = new LanguageOptions(languageFile("en"));

        List<Locale> locales = options.getLocales();

        assertEquals(List.of(Locale.ENGLISH), locales);
    }

    private static Properties languageFile(String languages) {
        Properties file = new Properties();
        file.setProperty("languages", languages);
        return file;
    }
}
