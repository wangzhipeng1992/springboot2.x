package com.hangxin.basic;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class ResultCode {

    @Sub(name = "SUCCESS")
    public static String RESULT_SUCCESS = "0000";

    @Sub(name = "FAILED : ")
    public static String RESULT_FAILED = "0101";

    @Sub(name = "internal server error")
    public static String RESULT__ERROR_2 = "0102";

    @Sub(name = "user is not logined")
    public static String RESULT__UNLOGIN = "0103";

    @Sub(name = "Invalid Token ")
    public static String RESULT__ERROR_5 = "0005";

    @Retention(RUNTIME)
    @Target({ FIELD })
    public @interface Sub {
        public String name();
    }

    public static String getValueByKey(String key) {
        ResultCode mySub = new ResultCode();

        if (key == null || key.equals("")) {
            key = ResultCode.RESULT_SUCCESS;
        }

        String result = "Success";

        Field[] fields = mySub.getClass().getDeclaredFields();
        for (Field field : fields) {

            Annotation ano = field.getAnnotation(Sub.class);
            try {
                if (key.equals(field.get(mySub).toString()) && ano != null) {
                    Sub sub = (Sub) ano;
                    result = (String) sub.name();
                    break;
                }
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return result;

    }

}
