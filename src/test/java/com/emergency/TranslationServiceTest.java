package com.emergency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.emergency.service.TranslationService;

class TranslationServiceTest {
    private TranslationService translation;

    @BeforeAll
    void setUp() throws Exception {
        translation = new TranslationService("src/main/resources/dictionary.json");
    }

    @Test
    void shouldTranslateFrenchKeywordToEnglish() {
        assertTrue(translation.translateToEnglish("Il y a un feu chez moi").contains("fire"));
    }

    @Test
    void shouldTranslateGermanKeywordToEnglish() {
        assertTrue(translation.translateToEnglish("feuer hier drin").contains("fire"));
    }

    @Test
    void shouldTranslateBlessureToBleed() {
        assertEquals("bleed", translation.translateToEnglish("blessure"));
    }

    @Test
    void shouldIgnoreCapitalLetters() {
        assertEquals("bleed", translation.translateToEnglish("VERLETZUNG"));
    }

    @Test
    void shouldTranslateMixedCases() {
        assertEquals("blood", translation.translateToEnglish("saNG"));
    }

    @Test
    void shouldTranslateMultipleWord() {
        assertEquals("blood theft", translation.translateToEnglish("blut diebstahl"));
    }

    @Test
    void shouldTranslateDifferentLanguage() {
        assertEquals("blood theft", translation.translateToEnglish("blut vole"));
    }

    @Test
    void shouldNotTranslateEnglishWord() {
        assertEquals("theft", translation.translateToEnglish("theft"));
    }

    @Test
    void shouldLeaveUnknownWordUnchanged() {
        assertEquals("anyword", translation.translateToEnglish("anyword"));
    }

    @Test
    void shouldLeaveEmptyString() {
        assertEquals("", translation.translateToEnglish(""));
    }

    @Test
    void shouldLeaveEmptySpaces() {
        assertEquals("   ", translation.translateToEnglish("   "));
    }

    @Test
    void shouldLeaveNumbers() {
        assertEquals("128389", translation.translateToEnglish("128389"));
    }

    @Test
    void shouldLeaveSpecialCharaters() {
        assertEquals("!@#", translation.translateToEnglish("!@#"));
    }

    @Test
    void shouldTranslateRepeatedKeywords() {
        assertEquals("theft theft theft", translation.translateToEnglish("diebstahl diebstahl diebstahl"));
    }

    @Test
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(NullPointerException.class, () -> translation.translateToEnglish(null));
    }

    @ParameterizedTest
    @CsvSource({ "feuer, fire",
            "feu, fire",
            "blessure, bleed",
            "sang, blood",
            "blut, blood",
            "verletzung, bleed",
            "vole, theft",
            "diebstahl, theft" })
    void shouldTranslateKeywords(String input, String expected) throws Exception {
        TranslationService translationTest = new TranslationService("src/main/resources/dictionary.json");
        assertEquals(expected, translationTest.translateToEnglish(input));
    }
}
