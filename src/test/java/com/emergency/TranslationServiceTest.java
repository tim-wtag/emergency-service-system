package com.emergency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.emergency.service.TranslationService;

public class TranslationServiceTest {
    @Test
    public void shouldTranslateFrenchKeywordToEnglish() {
        TranslationService translation = new TranslationService();
        assertTrue(translation.translateToEnglish("Il y a un feu chez moi").contains("fire"));
    }

    @Test
    public void shouldTranslateGermanKeywordToEnglish() {
        TranslationService translation = new TranslationService();
        assertTrue(translation.translateToEnglish("feuer hier drin").contains("fire"));
    }

    @Test
    public void shouldTranslateBlessureToBleed() {
        TranslationService translation = new TranslationService();
        assertEquals("bleed", translation.translateToEnglish("blessure"));
    }

    @Test
    public void shouldIgnoreCapitalLetters() {
        TranslationService translation = new TranslationService();
        assertEquals("bleed", translation.translateToEnglish("VERLETZUNG"));
    }

    @Test
    public void shouldTranslateMixedCases() {
        TranslationService translation = new TranslationService();
        assertEquals("bleed", translation.translateToEnglish("saNG"));
    }

    @Test
    public void shouldTranslateMultipleWord() {
        TranslationService translation = new TranslationService();
        assertEquals("bleed theft", translation.translateToEnglish("blut diebstahl"));
    }

    @Test
    public void shouldTranslateDifferentLanguage() {
        TranslationService translation = new TranslationService();
        assertEquals("bleed theft", translation.translateToEnglish("blut vole"));
    }

    @Test
    public void shouldNotTranslateEnglishWord() {
        TranslationService translation = new TranslationService();
        assertEquals("theft", translation.translateToEnglish("theft"));
    }

    @Test
    public void shouldLeaveUnknownWordUnchanged() {
        TranslationService translation = new TranslationService();
        assertEquals("ambulance", translation.translateToEnglish("ambulance"));
    }

    @Test
    public void shouldLeaveEmptyString() {
        TranslationService translation = new TranslationService();
        assertEquals("", translation.translateToEnglish(""));
    }

    @Test
    public void shouldLeaveEmptySpaces() {
        TranslationService translation = new TranslationService();
        assertEquals("   ", translation.translateToEnglish("   "));
    }

    @Test
    public void shouldLeaveNumbers() {
        TranslationService translation = new TranslationService();
        assertEquals("128389", translation.translateToEnglish("128389"));
    }

    @Test
    public void shouldLeaveSpecialCharaters() {
        TranslationService translation = new TranslationService();
        assertEquals("!@#", translation.translateToEnglish("!@#"));
    }

    @Test
    public void shouldTranslateRepeatedKeywords() {
        TranslationService translation = new TranslationService();
        assertEquals("theft theft theft", translation.translateToEnglish("diebstahl diebstahl diebstahl"));
    }

    @Test
    public void shouldThrowExceptionWhenInputIsNull() {
        TranslationService translation = new TranslationService();
        assertThrows(NullPointerException.class, () -> translation.translateToEnglish(null));
    }

    @ParameterizedTest
    @CsvSource({"feuer, fire","feu, fire", "incendie, fire", "blessure, bleed", 
                "sang, bleed", "blut, bleed", "verletzung, bleed",
                "vole, theft", "diebstahl, theft"})
    public void shouldTranslateKeywords(String input, String expected){
        TranslationService translationTest = new TranslationService();
        assertEquals(expected, translationTest.translateToEnglish(input));
    }

    //@ParameterizedTest
    /*@CsvSource({ "vole, theft", "feu, fire" })
    void isBlank_ShouldReturnTrueForNullOrBlankStrings(String input, String expected) {
        TranslationService translation = new TranslationService();
        assertEquals(expected, translation.translateToEnglish(input));
    }*/

}
