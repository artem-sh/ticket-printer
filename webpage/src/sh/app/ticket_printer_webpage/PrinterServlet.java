package sh.app.ticket_printer_webpage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

public class PrinterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String sampleHtml = loadTextFile("sample.html");
		response.getWriter().print(sampleHtml.substring(0, sampleHtml.indexOf("CHANGE_ME")));
		response.getWriter().print("\"" + loadTicketString() + "\"");
		response.getWriter().print(sampleHtml.substring(sampleHtml.indexOf("CHANGE_ME") + "CHANGE_ME".length()));
	}

	private String loadTicketString() {
		String origXml = loadTextFile("ticket1.xml");
		return StringEscapeUtils.escapeJavaScript(origXml);
	}

	private String loadTextFile(String fileName) {
		try {
			InputStream is = PrinterServlet.class.getResourceAsStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}