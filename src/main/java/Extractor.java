import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
	public static void main(String[] args) throws IOException {
		int numberOfPages = getNumberOfPages();

		String url = "https://kararlarbilgibankasi.anayasa.gov.tr/?page=1";
		Document document = Jsoup.parse(new URL(url).openStream(), "CP1252", url);
		Elements rulings = document.getElementsByClass("birkarar");
		for (Element ruling : rulings) {
			System.out.println(ruling.text());
			String rulingText = ruling.text();

			List rulingmetaData = getMetadata(rulingText);


			Element link = ruling.select("a[href]").get(0);
			String judgementLink = print( "%s ",link.attr("abs:href"), trim(link.text(), 35));


			System.out.println(judgementLink);
			System.out.println(getRulingDetails(judgementLink));
		}
		System.out.println(url);


//		for (int i = 1; i < numberOfPages + 1; i++) {
//			String url = "https://kararlarbilgibankasi.anayasa.gov.tr/?page="+i;
//			Document document = Jsoup.parse(new URL(url).openStream(), "CP1252", url);
//			Elements rulings = document.getElementsByClass("birkarar");
//			for (Element ruling:rulings) {
//				System.out.println(ruling.text());
//			}
//			System.out.println(url);
//
// 		}

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
	private static List getMetadata(String text) throws UnsupportedEncodingException {
		List <String> metaData = new ArrayList(3);

		//convert encoding to UTF-8 to work with regex
		text = new String(text.getBytes("CP1252"), "UTF-8");

		// Regular expression to match the title
		Pattern titlePattern = Pattern.compile("^(.*?)\\s*Başvurusuna İlişkin Karar");
		Matcher titleMatcher = titlePattern.matcher(text);
		if (titleMatcher.find()) {
			String title = new String(titleMatcher.group(1).getBytes("UTF-8"), "CP1252");
			System.out.println("Title: " + title);
			metaData.add(title);
		}
		// Regular expression to match the number after "Karar Number"
		Pattern kararNumberPattern = Pattern.compile("Başvurusuna İlişkin Karar\\s*(\\d+/\\d+)");
		Matcher kararNumberMatcher = kararNumberPattern.matcher(text);
		if (kararNumberMatcher.find()) {
			String kararNumber = kararNumberMatcher.group(1);
			System.out.println("Başvurusuna İlişkin Karar Number: " + kararNumber);
			metaData.add(kararNumber);
		}
		// Regular expression to match the number after "Karar Tarihi"
		Pattern kararTarihiPattern = Pattern.compile("Karar Tarihi\\s*:\\s*(\\d{2}/\\d{2}/\\d{4})");
		Matcher kararTarihiMatcher = kararTarihiPattern.matcher(text);
		if (kararTarihiMatcher.find()) {
			String kararTarihi = kararTarihiMatcher.group(1);
			System.out.println("Karar Tarihi: " + kararTarihi);
			metaData.add(kararTarihi);
		}

		return metaData;
	}
	private static String getRulingDetails(String judgementLink) throws IOException {
		Document rulingDocument = Jsoup.parse(new URL(judgementLink).openStream(), "CP1252", judgementLink);

		Elements rulingDetails = rulingDocument.getElementsByClass("WordSection1");
		return rulingDetails.text();
	}

}
