package org.zith.toolkit.dao.build.dsl.parser;

import org.zith.toolkit.dao.build.dsl.element.*;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

@NotThreadSafe
public class RecordSchemaParser {
    private static final int BUFFER_SIZE = 8192;

    private final Reader reader;
    private final char[] buf;
    private final LinkedList<Cursor> cursors;
    private int rpos, wpos;
    private boolean eof;

    public RecordSchemaParser(Reader reader) {
        this(reader, BUFFER_SIZE);
    }

    RecordSchemaParser(Reader reader, int bufferSize) {
        if (bufferSize < 2) {
            throw new IllegalArgumentException();
        }

        this.reader = reader;
        buf = new char[bufferSize];
        cursors = new LinkedList<>();
        cursors.add(new Cursor(0, 0, 0, 0));
        rpos = 0;
        wpos = 0;
        eof = false;
    }

    public RecordSchemaElement recordSchema() throws IOException, ParserException {
        RecordSchemaHeadElement head = readRecordSchemaHead();
        List<BlockItemElement> items = new LinkedList<>();

        Optional<BlockItemElement> item = readBlockItem();
        while (item.isPresent()) {
            items.add(item.get());
            item = readBlockItem();
        }

        return new RecordSchemaElement(
                head,
                items
        );
    }

    RecordSchemaHeadElement readRecordSchemaHead() throws IOException, ParserException {
        LinkedList<ImportedPathElement> paths = new LinkedList<>();

        for (; ; ) {
            Optional<ImportedPathElement> importedPath = readImportedPath();

            if (!importedPath.isPresent()) {
                break;
            }

            paths.add(importedPath.get());

            readSpaces();
        }

        return new RecordSchemaHeadElement(paths);
    }

    Optional<PackageScopeElement> readPackageScope() throws IOException, ParserException {
        fork();

        if (!readName().filter("package"::equals).isPresent()) {
            discard();
            return Optional.empty();
        }

        merge();

        readSpaces();
        Optional<JavaReferenceElement> packageNameSegment = readJavaReference();

        if (!packageNameSegment.isPresent()) {
            throw parserException("Expecting a package name");
        }

        readSpaces();
        if (!readSequence("{")) {
            throw parserException("Expecting \"{\"");
        }

        List<BlockItemElement> items = new LinkedList<>();

        readSpaces();
        Optional<BlockItemElement> blockItem = readBlockItem();
        while (blockItem.isPresent()) {
            items.add(blockItem.get());

            readSpaces();
            blockItem = readBlockItem();
        }

        readSpaces();
        if (!readSequence("}")) {
            throw parserException("Expecting \"}\"");
        }

        return Optional.of(new PackageScopeElement(
                packageNameSegment.get().getNames(),
                items
        ));
    }

    private Optional<BlockItemElement> readBlockItem() throws IOException, ParserException {
        {
            Optional<UtilizationElement> utilization = readUtilization();

            if (utilization.isPresent()) {
                return Optional.of(new BlockItemElement(utilization.get()));
            }
        }

        {
            Optional<DeclarationElement> declaration = readDeclaration();

            if (declaration.isPresent()) {
                return Optional.of(new BlockItemElement(declaration.get()));
            }
        }

        {
            Optional<SqlTupleElement> tuple = readSqlTuple();

            if (tuple.isPresent()) {
                return Optional.of(new BlockItemElement(tuple.get()));
            }
        }

        {
            Optional<SqlTypeDictionaryElement> sqlTypeDictionary = readSqlTypeDictionary();

            if (sqlTypeDictionary.isPresent()) {
                return Optional.of(new BlockItemElement(sqlTypeDictionary.get()));
            }
        }

        {
            Optional<PackageScopeElement> packageScope = readPackageScope();

            if (packageScope.isPresent()) {
                return Optional.of(new BlockItemElement(packageScope.get()));
            }
        }

        return Optional.empty();
    }

    Optional<ImportedPathElement> readImportedPath() throws IOException, ParserException {
        fork();

        if (!readName().filter("import"::equals).isPresent()) {
            discard();
            return Optional.empty();
        }

        merge();

        readSpaces();

        Optional<String> path = readStrings();

        if (!path.isPresent()) {
            throw parserException("Expecting a string as path");
        }

        return path.map(ImportedPathElement::new);
    }

    Optional<UtilizationElement> readUtilization() throws IOException, ParserException {
        fork();

        if (!readName().filter("use"::equals).isPresent()) {
            discard();
            return Optional.empty();
        }

        merge();

        readSpaces();

        Optional<JavaReferenceElement> absoluteReference = readJavaReference();

        if (!absoluteReference.isPresent()) {
            throw parserException("Expecting an absolute reference");
        }

        fork();

        readSpaces();

        if (!readName().filter("as"::equals).isPresent()) {
            discard();

            return Optional.of(new UtilizationElement(
                    absoluteReference.get(),
                    null
            ));
        }

        merge();

        readSpaces();

        Optional<String> internalName = readJavaIdentifier();

        if (!internalName.isPresent()) {
            throw parserException("Expecting a Java identifier");
        }

        return Optional.of(new UtilizationElement(
                absoluteReference.get(),
                internalName.get()
        ));
    }

    Optional<DeclarationElement> readDeclaration() throws IOException, ParserException {
        fork();

        if (!readName().filter("declare"::equals).isPresent()) {
            discard();
            return Optional.empty();
        }

        merge();

        readSpaces();

        Optional<String> topic = readGeneralizedSymbol();

        if (!topic.isPresent()) {
            throw parserException("Expecting a generalized symbol as topic name");
        }

        readSpaces();

        Optional<String> stringValue = readGeneralizedSymbol();

        if (stringValue.isPresent()) {
            return Optional.of(new DeclarationElement(
                    topic.get(),
                    stringValue.get()
            ));
        }

        readSpaces();

        if (!readSequence("(")) {
            throw parserException("Expecting a \"(\" as the start of a type specifier");
        }

        readSpaces();

        String type;
        {
            Optional<String> optionalType = readName();

            if (!optionalType.isPresent()) {
                throw parserException("Expecting a type specifier");
            }

            type = optionalType.get();
        }

        readSpaces();

        if (!readSequence(")")) {
            throw parserException("Expecting a \")\" as the end of a type specifier");
        }

        readSpaces();

        Object value;

        switch (type) {
            case "java-ref": {
                Optional<JavaReferenceElement> javaReference = readJavaReference();

                if (!javaReference.isPresent()) {
                    throw parserException("Expecting a Java reference");
                }

                value = javaReference.get();
            }
            break;
            default: {
                throw parserException("Unknown type: " + type);
            }
        }

        return Optional.of(new DeclarationElement(
                topic.get(),
                value
        ));
    }

    Optional<SqlTupleElement> readSqlTuple() throws IOException, ParserException {
        fork();

        if (!readName().filter("sql-tuple"::equals).isPresent()) {
            discard();
            return Optional.empty();
        }

        merge();

        readSpaces();
        Optional<String> recordName = readJavaIdentifier();
        if (!recordName.isPresent()) {
            throw parserException("Expecting a Java identifier as record name");
        }

        readSpaces();
        if (!readSequence("{")) {
            throw parserException("Expecting \"{\"");
        }

        LinkedList<ColumnElement> columns = new LinkedList<>();
        Optional<ColumnElement> column;

        for (; ; ) {
            readSpaces();
            column = readSqlTupleColumn();
            if (column.isPresent()) {
                columns.add(column.get());
                readSpaces();
                if (!readSequence(",")) {
                    break;
                }
            } else {
                break;
            }
        }

        if (!readSequence("}")) {
            throw parserException("Expecting a \"}\"");
        }

        return Optional.of(new SqlTupleElement(
                recordName.get(),
                columns
        ));
    }

    Optional<ColumnElement> readSqlTupleColumn() throws IOException, ParserException {
        Optional<String> typeHandlerName = readTypeName();
        if (!typeHandlerName.isPresent()) {
            return Optional.empty();
        }

        readSpaces();
        Optional<String> columnName = readSqlIdentifier();
        if (!columnName.isPresent()) {
            throw parserException("Expecting a SQL identifier as column name");
        }

        return Optional.of(new ColumnElement(
                columnName.get(),
                typeHandlerName.get()
        ));
    }

    private Optional<SqlTypeDictionaryElement> readSqlTypeDictionary() throws IOException, ParserException {
        fork();
        if (!readName().filter("sql-type-dict"::equals).isPresent()) {
            discard();
            return Optional.empty();
        }
        merge();

        readSpaces();

        Optional<String> name = readJavaIdentifier();

        if (!name.isPresent()) {
            throw parserException("Expecting a Java identifier");
        }

        readSpaces();

        if (!readSequence("{")) {
            throw parserException("Expecting \"{\"");
        }

        LinkedList<SqlTypeDictionaryItemElement> items = new LinkedList<>();

        do {
            readSpaces();
            if (readSequence("}")) {
                break;
            }

            readSpaces();
            Optional<JavaReferenceElement> typeReference = readJavaReference();

            if (!typeReference.isPresent()) {
                throw parserException("Expecting a Java reference");
            }

            Optional<String> handlerName;

            fork();
            readSpaces();
            handlerName = readTypeName();
            if (handlerName.isPresent()) {
                merge();
            } else {
                discard();
            }

            readSpaces();
            if (!readSequence(",")) {
                break;
            }

            items.add(new SqlTypeDictionaryItemElement(
                    handlerName.orElse(null),
                    typeReference.get()
            ));
        } while (true);

        return Optional.of(new SqlTypeDictionaryElement(
                name.get(),
                items
        ));
    }

    private Optional<JavaReferenceElement> readJavaReference() throws IOException, ParserException {
        Optional<String> javaIdentifier = readJavaIdentifier();

        if (!javaIdentifier.isPresent()) {
            return Optional.empty();
        }

        LinkedList<String> names = new LinkedList<>();

        names.add(javaIdentifier.get());

        for (; ; ) {
            fork();

            readSpaces();

            if (!readSequence(".")) {
                discard();
                break;
            }

            merge();

            readSpaces();

            javaIdentifier = readJavaIdentifier();

            if (!javaIdentifier.isPresent()) {
                discard();
                throw parserException("Expecting a Java identifier");
            }

            names.add(javaIdentifier.get());
        }

        return Optional.of(new JavaReferenceElement(names));
    }

    private Optional<String> readSqlIdentifier() throws IOException, ParserException {
        Optional<String> strictWord = readSqlString('"');

        if (strictWord.isPresent()) {
            return strictWord;
        }

        return readSqlWord();
    }

    Optional<String> readSqlString(char delimiter) throws ParserException, IOException {
        StringBuilder sb = new StringBuilder();
        int m = 0;

        for (; ; ) {
            int c = 0;

            for (int i = pos(); i < stop(); i++, c++) {
                char ch = buf[i];

                switch (m) {
                    case 0:
                        if (ch == delimiter) {
                            m = 1;
                        } else {
                            return Optional.empty();
                        }
                        break;
                    case 1:
                        if (ch == delimiter) {
                            m = 2;
                        } else {
                            sb.append(ch);
                        }
                        break;
                    case 2:
                        if (ch == delimiter) {
                            sb.append(delimiter);
                            m = 1;
                        } else {
                            move(c);
                            return Optional.of(sb.toString());
                        }
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }

            move(c);

            if (pos() == stop() && eof()) {
                switch (m) {
                    case 0:
                        return Optional.empty();
                    case 1:
                        throw parserException("Unexpected end of file during parsing a string");
                    case 2:
                        return Optional.of(sb.toString());
                    default:
                        throw new IllegalStateException();
                }
            }

            load();
        }
    }

    private Optional<String> readSqlWord() throws IOException {
        return readSymbol(
                ch -> ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_',
                ch -> ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') ||
                        ('0' <= ch && ch <= '9') ||
                        ch == '_'
        );
    }

    private Optional<String> readTypeName() throws IOException {
        return readSymbol(
                Character::isAlphabetic,
                ch -> Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_'
        )
                .map(String::toLowerCase);
    }

    private Optional<String> readJavaIdentifier() throws IOException {
        return readSymbol(Character::isJavaIdentifierStart, Character::isJavaIdentifierPart);
    }

    private Optional<String> readGeneralizedSymbol() throws IOException, ParserException {
        Optional<String> value = readStrings();

        if (!value.isPresent()) {
            value = readName();
        }

        return value;
    }

    Optional<String> readNumber() throws IOException {
        StringBuilder sb = new StringBuilder();
        int m = 0;

        for (; ; ) {
            int c = 0;

            for (int i = pos(); i < stop(); i++, c++) {
                char ch = buf[i];

                switch (m) {
                    case 0:
                        if ('0' == ch) {
                            sb.append(ch);
                            m = 3;
                        } else if ('1' <= ch && ch <= '9') {
                            sb.append(ch);
                            m = 1;
                        } else if (ch == '.') {
                            sb.append(ch);
                            m = 2;
                        } else {
                            return Optional.empty();
                        }
                        break;
                    case 1:
                        if ('1' <= ch && ch <= '9') {
                            sb.append(ch);
                        } else if (ch == '.') {
                            sb.append(ch);
                            m = 2;
                        } else {
                            move(c);
                            return Optional.of(sb.toString());
                        }
                        break;
                    case 2:
                        if ('0' <= ch && ch <= '9') {
                            sb.append(ch);
                        } else {
                            move(c);
                            return Optional.of(sb.toString());
                        }
                        break;
                    case 3:
                        if (ch == 'x' || ch == 'X') {
                            sb.append(Character.toLowerCase(ch));
                            m = 4;
                        } else if ('0' <= ch && ch <= '7') {
                            sb.append(ch);
                            m = 5;
                        } else {
                            move(c);
                            return Optional.of(sb.toString());
                        }
                        break;
                    case 4:
                        if ('0' <= ch && ch <= '9') {
                            sb.append(ch);
                        } else if (('a' <= ch && ch <= 'f') || ('A' <= ch && ch <= 'F')) {
                            sb.append(Character.toLowerCase(ch));
                        } else {
                            move(c);
                            return Optional.of(sb.toString());
                        }
                        break;
                    case 5:
                        if ('0' <= ch && ch <= '7') {
                            sb.append(ch);
                        } else {
                            move(c);
                            return Optional.of(sb.toString());
                        }
                        break;
                }
            }

            move(c);

            if (pos() == stop() && eof()) {
                return Optional.of(sb.toString());
            }

            load();
        }
    }

    Optional<String> readName() throws IOException {
        return readSymbol(
                ch -> ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_',
                ch -> ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') ||
                        ('0' <= ch && ch <= '9') ||
                        ch == '_' || ch == '-'
        );
    }

    private Optional<String> readSymbol(Predicate<Character> isStart, Predicate<Character> isPart) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean start = true;

        for (; ; ) {
            int c = 0;

            for (int i = pos(); i < stop(); i++, c++) {
                char ch = buf[i];

                if (start) {
                    if (!isStart.test(ch)) {
                        return Optional.empty();
                    }

                    start = false;
                } else {
                    if (!isPart.test(ch)) {
                        sb.append(buf, pos(), c);
                        move(c);
                        return Optional.of(sb.toString());
                    }
                }
            }

            sb.append(buf, pos(), c);
            move(c);


            if (pos() == stop() && eof()) {
                if (start) {
                    return Optional.empty();
                } else {
                    return Optional.of(sb.toString());
                }
            }

            load();
        }
    }

    private boolean readSequence(String sequence) throws IOException {
        if (sequence == null || sequence.isEmpty()) {
            throw new IllegalArgumentException();
        }

        fork();

        int p = 0;

        for (; ; ) {
            int c = 0;

            for (int i = pos(); i < stop(); i++, c++) {
                char ch = buf[i];

                if (sequence.charAt(p++) != ch) {
                    discard();
                    return false;
                }

                if (p == sequence.length()) {
                    move(c + 1);
                    merge();
                    return true;
                }
            }

            move(c);

            if (pos() == stop() && eof()) {
                discard();
                return false;
            }

            load();
        }
    }

    Optional<String> readStrings() throws IOException, ParserException {
        Optional<String> string = readString();
        if (string.isPresent()) {
            StringBuilder sb = new StringBuilder();

            do {
                sb.append(string.get());
                readSpaces();
                string = readString();
            } while (string.isPresent());

            return Optional.of(sb.toString());
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> readString() throws IOException, ParserException {
        StringBuilder sb = new StringBuilder();
        int m = 0;

        main_loop:
        for (; ; ) {
            int c = 0;

            for (int i = pos(); i < stop(); i++, c++) {
                char ch = buf[i];

                if (m != 0) {
                    if (ch == '\n') {
                        move(c);
                        throw parserException("Unexpected end of line during parsing a string");
                    }
                }

                switch (m) {
                    case 0:
                        if (ch == '\"') {
                            m = 1;
                        } else {
                            return Optional.empty();
                        }
                        break;
                    case 1:
                        if (ch == '\\') {
                            m = 2;
                        } else if (ch == '"') {
                            move(c + 1);
                            break main_loop;
                        } else {
                            sb.append(ch);
                        }
                        break;
                    case 2:
                        switch (ch) {
                            case 'n':
                                sb.append('\n');
                                break;
                            case '\\':
                            case '"':
                                sb.append(ch);
                                break;
                            default:
                                throw parserException("Unexpected escape character");
                        }
                        m = 1;
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }

            move(c);

            if (pos() == stop() && eof()) {
                if (m == 0) {
                    return Optional.empty();
                } else {
                    throw parserException("Unexpected end of file during parsing a string");
                }
            }

            load();
        }

        return Optional.of(sb.toString());
    }

    private void readSpaces() throws IOException {
        main_loop:
        for (; ; ) {
            int c = 0;

            for (int i = pos(); i < stop(); i++, ++c) {
                char ch = buf[i];

                if (!(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r')) {
                    move(c);
                    break main_loop;
                }
            }

            move(c);

            if (pos() == stop() && eof()) {
                return;
            }

            load();
        }
    }

    private void fork() {
        cursors.push(new Cursor(cursors.getFirst()));
    }

    private void discard() {
        cursors.pop();
    }

    private void merge() {
        Cursor cursor = cursors.pop();
        cursors.pop();
        cursors.push(cursor);
    }

    private boolean eof() {
        return eof;
    }

    private void load() throws IOException {
        if (!eof) {
            int len;

            if (wpos >= rpos) {
                if (rpos == 0) {
                    len = buf.length - wpos - 1;
                } else {
                    len = buf.length - wpos;
                }
            } else {
                len = rpos - wpos - 1;
            }

            if (len == 0 && pos() == stop()) {
                throw new BufferFullException();
            }

            int r = reader.read(buf, wpos, len);

            if (r == -1) {
                eof = true;
            } else {
                wpos = (wpos + r) % buf.length;
            }
        }
    }

    private void move(int c) {
        Cursor cursor = cursors.getFirst();

        if (cursor.pos + c > (wpos >= rpos ? wpos : wpos + buf.length)) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i = 0; i < c; i++) {
            char ch = buf[cursor.pos];

            ++cursor.pos;
            ++cursor.offset;
            ++cursor.column;

            if (cursor.pos == buf.length) {
                cursor.pos = 0;
            }

            if (ch == '\n') {
                cursor.column = 0;
                ++cursor.line;
            }
        }

        Cursor head = cursors.getLast();
        rpos = head.pos;
    }

    private int pos() {
        return cursors.getFirst().pos;
    }

    private int stop() {
        int pos = pos();
        return pos >= rpos ?
                (wpos >= rpos ?
                        wpos :
                        buf.length) :
                (wpos >= rpos ?
                        exceptional(IllegalStateException::new) :
                        wpos);
    }

    private ParserException parserException(String message) {
        Cursor currentCursor = cursors.getFirst();
        return new ParserException(
                currentCursor.offset,
                currentCursor.line,
                currentCursor.column,
                message
        );
    }

    private static <T> T exceptional(Supplier<RuntimeException> exceptionSupplier) {
        throw exceptionSupplier.get();
    }

    private class Cursor {
        private int pos;
        private long offset, line, column;

        Cursor(int pos, long offset, long line, long column) {
            this.pos = pos;
            this.offset = offset;
            this.line = line;
            this.column = column;
        }

        Cursor(Cursor a) {
            this.pos = a.pos;
            this.offset = a.offset;
            this.line = a.line;
            this.column = a.column;
        }
    }
}
