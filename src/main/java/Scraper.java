import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scraper implements Runnable {
	private int Page;
	private PreparedStatement stmt;

	public Scraper(int Page, Connection conn) throws SQLException {
		this.Page = Page;
	}

	@Override
	public void run() {
		try {
			stmt = DBConnector.createConnection();
			// Scrape the page and store the data in the database
			// Scrape the page and store the data in the database
			String url = "https://kararlarbilgibankasi.anayasa.gov.tr/?page=" + Page;

			Document document = Jsoup.parse(new URL(url).openStream(), "CP1252", url);

			Elements rulings = document.getElementsByClass("birkarar");
			int startNumber = ((Page -1 ) * 10) +1;

			for (Element ruling : rulings) {
				String rulingText = ruling.text();

				Map rulingmetaData = getMetadata(rulingText);

				Element link = ruling.select("a[href]").get(0);
				String judgementLink =  link.attr("abs:href");

				String rulingContent = getRulingDetails(judgementLink);
				stmt.setInt(1,startNumber);
				stmt.setString(2, rulingmetaData.get("title").toString());
				stmt.setString(3, rulingmetaData.get("kararNumber").toString());

				String dateString = rulingmetaData.get("kararTarihi").toString();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date date = dateFormat.parse(dateString);
				stmt.setDate(4, new java.sql.Date(date.getTime()));
				stmt.setString(5, judgementLink);
				stmt.setString(6, rulingContent);

				stmt.executeUpdate();
				startNumber++;
			}


		} catch (IOException | SQLException | ParseException e) {
			e.printStackTrace();
		}
	}

	private static Map getMetadata(String text) throws UnsupportedEncodingException {
		Map metaData = new HashMap();

		//convert encoding to UTF-8 to work with regex
		text = new String(text.getBytes("CP1252"), "UTF-8");

		// Regular expression to match the title
		Pattern titlePattern = Pattern.compile("^(.*?)\\s*Başvurusuna İlişkin Karar");
		Matcher titleMatcher = titlePattern.matcher(text);
		if (titleMatcher.find()) {
			String title = titleMatcher.group(1);
			metaData.put("title",title);
		}
		// Regular expression to match the number after "Karar Number"
		Pattern kararNumberPattern = Pattern.compile("Başvurusuna İlişkin Karar\\s*(\\d+/\\d+)");
		Matcher kararNumberMatcher = kararNumberPattern.matcher(text);
		if (kararNumberMatcher.find()) {
			String kararNumber = kararNumberMatcher.group(1);
			metaData.put("kararNumber",kararNumber);
		}else{
			Pattern kararNumberEnglishPattern = Pattern.compile("Başvurusuna İlişkin Karar English\\s*(\\d+/\\d+)");
			Matcher kararNumberEnglishMatcher = kararNumberEnglishPattern.matcher(text);
			if(kararNumberEnglishMatcher.find()) {
				String kararNumber = kararNumberEnglishMatcher.group(1);
				metaData.put("kararNumber", kararNumber);
			}
		}
		// Regular expression to match the number after "Karar Tarihi"
		Pattern kararTarihiPattern = Pattern.compile("Karar Tarihi\\s*:\\s*(\\d{2}/\\d{2}/\\d{4})");
		Matcher kararTarihiMatcher = kararTarihiPattern.matcher(text);
		if (kararTarihiMatcher.find()) {
			String kararTarihi = kararTarihiMatcher.group(1);
			metaData.put("kararTarihi",kararTarihi);
		}

		return metaData;
	}
	private static String getRulingDetails(String judgementLink) throws IOException {
		Document rulingDocument = Jsoup.connect(judgementLink).get();
		Elements rulingDetails = rulingDocument.getElementsByClass("WordSection1");

		return rulingDetails.html();
	}

}
