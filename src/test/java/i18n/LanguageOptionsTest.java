package i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void GetLocales_SpaceAfterComma_SpacesTrimmed() {
        LanguageOptions options = new LanguageOptions(languageFile("en, es"));

        List<Locale> locales = options.getLocales();

        assertEquals(List.of(Locale.ENGLISH, SPANISH), locales);
    }

    @Test
    public void Constructor_EmptyLanguageList_ThrowsIllegalArgumentException() {
        Properties file = languageFile("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new LanguageOptions(file));

        assertEquals("Language file must list at least one language", exception.getMessage());
    }

    @Test
    public void Constructor_NoLanguagesEntry_ThrowsIllegalArgumentException() {
        Properties file = new Properties();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new LanguageOptions(file));

        assertEquals("Language file is missing the 'languages' entry", exception.getMessage());
    }

    private static Properties languageFile(String languages) {
        Properties file = new Properties();
        file.setProperty("languages", languages);
        return file;
    }
}
