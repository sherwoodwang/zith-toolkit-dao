package org.zith.toolkit.dao.build.dsl.parser;

import org.junit.Test;
import org.zith.toolkit.dao.build.dsl.element.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class RecordSchemaParserTest {
    @Test
    public void test_parsing_string() throws IOException, ParserException {
        for (int bs = 2; bs < 100; bs++) {
            RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                    "\"test \\\"\"   \n" +
                            "\t  \"string\\\"\""
            ), bs);

            assertEquals(Optional.of("test \"string\""), parser.readStrings());
        }
    }

    @Test
    public void test_parsing_symbol() throws IOException, ParserException {
        for (int bs = 2; bs < 100; bs++) {
            RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                    "sql-tuple something else"
            ), bs);

            assertEquals(Optional.of("sql-tuple"), parser.readName());
        }
    }

    @Test
    public void test_parsing_importation_1() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "import \"interest things\""
        ));

        assertEquals(Optional.of(new ImportedPathElement("interest things")), parser.readImportedPath());
    }

    @Test
    public void test_parsing_importation_2() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "not-import \"interest things\""
        ));

        assertEquals(Optional.empty(), parser.readImportedPath());
    }

    @Test
    public void test_parsing_record_schema_head_1() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "" +
                        "import \"file1\"\n" +
                        "import \"file2\"\n" +
                        "\n" +
                        "import \"file3\""
        ));

        assertEquals(new RecordSchemaHeadElement(
                Arrays.asList(
                        new ImportedPathElement("file1"),
                        new ImportedPathElement("file2"),
                        new ImportedPathElement("file3")
                )
        ), parser.readRecordSchemaHead());
    }

    @Test
    public void test_parsing_record_schema_head_2() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "" +
                        "import \"file1\"\n" +
                        "import \"file2\"\n" +
                        "\n" +
                        "import \"file3\"\n" +
                        "sth else"
        ));

        assertEquals(new RecordSchemaHeadElement(
                Arrays.asList(
                        new ImportedPathElement("file1"),
                        new ImportedPathElement("file2"),
                        new ImportedPathElement("file3")
                )
        ), parser.readRecordSchemaHead());
    }

    @Test
    public void test_parsing_utilization() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "" +
                        "use java.lang.String as STRING\n"
        ));

        assertEquals(Optional.of(new UtilizationElement(
                new JavaReferenceElement(Arrays.asList("java", "lang", "String")),
                "STRING"
        )), parser.readUtilization());
    }

    @Test
    public void test_parsing_declaration_1() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "" +
                        "declare default-sql-type-dict (java-ref) org.zith.Test\n"
        ));

        assertEquals(Optional.of(new DeclarationElement(
                "default-sql-type-dict",
                new JavaReferenceElement(Arrays.asList("org", "zith", "Test"))
        )), parser.readDeclaration());
    }

    @Test
    public void test_parsing_declaration_2() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "" +
                        "declare \"test\" WHAT\n"
        ));

        assertEquals(Optional.of(new DeclarationElement(
                "test",
                "WHAT"
        )), parser.readDeclaration());
    }

    @Test
    public void test_parsing_sql_string_1() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "'test ''string'''"
        ));

        assertEquals(Optional.of("test 'string'"), parser.readSqlString('\''));
    }

    @Test
    public void test_parsing_sql_string_2() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "'test ''string'''xxx"
        ));

        assertEquals(Optional.of("test 'string'"), parser.readSqlString('\''));
    }

    @Test
    public void test_parsing_number_1() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "0 "
        ));

        assertEquals(Optional.of("0"), parser.readNumber());
    }

    @Test
    public void test_parsing_number_2() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "0"
        ));

        assertEquals(Optional.of("0"), parser.readNumber());
    }

    @Test
    public void test_parsing_number_3() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "123"
        ));

        assertEquals(Optional.of("123"), parser.readNumber());
    }

    @Test
    public void test_parsing_number_4() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                ".123"
        ));

        assertEquals(Optional.of(".123"), parser.readNumber());
    }

    @Test
    public void test_parsing_number_5() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "123.456"
        ));

        assertEquals(Optional.of("123.456"), parser.readNumber());
    }

    @Test
    public void test_parsing_number_6() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "01238"
        ));

        assertEquals(Optional.of("0123"), parser.readNumber());
    }

    @Test
    public void test_parsing_number_7() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "0x1abf"
        ));

        assertEquals(Optional.of("0x1abf"), parser.readNumber());
    }

    @Test
    public void test_parsing_sql_tuple_column_1() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "id BIGINT"
        ));

        assertEquals(Optional.of(new ColumnElement(
                "id",
                new SqlTypeElement(
                        Collections.singletonList("BIGINT")
                )
        )), parser.readSqlTupleColumn());
    }

    @Test
    public void test_parsing_sql_tuple_column_2() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "amount DECIMAL (22, 2)"
        ));

        assertEquals(Optional.of(new ColumnElement(
                "amount",
                new SqlTypeElement(
                        Collections.singletonList("DECIMAL"),
                        Arrays.asList(22, 2)
                )
        )), parser.readSqlTupleColumn());
    }

    @Test
    public void test_parsing_sql_tuple_1() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "sql-tuple Test {\n" +
                        "id BIGINT,\n" +
                        "amount DECIMAL(22, 2),\n" +
                        "modification TIMESTAMP WITH TIME ZONE (6),\n" +
                        "\"a very \"\"SpEcIaL\"\" column\" TEXT,\n" +
                        "}"
        ));

        assertEquals(Optional.of(
                new SqlTupleElement(
                        "Test",
                        Arrays.asList(
                                new ColumnElement(
                                        "id",
                                        new SqlTypeElement(
                                                Collections.singletonList("BIGINT")
                                        )
                                ),
                                new ColumnElement(
                                        "amount",
                                        new SqlTypeElement(
                                                Collections.singletonList("DECIMAL"),
                                                Arrays.asList(22, 2)
                                        )
                                ),
                                new ColumnElement(
                                        "modification",
                                        new SqlTypeElement(
                                                Arrays.asList("TIMESTAMP", "WITH", "TIME", "ZONE"),
                                                Collections.singletonList(6)
                                        )
                                ),
                                new ColumnElement(
                                        "a very \"SpEcIaL\" column",
                                        new SqlTypeElement(
                                                Collections.singletonList("TEXT")
                                        )
                                )
                        )
                )
        ), parser.readSqlTuple());
    }

    @Test
    public void test_parsing_package_scope_1() throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(new StringReader(
                "" +
                        "package org.zith.test {\n" +
                        "   use java.lang.Long\n" +
                        "   use java.lang.String\n" +
                        "   use java.math.BigDecimal\n" +
                        "\n" +
                        "   sql-type-dict TypeDictionary {\n" +
                        "       [BigDecimal](DECIMAL),\n" +
                        "       [Long](BIGINT),\n" +
                        "       [String](" +
                        "           TEXT { jdbcType: \"VARCHAR\" }," +
                        "           VARCHAR" +
                        "       )\n" +
                        "   }\n" +
                        "\n" +
                        "   declare default-sql-type-dict (java-ref) org.zith.TypeDictionary\n" +
                        "\n" +
                        "   sql-tuple Tuple {\n" +
                        "       id BIGINT,\n" +
                        "       amount DECIMAL(22, 12),\n" +
                        "   }\n" +
                        "}"
        ));

        Optional<PackageScopeElement> packageScopeElement = parser.readPackageScope();
        System.out.println(packageScopeElement);
    }
}
