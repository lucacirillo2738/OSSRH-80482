package it.sisal.digital.phoenix.utils.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;
import java.util.Optional;

public interface SisalRepository<T extends QueryDslEntity> {

    List<T> findAll();

    List<T> find(BooleanExpression where, final T entity, boolean byId, Integer limit, boolean forUpdate, OrderSpecifier<?>... orderBy);

    List<T> findOrderBy(OrderSpecifier<?>... orderBy);

    List<T> findOrderByLimit(Integer limit, OrderSpecifier<?>... orderBy);

    Optional<T> findById(BooleanExpression where, T entity);

    Optional<T> findById(T entity, boolean forUpdate);

    Optional<T> findById(T entity);

    List<T> find(BooleanExpression where, T entity);

    List<T> find(BooleanExpression where);

    List<T> find(T entity);


    boolean save(T entity);

    boolean delete(T entity);

    boolean update(T entity);

    boolean update(T entity, boolean setNullValue);

    boolean update(T entity, BooleanExpression where);

    boolean update(T entity, BooleanExpression where, boolean byId, boolean setNullValue);

    boolean updateWhereId(T entity);

    boolean updateWhereId(T entity, boolean setNullValue);

    boolean updateWhereId(T entity, BooleanExpression where);

    /**
     * Commons query logging using sql and parameter objects
     *
     * @param sql,    query
     * @param params, query parameters
     */
    void queryLogging(Object sql, Object... params);

}
