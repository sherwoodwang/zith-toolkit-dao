package org.zith.toolkit.dao.support;

import java.sql.SQLException;

/**
 * The context of execution.
 * <p>It's not thread safe.</p>
 */
public interface DaoSqlOperationContext extends AutoCloseable {
    void registerCleaner(Cleaner cleaner);

    @Override
    void close() throws SQLException;

    interface Cleaner {
        void clean() throws SQLException;
    }
}
