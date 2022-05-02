package com.lucas.json.deserialize;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/** Provide static methods that will be override in all subclass of this interface that are annotate with SisalDeserializer
 * @see Deserializer
 * */
public abstract class JsonDeserializable {

    /**@param json, of type String that has to be deserialized
     * @return the valorized instance of this class*/
    public static <T extends JsonDeserializable> T toObject(String json) throws Exception{
        throw new Exception("Implement method or user plugin to auto generate default implementation!");
    }

    /**Allows to reuse an existent instance of JsonFactory avoiding to create new one
     * @param json, of type String that has to be deserialized
     * @param jsonFactory, the object uset to create a JsonParser class from json String parameter
     * @return the valorized instance of this class*/
    public static <T extends JsonDeserializable> T toObject(JsonFactory jsonFactory, String json) throws Exception{
        throw new Exception("Implement method or user plugin to auto generate default implementation!");
    }

    /**Allows to reuse an existent instance of JsonParser constructed from json String
     * @param jsonParser, the JsonParser object created from json Sring unsing JsonFactory
     * @return the valorized instance of this class*/
    public static <T extends JsonDeserializable> T toObject(JsonParser jsonParser) throws Exception{
        throw new Exception("Implement method or user plugin to auto generate default implementation!");
    }
}
