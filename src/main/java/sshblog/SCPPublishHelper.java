package sshblog;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sshblog.dal.ArticleDAO;
import sshblog.dal.model.Article;

import java.io.*;
import java.util.List;

/**
 * modification of ScpCommandHelper from Apache MINA SSHD project
 *
 */
public class SCPPublishHelper {

    protected static final Logger log = LoggerFactory.getLogger(SCPPublishHelper.class);

    public static final int OK = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;

    protected final InputStream in;
    protected final OutputStream out;

    private ArticleDAO articleDAO;

    private PageGenerator pageGenerator = new PageGenerator();

    public SCPPublishHelper(ArticleDAO articleDAO, InputStream in, OutputStream out) {
        this.articleDAO = articleDAO;
        this.in = in;
        this.out = out;
    }

    public void receive(String path, boolean recursive, boolean shouldBeDir, boolean preserve) throws IOException {

        ack();
        long[] time = null;
        for (;;)
        {
            String line;
            boolean isDir = false;
            int c = readAck(true);
            switch (c)
            {
                case -1:
                    return;
                case 'D':
                    isDir = true;
                case 'C':
                    line = ((char) c) + readLine();
                    log.debug("Received header: " + line);
                    break;
                case 'T':
                    line = ((char) c) + readLine();
                    log.debug("Received header: " + line);
                    time = parseTime(line);
                    ack();
                    continue;
                case 'E':
                    line = ((char) c) + readLine();
                    log.debug("Received header: " + line);
                    return;
                default:
                    //a real ack that has been acted upon already
                    continue;
            }

            if (recursive && isDir)
            {
                throw new UnsupportedOperationException("No guts here");
            }
            else
            {
                receiveFile(line, path, time, preserve);
                time = null;
            }
        }
    }


    public void receiveFile(String header, String path, long[] time, boolean preserve) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Receiving file {}", path);
        }
        if (!header.startsWith("C")) {
            throw new IOException("Expected a C message but got '" + header + "'");
        }

        String perms = header.substring(1, 5);
        long length = Long.parseLong(header.substring(6, header.indexOf(' ', 6)));
        String name = header.substring(header.indexOf(' ', 6) + 1);


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ack();

            byte[] buffer = new byte[8192];
            while (length > 0) {
                int len = (int) Math.min(length, buffer.length);
                len = in.read(buffer, 0, len);
                if (len <= 0) {
                    throw new IOException("End of stream reached");
                }
                os.write(buffer, 0, len);
                length -= len;
            }
        } finally {
            os.close();
        }


        articleDAO.save(new Article(path, pageGenerator.fromMarkdown(path, new String(os.toByteArray(), "UTF-8"))));

        ack();
        readAck(false);
    }

    public String readLine() throws IOException {
        return readLine(false);
    }

    public String readLine(boolean canEof) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (;;) {
            int c = in.read();
            if (c == '\n') {
                return baos.toString();
            } else if (c == -1) {
                if (!canEof) {
                    throw new EOFException();
                }
                return null;
            } else {
                baos.write(c);
            }
        }
    }

    public void send(List<String> paths, boolean recursive, boolean preserve) throws IOException {
        throw new UnsupportedOperationException("No guts here");
    }

    private long[] parseTime(String line) {
        String[] numbers = line.substring(1).split(" ");
        return new long[] { Long.parseLong(numbers[0]), Long.parseLong(numbers[2]) };
    }


    public void ack() throws IOException {
        out.write(0);
        out.flush();
    }

    public int readAck(boolean canEof) throws IOException {
        int c = in.read();
        switch (c) {
            case -1:
                if (!canEof) {
                    throw new EOFException();
                }
                break;
            case OK:
                break;
            case WARNING:
                log.warn("Received warning: " + readLine());
                break;
            case ERROR:
                throw new IOException("Received nack: " + readLine());
            default:
                break;
        }
        return c;
    }


}

