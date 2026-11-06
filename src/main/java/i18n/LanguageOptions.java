package i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

public class LanguageOptions {
    private static final String LANGUAGES_KEY = "languages";
    private static final String SEPARATOR = ",";

    private final List<Locale> locales;

    public LanguageOptions(Properties languageFile) {
        this.locales = Arrays.stream(languageFile.getProperty(LANGUAGES_KEY).split(SEPARATOR))
                .map(String::trim)
                .map(Locale::forLanguageTag)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Locale> getLocales() {
        return locales;
    }
}
