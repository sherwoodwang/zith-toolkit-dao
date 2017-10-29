package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.math.BigDecimal;

public class TypeHandlerDictionaryBase {
    public DaoSqlTypeHandler<BigDecimal> handlerOfBigDecimal(String sqlType, int jdbcType) {
        return new BigDecimalHandler(sqlType, jdbcType);
    }

    public DaoSqlTypeHandler<Boolean> handlerOfBoolean(String sqlType, int jdbcType) {
        return new BooleanHandler(sqlType, jdbcType);
    }

    public DaoSqlTypeHandler<Byte> handlerOfByte(String sqlType, int jdbcType) {
        return new ByteHandler(sqlType, jdbcType);
    }

    public DaoSqlTypeHandler<Double> handlerOfDouble(String sqlType, int jdbcType) {
        return new DoubleHandler(sqlType, jdbcType);
    }

    public DaoSqlTypeHandler<Float> handlerOfFloat(String sqlType, int jdbcType) {
        return new FloatHandler(sqlType, jdbcType);
    }

    public DaoSqlTypeHandler<Integer> handlerOfInteger(String sqlType, int jdbcType) {
        return new IntegerHandler(sqlType, jdbcType);
    }

    public DaoSqlTypeHandler<Long> handlerOfLong(String sqlType, int jdbcType) {
        return new LongHandler(sqlType, jdbcType);
    }

    public DaoSqlTypeHandler<Short> handlerOfShort(String sqlType, int jdbcType) {
        return new ShortHandler(sqlType, jdbcType);
    }

    public DaoSqlTypeHandler<String> handlerOfString(String sqlType, int jdbcType) {
        return new StringHandler(sqlType, jdbcType);
    }
}
