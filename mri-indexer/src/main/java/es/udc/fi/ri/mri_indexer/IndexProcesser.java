package es.udc.fi.ri.mri_indexer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.packed.PackedLongValues.Iterator;

public class IndexProcesser {

	private String indexFile;
	public IndexProcesser (String indexfile){
		this.indexFile=indexfile;
	}
	
	private static List<Entry<String,Double>> orderForMax(Map map){
		Set<Entry<String,Double>> set = map.entrySet();
		List<Entry<String,Double>> list = new ArrayList<Entry<String, Double>>(set);
		
		Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
        {
            public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
		return list;
		
	}
	private static List<Entry<String,Double>> orderForMin(Map map){
		Set<Entry<String,Double>> set = map.entrySet();
		List<Entry<String,Double>> list = new ArrayList<Entry<String, Double>>(set);
		
		Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
        {
            public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );
		return list;
		
	}
	
	private static List<TfIdfObject> orderTdIdfMax(List<TfIdfObject> list){
	
		Collections.sort( list, new Comparator<TfIdfObject>()
        {
            public int compare( TfIdfObject o1, TfIdfObject o2 )
            {
                return (o1.compareTo(o2));
            }
        } );
		return list;
	}
	
	private Map<String, Double> calculateIdfTerms(String field){
		DirectoryReader indexReader = null;
		Directory dir = null;
		try{
			dir = FSDirectory.open(Paths.get(indexFile));
			indexReader = DirectoryReader.open(dir);
		}catch(CorruptIndexException e1){
			System.out.println("Gracefull message: Exception"+e1);
			e1.printStackTrace();
		}catch (IOException e1){
			System.out.println("Gracefull message: Exception"+e1);
			e1.printStackTrace();
		}
		
		int N = indexReader.numDocs();
		Map<String, Double> idfTerms = new HashMap<String, Double>();
		try{
			Fields fields = MultiFields.getFields(indexReader);
			Terms terms = fields.terms(field);
			final TermsEnum termsEnum = terms.iterator();
			while(termsEnum.next()!=null){
				int df_t = termsEnum.docFreq();
				double idf = Math.log10(N/df_t);
				idfTerms.put(termsEnum.term().utf8ToString(), idf);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return idfTerms;
	}
	
	public void bestIdfTerms (String field,int n ){
			Map<String, Double> idfTerms = calculateIdfTerms(field);
			List<Entry<String,Double>> orderedTerms = orderForMax(idfTerms);
			java.util.Iterator<Entry<String, Double>> it = orderedTerms.iterator();
			int i=1;
			Entry<String,Double> entry;
			while( i<=n ){
				entry=it.next();
				System.out.println("TERMINO: "+entry.getKey()+" VALOR: "+entry.getValue());
				i++;
			}
	}
	
	public void poorIdfTerms(String field,int n){
		Map<String, Double> idfTerms = calculateIdfTerms(field);
		List<Entry<String,Double>> orderedTerms = orderForMin(idfTerms);
		java.util.Iterator<Entry<String, Double>> it = orderedTerms.iterator();
		int i=1;
		Entry<String,Double> entry;
		while( i<=n ){
			entry=it.next();
			System.out.println("TERMINO: "+entry.getKey()+" VALOR: "+entry.getValue());
			i++;
		}
	}
	
	public void bestTfIdfTerms(String field, int n){
		DirectoryReader indexReader = null;
		Directory dir = null;
		try{
			dir = FSDirectory.open(Paths.get(indexFile));
			indexReader = DirectoryReader.open(dir);
		}catch(CorruptIndexException e1){
			System.out.println("Gracefull message: Exception"+e1);
			e1.printStackTrace();
		}catch (IOException e1){
			System.out.println("Gracefull message: Exception"+e1);
			e1.printStackTrace();
		}
		
		try{
			List<TfIdfObject> tfIdfList = new ArrayList<TfIdfObject>();
			int N = indexReader.numDocs();
			Fields fields = MultiFields.getFields(indexReader);
			Terms terms = fields.terms(field);
			BytesRef term = null;
			TermsEnum termsEnum = terms.iterator();
			while((term=termsEnum.next()) != null){
				
			}
			
		
			
			List<TfIdfObject> orderedlist = orderTdIdfMax(tfIdfList);
			java.util.Iterator<TfIdfObject> it = orderedlist.iterator();
			int i=1;
			TfIdfObject par;
			while( i<=N ){
				par=it.next();
				System.out.println(par.toString());
				i++;
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
		

		
	}
	
}
