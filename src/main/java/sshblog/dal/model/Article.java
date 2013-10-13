package sshblog.dal.model;

/**
 * Created with IntelliJ IDEA.
 * User: forker
 * Date: 10/13/13
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Article implements WithId {


    private String id;
    private String body;

    public Article(String id, String body) {
        this.id = id;
        this.body = body;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
