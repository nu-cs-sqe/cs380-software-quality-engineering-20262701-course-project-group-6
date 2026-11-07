package language;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class MessageBundlesTest {
    private static final String MESSAGES_BUNDLE = "messages";
    private static final ResourceBundle.Control NO_FALLBACK =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    @Test
    public void MessageBundles_EveryListedLanguage_HasEveryEnglishKey() throws IOException {
        Set<String> englishKeys = ResourceBundle.getBundle(MESSAGES_BUNDLE, Locale.ENGLISH, NO_FALLBACK).keySet();

        for (Locale locale : new LanguageOptions(loadLanguageFile()).getLocales()) {
            ResourceBundle messages = ResourceBundle.getBundle(MESSAGES_BUNDLE, locale, NO_FALLBACK);
            assertEquals(englishKeys, messages.keySet(), "keys in messages_" + locale + ".properties");
        }
    }

    private static Properties loadLanguageFile() throws IOException {
        Properties languageFile = new Properties();
        try (InputStream input = MessageBundlesTest.class.getResourceAsStream("/languages.properties")) {
            languageFile.load(input);
        }
        return languageFile;
    }
}
