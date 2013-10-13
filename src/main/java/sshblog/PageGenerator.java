package sshblog;

import org.pegdown.PegDownProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: forker
 * Date: 10/13/13
 * Time: 9:22 PM
 */
public class PageGenerator {

    private PegDownProcessor pegDownProcessor = new PegDownProcessor();

    public String fromMarkdown(String title, String mdText) {
        return "<html>" +
                "<head>" +
                "<title>Page "+ title +"</title>" +
                "</head>" +
                "<body>" + pegDownProcessor.markdownToHtml(mdText) + "</body>" +
                "</html>";
    }

    public void setPegDownProcessor(PegDownProcessor pegDownProcessor) {
        this.pegDownProcessor = pegDownProcessor;
    }
}
