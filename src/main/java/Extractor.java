import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extractor {
	public static int getNumberOfPages() throws IOException {
		String url = "https://kararlarbilgibankasi.anayasa.gov.tr/";

		//set encoding to allow proper parsing of non-latin characters
		Document doc = Jsoup.parse(new URL(url).openStream(), "CP1252", url);

		Elements pages = doc.getElementsByClass("pagination");
		String lastPage = pages.last().children().get(pages.last().childrenSize()-2).text();

		return Integer.valueOf(lastPage);
	}
	public static void main(String[] args) throws IOException, SQLException, ParseException {
		System.out.println("Judgement Scraping started");
		DBConnector.createDB();
		int numberOfPages = getNumberOfPages();
		int j =1;
		PreparedStatement Pstmt = DBConnector.createConnection();
		for (int i = 1; i < numberOfPages+1; i++) {
			String url = "https://kararlarbilgibankasi.anayasa.gov.tr/?page="+i;
			Document document = Jsoup.parse(new URL(url).openStream(), "CP1252", url);
			Elements rulings = document.getElementsByClass("birkarar");

			if(i % 100 == 0){
				Pstmt.executeBatch();
				Pstmt = DBConnector.createConnection();
			}

			for (Element ruling:rulings) {
				String rulingText = ruling.text();

				Map rulingmetaData = getMetadata(rulingText);

				Element link = ruling.select("a[href]").get(0);
				String judgementLink = print( "%s ",link.attr("abs:href"), trim(link.text(), 35));

				String rulingContent = getRulingDetails(judgementLink);

				Pstmt.setInt(1,j);
				Pstmt.setString(2,rulingmetaData.get("title").toString());
				Pstmt.setString(3,rulingmetaData.get("kararNumber").toString());

				String dateString = rulingmetaData.get("kararTarihi").toString();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date date = dateFormat.parse(dateString);

				Pstmt.setDate(4, new java.sql.Date(date.getTime()));
				Pstmt.setString(5,judgementLink);
				Pstmt.setString(6,rulingContent);
				Pstmt.addBatch();
				j++;
			}

		}
	}




	private static String print(String msg, Object... args) {
		return String.format(msg, args);
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width-1) + ".";
		else
			return s;
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
		return rulingDetails.text();
	}

}
