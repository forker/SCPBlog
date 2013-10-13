package sshblog.dal;

import sshblog.dal.model.Article;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: forker
 * Date: 10/13/13
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleDAO {

    private Map<String, Article> storage = new ConcurrentHashMap<String, Article>();

    public void save(Article article) {
        storage.put(article.getId(), article);
    }

    public Article get(String id) {
        return storage.get(id);
    }

    public Article remove(String id) {
        return storage.remove(id);
    }

}
