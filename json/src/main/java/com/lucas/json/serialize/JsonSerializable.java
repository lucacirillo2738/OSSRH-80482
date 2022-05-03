package com.lucas.json.serialize;

/** Provide a default method that will be override in all subclass of this interface that are annotate with SisalSerializer
 * @see Serializer
 * */
public interface JsonSerializable {

    default String toJson() {
        throw new RuntimeException("Implement method or user plugin to auto generate default implementation!");

    }
}
