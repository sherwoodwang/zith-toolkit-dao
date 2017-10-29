grammar RecordSchema;

@header {
package org.zith.toolkit.dao.build.dsl.parser;

import java.lang.StringBuilder;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.LinkedList;
}

recordSchema returns [RecordSchemaElement element]
    @init { List<String> importationPaths = new LinkedList<>(); }
    : (importation { importationPaths.add($importation.path); }) *
      block
        { $element = new RecordSchemaElement(importationPaths, $block.elements); }
    ;

importation returns [String path]
    : identifier { "import".equals($identifier.name) }? string { $path = $string.value; }
    ;

block returns [List<BlockItemElement> elements]
    @init { $elements = new LinkedList<>(); }
    : (blockItemElement { $elements.add($blockItemElement.element); }) *
    ;

blockItemElement returns [BlockItemElement element]
    : utilization
        { $element = new BlockItemElement($utilization.element); }
    | declaration
        { $element = new BlockItemElement($declaration.element); }
    | packageScope
        { $element = new BlockItemElement($packageScope.element); }
    | tupleDefinition
        { $element = new BlockItemElement($tupleDefinition.element); }
    | typeDictionaryDefinition
        { $element = new BlockItemElement($typeDictionaryDefinition.element); }
    ;

utilization returns [UtilizationElement element]
    @init {
        JavaFarReferenceElement externalReference = null;
        String internalName = null;
    }
    : identifier { "use".equals($identifier.name) }? javaFarReference
        { externalReference = $javaFarReference.reference; }
      (identifier { "as".equals($identifier.name) }? javaName
          { internalName = $javaName.name; } )?
        { $element = new UtilizationElement(externalReference, internalName); }
    ;

declaration returns [DeclarationElement element]
    : identifier { "declare".equals($identifier.name) }? topic = identifier declarationValue
        { $element = new DeclarationElement($topic.name, $declarationValue.value); };


packageScope returns [PackageScopeElement element]
    : identifier { "package".equals($identifier.name) }? packageNameSegement
      LBRACE def = block RBRACE
        { $element = new PackageScopeElement($packageNameSegement.comps, $def.elements); }
    ;

tupleDefinition returns [TupleElement element]
    : identifier { "sql-tuple".equals($identifier.name) }? recordName LBRACE def = tupleDefinitionItemList RBRACE
        { $element = new TupleElement($recordName.name, $def.fields); }
    ;

declarationValue returns [Object value]
    : javaReference { $value = $javaReference.reference; }
    ;

packageNameSegement returns [List<String> comps]
    @init { $comps = new LinkedList<>(); }
    : javaName { $comps.add($javaName.name); } (DOT javaName { $comps.add($javaName.name); }) +
    ;

recordName returns [String name]
    : javaName { $name = $javaName.name; }
    ;

tupleDefinitionItemList returns [List<ColumnElement> fields]
    @init { $fields = new LinkedList<>(); }
    : columnDefinition { $fields.add($columnDefinition.element); }
      (COMMA columnDefinition { $fields.add($columnDefinition.element); }) *
      COMMA ?
    ;

tupleDefinitionItem
    : columnDefinition
    | tupleDefinitionInclusion
    ;

tupleDefinitionInclusion
    : BANG symbol { "include".equals($symbol.name) }? (
          javaReference LBRACE ASTERISK RBRACE
        | javaReference LBRACE tupleDefinitionInclusionColumnList RBRACE
      )
    ;

tupleDefinitionInclusionColumnList
    : columnName
    | columnName ARROW columnName
    ;

columnDefinition returns [ColumnElement element]
    : columnName sqlType
        { $element = new ColumnElement($columnName.name, $sqlType.element); }
    ;

columnName returns [String name]
    : sqlWord
        { $name = $sqlWord.text; }
    ;

sqlType returns [SqlTypeElement element]
    : sqlTypeRoot
        { $element = new SqlTypeElement($sqlTypeRoot.words); }
    | sqlTypeRoot sqlTypePrecision
        { $element = new SqlTypeElement($sqlTypeRoot.words, $sqlTypePrecision.precision); }
    ;

sqlTypeRoot returns [List<String> words]
    @init { $words = new LinkedList<>(); }
    : (sqlWord { $words.add($sqlWord.text); } ) +
    ;

sqlTypePrecision returns [List<Integer> precision]
    @init { $precision = new LinkedList<>(); }
    : LPARENTHESE sqlNumber { $precision.add($sqlNumber.number.intValueExact()); }
      (COMMA sqlNumber { $precision.add($sqlNumber.number.intValueExact()); }) * RPARENTHESE
    ;

sqlNumber returns [BigDecimal number]
    : NUMBER
        { $number = new BigDecimal($NUMBER.text); }
    ;

sqlWord returns [String text]
    : symbol
        { $text = $symbol.name; }
    ;

typeDictionaryDefinition returns [SqlTypeDictionaryElement element]
    : identifier { "sql-type-dictionary".equals($identifier.name) }? name = sqlTypeDictionaryName LBRACE
        def = sqlTypeDictionaryItemList
      RBRACE
      { $element = new SqlTypeDictionaryElement($name.name, $def.typeHandlerDeclarations); }
    ;

sqlTypeDictionaryName returns [String name]
    : javaName { $name = $javaName.name; }
    ;

sqlTypeDictionaryItemList returns [List<SqlTypeDictionaryItemElement> typeHandlerDeclarations]
    @init { $typeHandlerDeclarations = new LinkedList<>(); }
    : (sqlTypeDictionaryItem
        { $typeHandlerDeclarations.addAll($sqlTypeDictionaryItem.elements); }
      (SEMICOLON sqlTypeDictionaryItem
        { $typeHandlerDeclarations.addAll($sqlTypeDictionaryItem.elements); }) *
      SEMICOLON ?) ?
    ;

sqlTypeDictionaryItem returns [List<SqlTypeDictionaryItemElement> elements]
    @init {
        $elements = new LinkedList<>();
    }
    : sqlTypeHandlerDeclaration
        { $elements.add(new SqlTypeDictionaryItemElement($sqlTypeHandlerDeclaration.element)); }
    | sqlTypeSelectorDeclaration
        {
            $sqlTypeSelectorDeclaration.elements.stream()
                .map(SqlTypeDictionaryItemElement::new)
                .forEach($elements ::add);
        }
    ;

sqlTypeHandlerDeclaration returns [SqlTypeHandlerElement element]
    @init {
        JavaReferenceElement type = null;
        String name = null;
    }
    : javaReference { type = $javaReference.reference; } (javaName { name = $javaName.name; } ) ?
        { $element = new SqlTypeHandlerElement(type, name); }
    ;

sqlTypeSelectorDeclaration returns [List<SqlTypeSelectorElement> elements]
    @init {
        List<SqlTypePatternElement> sqlTypePatterns = new LinkedList<>();
    }
    : sqlTypePattern { sqlTypePatterns.add($sqlTypePattern.element); }
      (COMMA sqlTypePattern { sqlTypePatterns.add($sqlTypePattern.element); }) * COMMA ?
      ARROW javaName
        {
            $elements = sqlTypePatterns.stream()
                .map(sqlType -> new SqlTypeSelectorElement($javaName.name, sqlType))
                .collect(Collectors.toList());
        }
    ;

sqlTypePattern returns [SqlTypePatternElement element]
    : sqlTypeRoot
        { $element = new SqlTypePatternElement($sqlTypeRoot.words); }
    | sqlTypeRoot parameterDictionary
        { $element = new SqlTypePatternElement($sqlTypeRoot.words, $parameterDictionary.value); }
    ;

parameterDictionary returns [HashMap<String, Object> value]
    @init {
        $value = new HashMap<>();
    }
    : LBRACE
      (symbol COLON string { $value.put($symbol.name, $string.value); }) *
      RBRACE
    ;

javaReference returns [JavaReferenceElement reference]
    @init { List<String> names = new LinkedList<>();}
    : javaName { names.add($javaName.name); } (DOT javaName { names.add($javaName.name); })*
      { $reference = new JavaReferenceElement(names); }
    ;

javaFarReference returns [JavaFarReferenceElement reference]
    @init {
        int upper = -1;
        List<String> names = new LinkedList<>();
    }
    : (DOT { if (upper == -1) upper = 1; else ++upper;}) *
      javaName { names.add($javaName.name); } (DOT javaName { names.add($javaName.name); })*
      { $reference = new JavaFarReferenceElement(upper, names); }
    ;

javaName returns [String name]
    : symbol { Helper.isJavaName($symbol.name) }?
        { $name = $symbol.name; }
    ;

identifier returns [String name]
    : symbol { $name = $symbol.name; }
    | { StringBuilder nameBuilder = new StringBuilder(); }
      LBRACKET (symbol { nameBuilder.append($symbol.name); nameBuilder.append('-'); } ) + RBRACKET
      { if (nameBuilder.length() > 0) nameBuilder.setLength(nameBuilder.length() - 1); $name = nameBuilder.toString(); }
    ;

symbol returns [String name]
    : SYMBOL
        { $name = $SYMBOL.text; }
    ;

string returns [String value]
    : STRING
        { $value = Helper.decodeString($STRING.text); }
    ;

COMMENT
    : '//' (~[\r\n]) * {_input.LA(1) == '\r' || _input.LA(1) == '\n'}?  -> channel (HIDDEN);

NUMBER
    : ('0'..'9') + ('.' ('0'..'9') *) ?
    | '.' ('0'..'9') +;

STRING: '"' (~('\\'|'"'|'\r'|'\n')|('\\\\'|'\\"'|'\\r'|'\\n'))* '"';

SYMBOL: (('a'..'z')|('A'..'Z')|'_')(('a'..'z')|('A'..'Z')|'_'|('0'..'9')) *;

ARROW: '=>';

LBRACE: '{';

RBRACE: '}';

LPARENTHESE: '(';

RPARENTHESE: ')';

LBRACKET: '[';

RBRACKET: ']';

COMMA: ',';

COLON: ':';

SEMICOLON: ';';

DOT: '.';

BANG: '!';

ASTERISK: '*';

WS: [ \r\n\t] + -> channel (HIDDEN);