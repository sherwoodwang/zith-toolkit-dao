package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.math.BigDecimal;

public class TypeDictionaryBase {
    public DaoSqlTypeHandler<BigDecimal> handlerOfNumeric() {
        return new NumericHandler();
    }

    public DaoSqlTypeHandler<Boolean> handlerOfBoolean() {
        return new BooleanHandler();
    }

    public DaoSqlTypeHandler<Byte> handlerOfTinyint() {
        return new TinyintHandler();
    }

    public DaoSqlTypeHandler<Double> handlerOfDouble() {
        return new DoubleHandler();
    }

    public DaoSqlTypeHandler<Float> handlerOfFloat() {
        return new FloatHandler();
    }

    public DaoSqlTypeHandler<Integer> handlerOfInteger() {
        return new IntegerHandler();
    }

    public DaoSqlTypeHandler<Long> handlerOfBigint() {
        return new BigintHandler();
    }

    public DaoSqlTypeHandler<Short> handlerOfSmallint() {
        return new SmallintHandler();
    }

    public DaoSqlTypeHandler<String> handlerOfVarchar() {
        return new VarcharHandler();
    }
}
