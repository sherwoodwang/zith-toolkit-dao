package org.zith.toolkit.dao.build.dsl.element;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

public class RecordSchemaElement {
    private final RecordSchemaHeadElement head;
    private final List<BlockItemElement> elements;

    public RecordSchemaElement(RecordSchemaHeadElement head, List<BlockItemElement> elements) {
        this.head = Objects.requireNonNull(head);
        this.elements = ImmutableList.copyOf(elements);
    }

    public RecordSchemaHeadElement getHead() {
        return head;
    }

    public List<BlockItemElement> getElements() {
        return elements;
    }
}
