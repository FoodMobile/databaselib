package com.foodmobile.databaselib.adapters;

import com.foodmobile.databaselib.models.Entity;

import java.util.List;

public interface CRUDCompliant {

    public <T> List<T> read(QueryDetails details, Class<T> tClass) throws Exception;

    public <T extends Entity> int create(QueryDetails details,T obj) throws Exception;

    public <T extends Entity> int update(QueryDetails details, T obj) throws Exception;

    public int delete(QueryDetails details) throws Exception;
}
