package com.lucas.querydsl;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An abstract repository that allows CRUD operations via QueryDsl.
 * It uses instances of abstract class QueryDslEntity
 */
@Repository
public class AbstractQueryDslRepository<T extends QueryDslEntity> implements SisalRepository<T> {

    private T entityInstance;
    protected SQLQueryFactory factory;
    protected Logger logger;

    public AbstractQueryDslRepository() {
        this.entityInstance = null;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Create an instance of QueryDslEntity that will be used for getting all static contents of the provided class
     */
    public AbstractQueryDslRepository(SQLQueryFactory factory, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        this.entityInstance = clazz.newInstance();
        this.entityInstance.init();
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.factory = factory;
    }

    public AbstractQueryDslRepository(SQLQueryFactory factory) {
        this.entityInstance = null;
        this.entityInstance.init();
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.factory = factory;
    }

    public void setEntityInstance(T entityInstance){
        this.entityInstance = entityInstance;
        this.entityInstance.init();
    }



    private Optional<T> getFirst(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    @Override
    public List<T> findOrderBy(OrderSpecifier<?>... orderBy) {
        return this.find(null, null, false, null, false, orderBy);

    }

    @Override
    public List<T> findOrderByLimit(Integer limit, OrderSpecifier<?>... orderBy) {
        return this.find(null, null, false, limit, false, orderBy);

    }

    @Override
    public List<T> findAll() {
        return this.find(null, null, false, null, false, null);

    }

    @Override
    public Optional<T> findById(BooleanExpression where, T entity) {
        return getFirst(this.find(where, entity, true, null,  false, null));
    }

    @Override
    public Optional<T> findById(T entity) {
        return getFirst(this.find(null, entity, true, null, false, null));

    }

    @Override
    public List<T> find(BooleanExpression where, T entity) {
        return this.find(where, entity, false, null, false, null);

    }

    @Override
    public List<T> find(BooleanExpression where) {
        return this.find(where, null, false, null, false, null);

    }

    @Override
    public List<T> find(T entity) {
        return this.find(null, entity, false, null, false, null);

    }

    @Override
    public List<T> find(BooleanExpression where, final T entity, boolean byId, Integer limit, boolean forUpdate, OrderSpecifier<?>... orderBy) {
        LinkedHashMap<String, T> resultMap = new LinkedHashMap<>();

        try {
            if (entity != null) {
                BooleanExpression exp = entity.whereEqAllNonNull();
                where = where == null ? exp : where.and(exp);

                if (byId) {
                    where = entity.whereEqId();
                }
            }

            SQLQuery<Tuple> select = getSQLQuery(where, limit, forUpdate, orderBy);

            //avoid fetchOne in order to handle Mysql integration table aliases problem
            List<Tuple> list = select.fetch();
            if (list != null) {
                if (list.size() > 1 && byId) {
                    throw new NonUniqueResultException();
                } else {
                    list.forEach(t -> this.entityInstance.create(t, resultMap));
                }
            }

        } catch (EmptyResultDataAccessException e) {
            logger.warn("No data returned");
        }
        return resultMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }


    private SQLQuery<Tuple> getSQLQuery(Predicate where, Integer limit, boolean forUpdate, OrderSpecifier<?>... orderBy) {
        SQLQuery<Tuple> query = factory
                .select(this.entityInstance.select())
                .from(this.entityInstance.QType());

        this.entityInstance.join(query);

        if (where != null) {
            query = query.where(where);
        }

        if (orderBy != null) {
            query = query.orderBy(orderBy);
        }
        if (limit != null) {
            query = query.limit(limit);
        }

        queryLogging(query);

        if (forUpdate) {
            query = query.forUpdate();
        }

        return query;
    }

    @Override
    public Optional<T> findById(T entity, boolean forUpdate) {
        return getFirst(this.find(null, entity, true, null, forUpdate, null));
    }

    @Override
    public boolean save(T entity) {
        Object[] values = entity.applyGetMethod();

        SQLInsertClause insert = factory
                .insert(entity.QType())
                .values(values);
        queryLogging(insert, values);
        return insert.execute() > 0;
    }

    @Override
    public boolean delete(T entity) {
        BooleanExpression where = entity.whereEqId();
        SQLDeleteClause delete = factory
                .delete(entity.QType())
                .where(where);

        queryLogging(delete, where);

        return delete.execute() > 0;
    }

    @Override
    public boolean update(T entity) {
        return update(entity, entity.whereEqId(), false, false);
    }

    @Override
    public boolean update(T entity, boolean setNullValue) {
        return update(entity, entity.whereEqId(), false, setNullValue);
    }

    @Override
    public boolean update(T entity, BooleanExpression where) {
        return update(entity, where, false, false);
    }

    @Override
    public boolean updateWhereId(T entity) {
        return update(entity, entity.whereEqId(), true, false);
    }

    @Override
    public boolean updateWhereId(T entity, boolean setNullValue) {
        return update(entity, entity.whereEqId(), true, setNullValue);
    }

    @Override
    public boolean updateWhereId(T entity, BooleanExpression where) {
        return update(entity, where, true, false);
    }

    /**
     * Update table using entity values. Will never update primary key columns as defined in the entity
     *
     * @param entity,       contains the value to be set
     * @param where,        where condition of update
     * @param byId,         if true use the values of entity that realize the primary key as where condition
     * @param setNullValue, if true set the eventually null values of entity's parameters
     */
    @Override
    public boolean update(T entity, BooleanExpression where, boolean byId, boolean setNullValue) {
        final SQLUpdateClause update = factory
                .update(this.entityInstance.QType())
                .where(where);

        List<FieldHandler> fields = entity.getAllFields();
        if (setNullValue) {
            fields.stream().filter(f -> !f.isId()).forEach(e -> update.set((Path) e.getField(), e.applyGetMethod()));
        } else {
            fields.stream().filter(f -> !f.isId()).forEach(e -> {
                Object val = e.applyGetMethod();
                if (val != null) {
                    update.set((Path) e.getField(), val);
                }
            });
        }
        if (byId) {
            where = where != null ? where.and(entity.whereEqId()) : entity.whereEqId();

        }
        queryLogging(update, where);
        return update.execute() > 0;
    }

    @Override
    /** Commons query logging using sql and parameter objects
     * @param sql, query
     * @param params, query parameters */
    public void queryLogging(Object sql, Object... params) {
        if(logger.isInfoEnabled()){
            String query = "SQL: " + sql.toString().replace("?", "{}");
            if(SQLQuery.class.isAssignableFrom(sql.getClass())){
                List<Object> paramsList = ((SQLQuery) sql).getSQL().getBindings();
                params = new Object[paramsList.size()];
                paramsList.toArray(params);
            }
            logger.info(query, params);
        }
    }
}
