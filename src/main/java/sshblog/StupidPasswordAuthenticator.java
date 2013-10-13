package sshblog;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

/**
 * Created with IntelliJ IDEA.
 * User: forker
 * Date: 10/13/13
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class StupidPasswordAuthenticator implements PasswordAuthenticator {

    @Override
    public boolean authenticate(String s, String s2, ServerSession serverSession) {
        return true;
    }
}
