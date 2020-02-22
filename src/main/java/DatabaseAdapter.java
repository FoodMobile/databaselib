
import adapters.ConnectionInfo;
import adapters.DBAdapter;
import adapters.QueryDetails;
import models.Entity;

import java.util.List;

public class DatabaseAdapter implements DBAdapter {

    private DBAdapter adapter;

    public static DatabaseAdapter shared;

    public <T extends DBAdapter> DatabaseAdapter(Class<T> tClass) throws Exception {
        this.adapter = tClass.getConstructor().newInstance();
    }

    @Override
    public <T> List<T> read(QueryDetails details, Class<T> tClass) throws Exception {
        return this.adapter.read(details,tClass);
    }

    @Override
    public <T extends Entity> int create(QueryDetails details, T obj) throws Exception{
        return this.adapter.create(details,obj);
    }

    @Override
    public <T extends Entity> int update(QueryDetails details, T obj) throws Exception{
        return this.adapter.update(details,obj);
    }

    @Override
    public int delete(QueryDetails details) throws Exception {
        return this.adapter.delete(details);
    }

    @Override
    public void connect(ConnectionInfo info) throws Exception {
        this.adapter.connect(info);
    }

    @Override
    public <T extends QueryDetails> T queryFactory() {
        return this.adapter.queryFactory();
    }
}
