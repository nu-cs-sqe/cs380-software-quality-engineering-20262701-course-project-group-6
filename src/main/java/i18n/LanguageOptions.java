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
        this.locales = parseLocales(readLanguages(languageFile));
    }

    public List<Locale> getLocales() {
        return locales;
    }

    private static String readLanguages(Properties languageFile) {
        String languages = languageFile.getProperty(LANGUAGES_KEY);
        if (languages == null) {
            throw new IllegalArgumentException("Language file is missing the '" + LANGUAGES_KEY + "' entry");
        }
        if (languages.isBlank()) {
            throw new IllegalArgumentException("Language file must list at least one language");
        }
        return languages;
    }

    private static List<Locale> parseLocales(String languages) {
        return Arrays.stream(languages.split(SEPARATOR))
                .map(String::trim)
                .map(Locale::forLanguageTag)
                .collect(Collectors.toUnmodifiableList());
    }
}
