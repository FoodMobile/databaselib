package adapters;

public interface DBAdapter extends CRUDCompliant{
    public void connect(ConnectionInfo info) throws Exception;

    public <T extends QueryDetails> T queryFactory();
}
