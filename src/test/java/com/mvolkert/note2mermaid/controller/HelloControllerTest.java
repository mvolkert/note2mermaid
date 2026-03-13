package com.mvolkert.note2mermaid.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HelloController Unit Tests")
class HelloControllerTest {

    private final HelloController helloController = new HelloController();

    @Test
    @DisplayName("should return 'hi' when sayHi is called")
    void shouldReturnHi() {
        // When
        String result = helloController.sayHi();

        // Then
        assertThat(result).isEqualTo("hi");
    }
}
