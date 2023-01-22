import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Extractor {
	public static int getNumberOfPages() throws IOException {
		String url = "https://kararlarbilgibankasi.anayasa.gov.tr/";

		//set encoding to allow proper parsing of non-latin characters
		Document doc = Jsoup.parse(new URL(url).openStream(), "CP1252", url);

		Elements pages = doc.getElementsByClass("pagination");
		String lastPage = pages.last().children().get(pages.last().childrenSize()-2).text();

		return Integer.valueOf(lastPage);
	}
	public static void main(String[] args) throws IOException, SQLException, ParseException, InterruptedException {
		System.out.println("Judgement Scraping started");
		int numberOfPages = getNumberOfPages();
		DBConnector.createDB();
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

		ThreadPoolExecutor executor = new ThreadPoolExecutor(10,
				50,
				1000,
				TimeUnit.MILLISECONDS,
				queue);


		for (int i = 1; i <= numberOfPages; i ++) {
			Scraper scraper = new Scraper(i, DBConnector.conn);
			executor.submit(scraper);
		}

		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

		System.out.println("Judgement Scraping ended"+ new Date().getTime());


	}

}
