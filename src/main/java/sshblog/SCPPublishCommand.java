package sshblog;

import org.apache.sshd.common.file.FileSystemAware;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.command.ScpCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: forker
 * Date: 10/13/13
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SCPPublishCommand implements Command, Runnable {

    public static class Factory implements CommandFactory {

        private CommandFactory delegate;

        public Factory() {
        }

        public Factory(CommandFactory delegate) {
            this.delegate = delegate;
        }

        /**
         * Parses a command string and verifies that the basic syntax is
         * correct. If parsing fails the responsibility is delegated to
         * the configured {@link CommandFactory} instance; if one exist.
         *
         * @param command command to parse
         * @return configured {@link Command} instance
         * @throws IllegalArgumentException
         */
        public Command createCommand(String command) {
            if (!command.startsWith("scp")) {
                throw new IllegalArgumentException("Unknown command, does not begin with 'scp'");
            }
            return  new SCPPublishCommand(command);
        }
    }

    protected static final Logger log = LoggerFactory.getLogger(ScpCommand.class);

    protected String name;
    protected boolean optR;
    protected boolean optT;
    protected boolean optF;
    protected boolean optD;
    protected boolean optP; // TODO: handle modification times
    protected String path;
    protected InputStream in;
    protected OutputStream out;
    protected OutputStream err;
    protected ExitCallback callback;
    protected IOException error;



    public SCPPublishCommand(String command) {
        this.name = command;
        log.debug("Executing command {}", command);
        String[] args = command.split(" ");
        for (int i = 1; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                for (int j = 1; j < args[i].length(); j++) {
                    switch (args[i].charAt(j)) {
                        case 'f':
                            optF = true;
                            break;
                        case 'p':
                            optP = true;
                            break;
                        case 'r':
                            optR = true;
                            break;
                        case 't':
                            optT = true;
                            break;
                        case 'd':
                            optD = true;
                           return;
                    }
                }
            } else {
                path = command.substring(command.indexOf(args[i-1]) + args[i-1].length() + 1);
                if (path.startsWith("\"") && path.endsWith("\"") || path.startsWith("'") && path.endsWith("'")) {
                    path = path.substring(1, path.length() - 1);
                }
                break;
            }
        }
        if (!optF && !optT) {
            error = new IOException("Either -f or -t option should be set");
        }
    }

    public void setInputStream(InputStream in) {
        this.in = in;
    }

    public void setOutputStream(OutputStream out) {
        this.out = out;
    }

    public void setErrorStream(OutputStream err) {
        this.err = err;
    }

    public void setExitCallback(ExitCallback callback) {
        this.callback = callback;
    }



    public void start(Environment env) throws IOException {
        if (error != null) {
            throw error;
        }
        new Thread(this, "ScpCommand: " + name).start();
    }

    public void destroy() {
    }

    public void run() {
        int exitValue = SCPPublishHelper.OK;
        String exitMessage = null;
        SCPPublishHelper helper = new SCPPublishHelper(in, out);
        try {
            if (optT) {
                helper.receive(path, optR, optD, optP);
            } else if (optF) {
                helper.send(Collections.singletonList(path), optR, optP);
            } else {
                throw new IOException("Unsupported mode");
            }
        } catch (IOException e) {
            try {
                exitValue = SCPPublishHelper.ERROR;
                exitMessage = e.getMessage() == null ? "" : e.getMessage();
                out.write(exitValue);
                out.write(exitMessage.getBytes());
                out.write('\n');
                out.flush();
            } catch (IOException e2) {
                // Ignore
            }
            log.info("Error in scp command", e);
        } finally {
            if (callback != null) {
                callback.onExit(exitValue, exitMessage);
            }
        }
    }

}
