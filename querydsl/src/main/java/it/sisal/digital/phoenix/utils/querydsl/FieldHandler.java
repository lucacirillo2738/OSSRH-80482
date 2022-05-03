package it.sisal.digital.phoenix.utils.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.RelationalPathBase;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FieldHandler<R extends RelationalPathBase> {

    boolean isId;
    Expression field;
    Supplier<Object> getter;
    Consumer<Tuple> setter;

    public static <R extends RelationalPathBase> FieldHandler of(Expression field,
                                                                                                                 Supplier<Object> get,
                                                                                                                 Consumer<Tuple> set,
                                                                                                                 boolean isId) {

        FieldHandler result = new FieldHandler<>();
        result.setField(field);
        result.setGetter(get);
        result.setSetter(set);
        result.setId(isId);
        return result;
    }

    public boolean isId() {
        return isId;
    }

    public void setId(boolean id) {
        isId = id;
    }

    public Expression getField() {
        return field;
    }

    public void setField(Expression field) {
        this.field = field;
    }

    public Supplier<Object> getGetter() {
        return getter;
    }

    public void setGetter(Supplier<Object> getter) {
        this.getter = getter;
    }

    public Consumer<Tuple> getSetter() {
        return setter;
    }

    public void setSetter(Consumer<Tuple> setter) {
        this.setter = setter;
    }


    public void setMethod(Tuple tuple) {
        this.setter.accept(tuple);
    }

    public Object applyGetMethod() {
        return this.getter.get();
    }
}
