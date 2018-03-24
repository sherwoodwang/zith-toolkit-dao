package org.zith.toolkit.dao.util.spring;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.CleanupFailureDataAccessException;
import org.springframework.jdbc.support.SqlValue;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A {@link SqlValue} implementation based on {@link DaoSqlTypeHandler}.
 * <p>It's not thread safe.</p>
 *
 * @param <T> The value type of type handler.
 */
public class TypeHandlerBasedSqlValue<T> implements SqlValue {
    private final DaoSqlTypeHandler<T> typeHandler;
    private final T value;

    private DaoSqlOperationContext context;

    private TypeHandlerBasedSqlValue(DaoSqlTypeHandler<T> typeHandler, T value) {
        this.typeHandler = typeHandler;
        this.value = value;
        ensureContext();
    }

    private DaoSqlOperationContext ensureContext() {
        if (context != null) {
            return context;
        }

        context = new DaoSqlOperationContext() {
            private Cleaner cleaner = null;

            @Override
            public void registerCleaner(Cleaner cleaner) {
                if (this.cleaner != null) {
                    throw new UnsupportedOperationException();
                }

                this.cleaner = cleaner;
            }

            @Override
            public void close() throws SQLException {
                if (cleaner != null) {
                    cleaner.clean();
                    cleaner = null;
                }
            }
        };

        return context;
    }

    @Override
    public void setValue(@NotNull PreparedStatement ps, int paramIndex) throws SQLException {
        typeHandler.store(ensureContext(), ps, paramIndex, value);
    }

    @Override
    public void cleanup() {
        try {
            context.close();
        } catch (SQLException e) {
            throw new CleanupFailureDataAccessException("Failed to clean resources created for parameters", e);
        } finally {
            context = null;
        }
    }

    public static <T, U> SqlValue from(DaoSqlTypeHandler<U> typeHandler, Object value_) {
        U value = typeHandler.type().cast(value_);

        return new TypeHandlerBasedSqlValue<>(typeHandler, value);
    }
}
