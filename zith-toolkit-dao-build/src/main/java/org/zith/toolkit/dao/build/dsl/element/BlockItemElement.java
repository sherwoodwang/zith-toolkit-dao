package org.zith.toolkit.dao.build.dsl.element;

import java.util.Objects;

public class BlockItemElement {
    private final Type type;
    private final PackageScopeElement packageScopeElement;
    private final SqlTupleElement sqlTupleElement;
    private final DeclarationElement declarationElement;
    private final SqlTypeDictionaryElement sqlTypeDictionaryElement;
    private final UtilizationElement utilizationElement;

    public BlockItemElement(PackageScopeElement packageScopeElement) {
        type = Type.PACKAGE_SCOPE;
        this.packageScopeElement = Objects.requireNonNull(packageScopeElement);
        sqlTupleElement = null;
        declarationElement = null;
        utilizationElement = null;
        sqlTypeDictionaryElement = null;
    }

    public BlockItemElement(SqlTupleElement sqlTupleElement) {
        type = Type.SQL_TUPLE;
        this.sqlTupleElement = Objects.requireNonNull(sqlTupleElement);
        packageScopeElement = null;
        declarationElement = null;
        utilizationElement = null;
        sqlTypeDictionaryElement = null;
    }

    public BlockItemElement(DeclarationElement declarationElement) {
        type = Type.DECLARATION;
        this.declarationElement = Objects.requireNonNull(declarationElement);
        packageScopeElement = null;
        sqlTupleElement = null;
        utilizationElement = null;
        sqlTypeDictionaryElement = null;
    }

    public BlockItemElement(UtilizationElement utilizationElement) {
        type = Type.UTILIZATION;
        this.utilizationElement = Objects.requireNonNull(utilizationElement);
        packageScopeElement = null;
        sqlTupleElement = null;
        declarationElement = null;
        sqlTypeDictionaryElement = null;
    }

    public BlockItemElement(SqlTypeDictionaryElement sqlTypeDictionaryElement) {
        type = Type.SQL_TYPE_DICTIONARY;
        this.sqlTypeDictionaryElement = sqlTypeDictionaryElement;
        utilizationElement = null;
        packageScopeElement = null;
        sqlTupleElement = null;
        declarationElement = null;
    }

    public Type getType() {
        return type;
    }

    public PackageScopeElement getPackageScopeElement() {
        return packageScopeElement;
    }

    public SqlTupleElement getSqlTupleElement() {
        return sqlTupleElement;
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
        SQL_TUPLE,
        SQL_TYPE_DICTIONARY,
        UTILIZATION,
    }

    @Override
    public String toString() {
        switch (type) {

            case DECLARATION:
                return "BlockItemElement{" +
                        "type=" + type +
                        ", declarationElement=" + declarationElement +
                        "}";
            case PACKAGE_SCOPE:
                return "BlockItemElement{" +
                        "type=" + type +
                        ", packageScopeElement=" + packageScopeElement +
                        "}";
            case SQL_TUPLE:
                return "BlockItemElement{" +
                        "type=" + type +
                        ", sqlTupleElement=" + sqlTupleElement +
                        "}";
            case SQL_TYPE_DICTIONARY:
                return "BlockItemElement{" +
                        "type=" + type +
                        ", sqlTypeDictionaryElement=" + sqlTypeDictionaryElement +
                        "}";
            case UTILIZATION:
                return "BlockItemElement{" +
                        "type=" + type +
                        ", utilizationElement=" + utilizationElement +
                        "}";
            default:
                return "BlockItemElement{" +
                        "type=" + type +
                        "}";
        }
    }
}
