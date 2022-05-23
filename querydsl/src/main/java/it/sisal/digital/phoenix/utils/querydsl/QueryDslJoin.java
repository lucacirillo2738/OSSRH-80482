package it.sisal.digital.phoenix.utils.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.function.Consumer;

public class QueryDslJoin<Q extends QueryDslEntity> {
    private Q joined;
    private BooleanExpression on;
    private Consumer<QueryDslJoin> aggregate;

    public QueryDslJoin(Q joined, BooleanExpression on, Consumer<QueryDslJoin> aggregate) {
        this.joined = joined;
        this.on = on;
        this.aggregate = aggregate;
    }

    public static <Q extends QueryDslEntity> QueryDslJoin build(Q joined, BooleanExpression on, Consumer<Q> aggregate){
        return new QueryDslJoin(joined, on, aggregate);
    }

    public Q getJoined() {
        return joined;
    }

    public BooleanExpression getOn() {
        return on;
    }

    public Consumer<QueryDslJoin> getAggregate() {
        return aggregate;
    }

}
