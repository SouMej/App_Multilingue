package com.events;

import com.events.entity.Event;
import com.events.entity.EventTranslation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests unitaires — Event translations")
class EventTranslationTest {

    @Test
    @DisplayName("Retourne la traduction demandée")
    void getTranslation_requestedLanguage() {
        Event event = new Event();
        event.addTranslation(new EventTranslation(EventTranslation.Language.FR, "Titre FR", "Desc FR"));
        event.addTranslation(new EventTranslation(EventTranslation.Language.EN, "Title EN", "Desc EN"));

        EventTranslation translation = event.getTranslation("en");

        assertThat(translation).isNotNull();
        assertThat(translation.getLanguage()).isEqualTo(EventTranslation.Language.EN);
        assertThat(translation.getTitle()).isEqualTo("Title EN");
    }

    @Test
    @DisplayName("Fallback vers FR si langue absente")
    void getTranslation_fallsBackToFrench() {
        Event event = new Event();
        event.addTranslation(new EventTranslation(EventTranslation.Language.FR, "Titre FR", "Desc FR"));

        EventTranslation translation = event.getTranslation("ar");

        assertThat(translation).isNotNull();
        assertThat(translation.getLanguage()).isEqualTo(EventTranslation.Language.FR);
    }
}
