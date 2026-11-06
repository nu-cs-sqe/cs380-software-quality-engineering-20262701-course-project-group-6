package i18n;

import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class LanguageOptions {
    private static final String LANGUAGES_KEY = "languages";

    private final List<Locale> locales;

    public LanguageOptions(Properties languageFile) {
        this.locales = List.of(Locale.forLanguageTag(languageFile.getProperty(LANGUAGES_KEY)));
    }

    public List<Locale> getLocales() {
        return locales;
    }
}
