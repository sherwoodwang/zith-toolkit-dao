package org.zith.toolkit.dao.build.dsl.parser;

public class ParserException extends Exception {

    private final long offset;
    private final long line;
    private final long column;

    public ParserException(long offset, long line, long column, String message) {
        super(String.format("(%d:%d; %d): ", line, column, offset) + message);
        this.offset = offset;
        this.line = line;
        this.column = column;
    }
}
