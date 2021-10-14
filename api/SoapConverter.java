package ru.kazachkov.florist.api;


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import ru.kazachkov.florist.interfaces.XmlFormated;

public class SoapConverter extends Converter.Factory {

    private Converter.Factory mSerializer;
    private Converter.Factory mDeserializer;

    public SoapConverter(Converter.Factory serializer, Converter.Factory deserializer) {
        mSerializer = serializer;
        mDeserializer = deserializer;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return mSerializer.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        try {
            Class<?> c = getClass(type);
            if (c != null && XmlFormated.class.isAssignableFrom(c)) {
                return mSerializer.responseBodyConverter(type, annotations, retrofit);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return mDeserializer.responseBodyConverter(type, annotations, retrofit);
    }


    private static final String TYPE_NAME_PREFIX = "class ";

    public static String getClassName(Type type) {
        if (type == null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_NAME_PREFIX)) {
            className = className.substring(TYPE_NAME_PREFIX.length());
        }
        return className;
    }

    public static Class<?> getClass(Type type)
            throws ClassNotFoundException {
        String className = getClassName(type);
        if (className == null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }
}
