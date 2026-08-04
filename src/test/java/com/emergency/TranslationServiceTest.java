package com.emergency;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.emergency.service.TranslationService;

public class TranslationServiceTest {
    @Test
    public void shouldTranslateFrenchKeywordToEnglish() {
        TranslationService translation = new TranslationService();
        assertTrue(translation.translateToEnglish("Il y a un feu chez moi").contains("fire"));
    }
}
