package com.foodmobile.databaselib.adapters;

import com.foodmobile.databaselib.models.Entity;

import java.util.List;
import java.util.Optional;

interface CRUDCompliant {

    public <T> List<T> read(QueryDetails details, Class<T> tClass) throws Exception;

    public <T> Optional<T> readOne(QueryDetails details, Class<T> tClass) throws Exception;

    public <T extends Entity> int create(QueryDetails details,T obj) throws Exception;

    public <T extends Entity> int update(QueryDetails details, T obj) throws Exception;

    public <T extends Entity> int create(QueryDetails details,List<T> obj) throws Exception;

    public <T extends Entity> int update(QueryDetails details, List<T> obj) throws Exception;

    public int deleteOne(QueryDetails details) throws Exception;

    public int deleteMany(QueryDetails details) throws Exception;
}
