package org.zith.toolkit.dao.build.dsl.parser;

import java.util.Objects;

public class DeclarationElement {
    private final String topic;
    private final Object value;

    public DeclarationElement(String topic, Object value) {
        this.topic = Objects.requireNonNull(topic);
        this.value = value;
    }

    public String getTopic() {
        return topic;
    }

    public Object getValue() {
        return value;
    }
}
