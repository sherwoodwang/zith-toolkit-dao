package org.zith.toolkit.dao.util.typehandler;

import java.sql.Timestamp;
import java.time.Instant;

public class InstantFromTimstampHandler extends ConvertingDaoSqlTypeHandler<Instant, Timestamp> {
    protected InstantFromTimstampHandler(String sqlType, int jdbcType) {
        super(new TimestampHandler(sqlType, jdbcType));
    }

    @Override
    protected Instant load(Timestamp value) {
        return value == null ? null : value.toInstant();
    }

    @Override
    protected Timestamp store(Instant value) {
        return value == null ? null : Timestamp.from(value);
    }

    @Override
    public Class<Instant> type() {
        return Instant.class;
    }
}
