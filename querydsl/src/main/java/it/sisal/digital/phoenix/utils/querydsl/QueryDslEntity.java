package com.lucas.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Define abstract methods to implements in order to use the API provided from AbstractSisalRepository
 *
 * @see AbstractQueryDslRepository
 */
public abstract class QueryDslEntity<R extends RelationalPathBase> {

    private Expression<?>[] select;

    private List<FieldHandler<R>> idFields;
    private List<FieldHandler<R>> allFields;
    private List<QueryDslJoin> oneToOne = new ArrayList<>();
    private List<QueryDslJoin> oneToMany = new ArrayList<>();

    R qEntity;

    private QueryDslEntity() {
        this.idFields = new ArrayList<>();
        this.allFields = new ArrayList<>();
        init();
    }

    public QueryDslEntity(R qEntity) {
        this.idFields = new ArrayList<>();
        this.allFields = new ArrayList<>();
        this.qEntity = qEntity;
        init();
    }

    public List<FieldHandler<R>> getIdFields() {
        return idFields;
    }

    public void setIdFields(List<FieldHandler<R>> idFields) {
        this.idFields = idFields;
    }

    public List<FieldHandler<R>> getAllFields() {
        return allFields;
    }

    public void setAllFields(List<FieldHandler<R>> allFields) {
        this.allFields = allFields;
    }

    public R QType() {
        return qEntity;
    }

    public void setqEntity(R qEntity) {
        this.qEntity = qEntity;
    }

    /** Define association between entity field and the corrisponding Qtype field
     * @param field, QType field
     * @param get, get function of the entity field
     * @param set, set function of the entity field
     * @param isId, identify pk
     **/
    public void field(Expression field,
                      Supplier<Object> get,
                      Consumer<Tuple> set,
                      boolean isId){
        FieldHandler fH = FieldHandler.of(field, get, set, isId);
        if(isId){
            this.getIdFields().add(fH);
        }
        this.getAllFields().add(fH);
    }

    public void field(Expression field,
                      Supplier<Object> get,
                      Consumer<Tuple> set){
        field(field, get, set, false);
    }

    public <Q extends QueryDslEntity> Object[] applyGetMethod(){
        return allFields.stream().map(f -> f.applyGetMethod()).toArray();
    }

    public <Q extends QueryDslEntity> void applySetMethod(Tuple tuple){
        allFields.forEach(f -> f.setMethod(tuple));
    }

    public  BooleanExpression whereEqId(){
        return buildWhere(idFields, false);
    }

    public BooleanExpression whereEqAllNonNull(){
        return buildWhere(allFields, true);
    }

    private BooleanExpression buildWhere(List<FieldHandler<R>> fieldHandlerList, boolean nullable) {
        BooleanExpression result = null;
        for(FieldHandler field : fieldHandlerList){
            Object val = field.applyGetMethod();
            if(val == null && !nullable){
                throw new RuntimeException("Field cannot be null");
            }else if(val != null){
                BooleanExpression exp = ((SimpleExpression)field.getField()).eq(val);
                if(result == null){
                    result = exp;
                }else{
                    result = result.and(exp);
                }
            }
        }
        return result;
    }

    public Expression<?>[] select(){
        HashSet<Expression> select = select(null);
        Expression[] selectArray = {};
        selectArray = select.toArray(selectArray);
        return selectArray;
    }

    private <Q extends QueryDslEntity> HashSet<Expression> select(HashSet<Expression> select){
        if(select == null){
            select = new HashSet<>();
        }
        select.addAll(Arrays.asList(allFields.stream().map(e -> e.getField()).toArray(Expression<?>[]::new)));

        for (QueryDslJoin o : oneToOne) {
            o.getJoined().select(select);
        }

        for (QueryDslJoin o : oneToMany) {
            o.getJoined().select(select);
        }

        return select;
    }

    public abstract <Q extends QueryDslEntity> Q newInstance();

    public void build(Tuple tuple){
        allFields.forEach(f -> f.setMethod(tuple));
    }

    public <Q extends QueryDslEntity> Q create(Tuple tuple, Class<Q> clazz){
        Q newInst = newInstance();
        List<FieldHandler<R>> allFields = newInst.getAllFields();
        allFields.forEach(f -> f.setMethod(tuple));
        return newInst;
    }

    /** create QueryDslEntity from tuple
     * @param tuple, tuple data coming from query execution
     * @param resultMap, map that contains data of previous tuple conversions used to removing duplicate on joined table aggregating data
     * @return entity
     * */
    public <Q extends QueryDslEntity> Q create(Tuple tuple, LinkedHashMap<String, Q> resultMap){
        Q newInst = newInstance();
        List<FieldHandler<R>> allFields = newInst.getAllFields();
        allFields.forEach(f -> f.setMethod(tuple));
        newInst.aggregate(tuple, resultMap);
        return newInst;
    }

    /** Define ont to one join between entities
     * @param joined, table entity to join
     * @param on, sql on clause used to join Qtype fields
     * @param aggregate, aggregate function that define where to insert query result of joined table
     * */
    public <Q extends QueryDslEntity> void joinOneToOne(Q joined, BooleanExpression on, Consumer<Q> aggregate){
        this.oneToOne.add(QueryDslJoin.build(joined, on, aggregate));
    }

    /** Define ont to many join between entities
     * @param joined, table entity to join
     * @param on, sql on clause used to join Qtype fields
     * @param aggregate, aggregate function that define where to insert i-esim query result of joined table
     * */
    public <Q extends QueryDslEntity> void joinOneToMany(Q joined, BooleanExpression on, Consumer<Q> aggregate){
        this.oneToMany.add(QueryDslJoin.build(joined, on, aggregate));
    }

    public List<QueryDslJoin> getOneToOne() {
        return oneToOne;
    }

    public List<QueryDslJoin> getOneToMany() {
        return oneToMany;
    }

    protected abstract void init();

    public <Q extends QueryDslEntity> Map<String, Q> aggregate(Tuple t, LinkedHashMap<String, Q> resultMap){
        if(resultMap == null) {
            resultMap = new LinkedHashMap<>();
        }

        Q inst = (Q) this.newInstance();
        inst.build(t);

        StringBuilder id = new StringBuilder();
        List<FieldHandler> ids = inst.getIdFields();
        ids.forEach(f -> id.append(f.getGetter().get()));
        String idString = id.toString();

        Q r = resultMap.get(idString);
        if(r == null){
            r = inst;
            resultMap.put(idString, r);
        }

        List<QueryDslJoin> oneToOne = r.getOneToOne();
        if(!oneToOne.isEmpty()){

            for (QueryDslJoin o : oneToOne) {
                LinkedHashMap<String, Q> oneToOneMap = new LinkedHashMap<>();
                o.getJoined().aggregate(t, oneToOneMap);
                oneToOneMap.entrySet().forEach(e -> o.getAggregate().accept(e.getValue()));
            }
        }

        List<QueryDslJoin> oneToMany = r.getOneToMany();
        if(!oneToMany.isEmpty()){
            for (QueryDslJoin o : oneToMany) {

                LinkedHashMap<String, Q> oneToManyMap = new LinkedHashMap<>();
                o.getJoined().aggregate(t, oneToManyMap);
                oneToManyMap.entrySet().forEach(e -> o.getAggregate().accept(e.getValue()));
            }

        }
        return resultMap;
    }

    public  SQLQuery<Tuple> join(SQLQuery<Tuple> query){
        if(!oneToOne.isEmpty()){
            for(QueryDslJoin o : oneToOne){
                query = query.leftJoin(o.getJoined().QType()).on(o.getOn());
                query = o.getJoined().join(query);
            }
        }

        if(!oneToMany.isEmpty()){
            for(QueryDslJoin o : oneToMany){
                query = query.leftJoin(o.getJoined().QType()).on(o.getOn());
                query = o.getJoined().join(query);
            }
        }

        return query;
    }
}





