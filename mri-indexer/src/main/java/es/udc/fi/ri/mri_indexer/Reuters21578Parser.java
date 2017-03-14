package es.udc.fi.ri.mri_indexer;

import java.util.LinkedList;
import java.util.List;

public class Reuters21578Parser {

	/*
	 * Project testlucene 3.6.0, the Reuters21578Parser class parses the
	 * collection.
	 */
	
	
	
	// Comentario Branch!!!!

	private static final String END_BOILERPLATE_1 = "Reuter&#3;";
	private static final String END_BOILERPLATE_2 = "REUTER&#3;";

	// private static final String[] TOPICS = { "acq", "alum", "austdlr",
	// "barley", "bean", "belly", "bfr", "bop", "cake", "can", "carcass",
	// "castor", "castorseed", "cattle", "chem", "citruspulp", "cocoa",
	// "coconut", "coffee", "copper", "copra", "corn", "cornglutenfeed",
	// "cotton", "cottonseed", "cpi", "cpu", "crude", "cruzado", "debt",
	// "dfl", "dkr", "dlr", "dmk", "earn", "f", "feed", "fishmeal",
	// "fuel", "fx", "gas", "gnp", "gold", "grain", "groundnut", "heat",
	// "hk", "hog", "housing", "income", "instal", "interest",
	// "inventories", "ipi", "iron", "jet", "jobs", "l", "lead", "lei",
	// "lin", "linseed", "lit", "livestock", "lumber", "meal", "metal",
	// "money", "naphtha", "nat", "nickel", "nkr", "nzdlr", "oat", "oil",
	// "oilseed", "orange", "palladium", "palm", "palmkernel", "peseta",
	// "pet", "platinum", "plywood", "pork", "potato", "propane", "rand",
	// "rape", "rapeseed", "red", "reserves", "retail", "rice", "ringgit",
	// "rubber", "rupiah", "rye", "saudriyal", "sfr", "ship", "silver",
	// "skr", "sorghum", "soy", "soybean", "steel", "stg", "strategic",
	// "sugar", "sun", "sunseed", "supply", "tapioca", "tea", "tin",
	// "trade", "veg", "wheat", "wool", "wpi", "yen", "zinc" };

	public static List<List<String>> parseString(StringBuffer fileContent) {
		/* First the contents are converted to a string */
		String text = fileContent.toString();

		/*
		 * The method split of the String class splits the strings using the
		 * delimiter which was passed as argument Therefor lines is an array of
		 * strings, one string for each line
		 */
		String[] lines = text.split("\n");

		/*
		 * For each Reuters article the parser returns a list of strings where
		 * each element of the list is a field (TITLE, BODY, TOPICS, DATELINE).
		 * Each *.sgm file that is passed in fileContent can contain many
		 * Reuters articles, so finally the parser returns a list of list of
		 * strings, i.e, a list of reuters articles, that is what the object
		 * documents contains
		 */

		List<List<String>> documents = new LinkedList<List<String>>();

		/* The tag REUTERS identifies the beginning and end of each article */

		for (int i = 0; i < lines.length; ++i) {
			if (!lines[i].startsWith("<REUTERS"))
				continue;
			StringBuilder sb = new StringBuilder();
			while (!lines[i].startsWith("</REUTERS")) {
				sb.append(lines[i++]);
				sb.append("\n");
			}
			/*
			 * Here the sb object of the StringBuilder class contains the
			 * Reuters article which is converted to text and passed to the
			 * handle document method that will return the document in the form
			 * of a list of fields
			 */
			documents.add(handleDocument(sb.toString()));
		}
		return documents;
	}

	public static List<String> handleDocument(String text) {

		/*
		 * This method returns the Reuters article that is passed as text as a
		 * list of fields
		 */

		/* The fields TOPICS, TITLE, DATELINE and BODY are extracted */
		/* Each topic inside TOPICS is identified with a tag D */
		/* If the BODY ends with boiler plate text, this text is removed */

		String topics = extract("TOPICS", text, true);
		String title = extract("TITLE", text, true);
		String dateline = extract("DATELINE", text, true);
		String body = extract("BODY", text, true);
		String date = extract("DATE", text, true);
		if (body.endsWith(END_BOILERPLATE_1)
				|| body.endsWith(END_BOILERPLATE_2))
			body = body
					.substring(0, body.length() - END_BOILERPLATE_1.length());
		List<String> document = new LinkedList<String>();
		document.add(title);
		document.add(body);
		document.add(topics.replaceAll("\\<D\\>", " ").replaceAll("\\<\\/D\\>",
				""));
		document.add(dateline);
		document.add(date);
		return document;
	}

	private static String extract(String elt, String text, boolean allowEmpty) {

		/*
		 * This method find the tags for the field elt in the String text and
		 * extracts and returns the content
		 */
		/*
		 * If the tag does not exists and the allowEmpty argument is true, the
		 * method returns the null string, if allowEmpty is false it returns a
		 * IllegalArgumentException
		 */

		String startElt = "<" + elt + ">";
		String endElt = "</" + elt + ">";
		int startEltIndex = text.indexOf(startElt);
		if (startEltIndex < 0) {
			if (allowEmpty)
				return "";
			throw new IllegalArgumentException("no start, elt=" + elt
					+ " text=" + text);
		}
		int start = startEltIndex + startElt.length();
		int end = text.indexOf(endElt, start);
		if (end < 0)
			throw new IllegalArgumentException("no end, elt=" + elt + " text="
					+ text);
		return text.substring(start, end);
	}

}
