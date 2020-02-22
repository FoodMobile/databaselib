package com.foodmobile.databaselib.models;

import com.foodmobile.databaselib.annotations.DBIgnore;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bson.Document;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Entity {
    public Entity(){}
    public Entity(Document doc){
        Field[] fields = FieldUtils.getAllFields(this.getClass());
        for(Field f : fields){
            if(f.isAnnotationPresent(DBIgnore.class)){continue;}
            if (doc.containsKey(f.getName())){
                Object value = doc.get(f.getName());
                try {
                    FieldUtils.writeField(this,f.getName(),value,true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public Map<String,Object> keyValuePairs(Class<? extends Annotation>  ... ignoring){
        return Arrays.stream(FieldUtils.getAllFields(this.getClass())).filter(f ->{
            for(Class<? extends Annotation> ann : ignoring){
                try {
                    if (f.isAnnotationPresent(ann) || f.get(this) == null) {
                        return false;
                    }
                }catch (Exception e){
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toMap(Field::getName,f -> {
            try {
                return f.get(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }));

    }

}
