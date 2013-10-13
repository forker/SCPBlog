package sshblog;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;
import static spark.Spark.*;
import spark.*;
import sshblog.dal.ArticleDAO;
import sshblog.dal.model.Article;

/**
 * Created with IntelliJ IDEA.
 * User: forker
 * Date: 10/13/13
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static ArticleDAO articleDAO = new ArticleDAO();

    public static void main(String[] args) throws Exception {

        get(new Route("/:id") {
            @Override
            public Object handle(Request request, Response response) {
                return articleDAO.get(request.params("id")).getBody();
            }
        });

        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(2222);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("host.key"));
        sshd.setCommandFactory(new SCPPublishCommand.Factory(articleDAO));
        sshd.setPasswordAuthenticator(new StupidPasswordAuthenticator());
        sshd.start();
        Thread.sleep(Long.MAX_VALUE);
    }

}
