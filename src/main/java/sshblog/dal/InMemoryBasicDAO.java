package sshblog.dal;

import sshblog.dal.model.Article;
import sshblog.dal.model.WithId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: forker
 * Date: 10/13/13
 * Time: 9:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class InMemoryBasicDAO<T extends WithId> implements IBasicDAO<T> {

    private Map<String, WithId> storage = new ConcurrentHashMap<String, WithId>();

    @Override
    public void save(T obj) {
        storage.put(obj.getId(), obj);
    }

    @Override
    public T get(String id) {
        return (T) storage.get(id);
    }

    @Override
    public T remove(String id) {
        return (T) storage.remove(id);
    }
}
