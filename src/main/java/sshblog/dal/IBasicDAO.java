package sshblog.dal;

import sshblog.dal.model.WithId;

/**
 * Created with IntelliJ IDEA.
 * User: forker
 * Date: 10/13/13
 * Time: 9:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IBasicDAO<T extends WithId> {
    void save(T t);
    T get(String id);
    T remove(String id);
}
