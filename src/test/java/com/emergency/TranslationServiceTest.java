package com.emergency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.emergency.service.TranslationService;

public class TranslationServiceTest {
    private static TranslationService translation;

    @BeforeAll
    public static void setUp() throws Exception {
        translation = new TranslationService("src/main/resources/dictionary.json");
    }

    @Test
    public void shouldTranslateFrenchKeywordToEnglish() {
        assertTrue(translation.translateToEnglish("Il y a un feu chez moi").contains("fire"));
    }

    @Test
    public void shouldTranslateGermanKeywordToEnglish() {
        assertTrue(translation.translateToEnglish("feuer hier drin").contains("fire"));
    }

    @Test
    public void shouldTranslateBlessureToBleed() {
        assertEquals("bleed", translation.translateToEnglish("blessure"));
    }

    @Test
    public void shouldIgnoreCapitalLetters() {
        assertEquals("bleed", translation.translateToEnglish("VERLETZUNG"));
    }

    @Test
    public void shouldTranslateMixedCases() {
        assertEquals("bleed", translation.translateToEnglish("saNG"));
    }

    @Test
    public void shouldTranslateMultipleWord() {
        assertEquals("bleed theft", translation.translateToEnglish("blut diebstahl"));
    }

    @Test
    public void shouldTranslateDifferentLanguage() {
        assertEquals("bleed theft", translation.translateToEnglish("blut vole"));
    }

    @Test
    public void shouldNotTranslateEnglishWord() {
        assertEquals("theft", translation.translateToEnglish("theft"));
    }

    @Test
    public void shouldLeaveUnknownWordUnchanged() {
        assertEquals("anyword", translation.translateToEnglish("anyword"));
    }

    @Test
    public void shouldLeaveEmptyString() {
        assertEquals("", translation.translateToEnglish(""));
    }

    @Test
    public void shouldLeaveEmptySpaces() {
        assertEquals("   ", translation.translateToEnglish("   "));
    }

    @Test
    public void shouldLeaveNumbers() {
        assertEquals("128389", translation.translateToEnglish("128389"));
    }

    @Test
    public void shouldLeaveSpecialCharaters() {
        assertEquals("!@#", translation.translateToEnglish("!@#"));
    }

    @Test
    public void shouldTranslateRepeatedKeywords() {
        assertEquals("theft theft theft", translation.translateToEnglish("diebstahl diebstahl diebstahl"));
    }

    @Test
    public void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(NullPointerException.class, () -> translation.translateToEnglish(null));
    }

    @ParameterizedTest
    @CsvSource({ "feuer, fire", 
                "feu, fire", 
                "incendie, fire", 
                "blessure, bleed",
                "sang, bleed", 
                "blut, bleed", 
                "verletzung, bleed",
                "vole, theft", 
                "diebstahl, theft" })
    public void shouldTranslateKeywords(String input, String expected) throws Exception {
        TranslationService translationTest = new TranslationService("src/main/resources/dictionary.json");
        assertEquals(expected, translationTest.translateToEnglish(input));
    }
}
