package org.zith.toolkit.dao.build.dsl.parser;

import java.util.Objects;

public class BlockItemElement {
    private final Type type;
    private final PackageScopeElement packageScopeElement;
    private final TupleElement tupleElement;
    private final DeclarationElement declarationElement;
    private final SqlTypeDictionaryElement sqlTypeDictionaryElement;
    private UtilizationElement utilizationElement;

    public BlockItemElement(PackageScopeElement packageScopeElement) {
        type = Type.PACKAGE_SCOPE;
        this.packageScopeElement = Objects.requireNonNull(packageScopeElement);
        tupleElement = null;
        declarationElement = null;
        utilizationElement = null;
        sqlTypeDictionaryElement = null;
    }

    public BlockItemElement(TupleElement tupleElement) {
        type = Type.RECORD_DEFINITION;
        this.tupleElement = Objects.requireNonNull(tupleElement);
        packageScopeElement = null;
        declarationElement = null;
        utilizationElement = null;
        sqlTypeDictionaryElement = null;
    }

    public BlockItemElement(DeclarationElement declarationElement) {
        type = Type.DECLARATION;
        this.declarationElement = Objects.requireNonNull(declarationElement);
        packageScopeElement = null;
        tupleElement = null;
        utilizationElement = null;
        sqlTypeDictionaryElement = null;
    }

    public BlockItemElement(UtilizationElement utilizationElement) {
        type = Type.UTILIZATION;
        this.utilizationElement = Objects.requireNonNull(utilizationElement);
        packageScopeElement = null;
        tupleElement = null;
        declarationElement = null;
        sqlTypeDictionaryElement = null;
    }

    public BlockItemElement(SqlTypeDictionaryElement sqlTypeDictionaryElement) {
        type = Type.TYPE_HANDLER_DICTIONARY_DEFINITION;
        this.sqlTypeDictionaryElement = sqlTypeDictionaryElement;
        utilizationElement = null;
        packageScopeElement = null;
        tupleElement = null;
        declarationElement = null;
    }

    public Type getType() {
        return type;
    }

    public PackageScopeElement getPackageScopeElement() {
        return packageScopeElement;
    }

    public TupleElement getTupleElement() {
        return tupleElement;
    }

    public DeclarationElement getDeclarationElement() {
        return declarationElement;
    }

    public UtilizationElement getUtilizationElement() {
        return utilizationElement;
    }

    public SqlTypeDictionaryElement getSqlTypeDictionaryElement() {
        return sqlTypeDictionaryElement;
    }

    public enum Type {
        DECLARATION,
        PACKAGE_SCOPE,
        RECORD_DEFINITION,
        TYPE_HANDLER_DICTIONARY_DEFINITION,
        UTILIZATION,
    }

    @Override
    public String toString() {
        return "Element{" +
                "type=" + type +
                ", packageScopeElement=" + packageScopeElement +
                ", tupleElement=" + tupleElement +
                '}';
    }
}
