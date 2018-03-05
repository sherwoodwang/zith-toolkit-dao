package org.zith.toolkit.dao.build.dsl.element;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeclarationElement that = (DeclarationElement) o;
        return Objects.equals(topic, that.topic) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(topic, value);
    }

    @Override
    public String toString() {
        return "DeclarationElement{" +
                "topic='" + topic + '\'' +
                ", value=" + value +
                '}';
    }
}
