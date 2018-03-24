package org.zith.toolkit.dao.util.typehandler;

import java.sql.Timestamp;
import java.time.Instant;

public class InstantFromTimestampHandler extends ConvertingDaoSqlTypeHandler<Instant, Timestamp> {
    protected InstantFromTimestampHandler() {
        super(new TimestampHandler());
    }

    @Override
    protected Instant unpack(Timestamp value) {
        return value == null ? null : value.toInstant();
    }

    @Override
    protected Timestamp pack(Instant value) {
        return value == null ? null : Timestamp.from(value);
    }

    @Override
    public Class<Instant> type() {
        return Instant.class;
    }
}
