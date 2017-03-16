package es.udc.fi.ri.mri_indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexConstructor {
	
	private final String indexout;
	private final String indexin;
	
	public IndexConstructor(String indexout, String indexin){
		this.indexin = indexin;
		this.indexout = indexout;
	}
	
	private IndexWriter createWriter(){
		try{
			Directory dir;
			dir = FSDirectory.open(Paths.get(indexin));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.APPEND);
			IndexWriter writer = new IndexWriter(dir, iwc);
			return writer;
		 }catch (IOException e){
			 
		 }
		return null;
	}
	private IndexReader createReader(){
		try{
			Directory dir = null;
			DirectoryReader indexReader = null;
		
			dir = FSDirectory.open(Paths.get(indexin));
			indexReader = DirectoryReader.open(dir);
			return indexReader;
		}catch(IOException e){
			
		}
		return null;
	}
	
	public void deldocsterm(String field, String termino){
		IndexWriter writer = createWriter();
		try{
			Term term = new Term(field, termino);
			System.out.println("delete term "+termino+" "+field);
			writer.deleteDocuments(term);
			writer.forceMergeDeletes();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			writer.commit();
			writer.close();
		}catch (CorruptIndexException e) {
			System.out.println("Graceful message: exception "+e);
			e.printStackTrace();
		}catch (IOException e) {
			System.out.println("Graceful message: exception "+e);
			e.printStackTrace();
		}
	}
	
	public void deldocsquery(String squery){
		IndexReader reader = createReader();
		IndexWriter writer = createWriter();
		try {
			Fields fields = MultiFields.getFields(reader);
			String [] arrayFields = new String[fields.size()];
			BooleanClause.Occur [] bool = new BooleanClause.Occur [fields.size()];
			Iterator<String> it = fields.iterator();
			String s; 
			int i = 0;
			while((s=it.next())!=null){
				arrayFields[i] = s;
				bool[i] =  BooleanClause.Occur.MUST;
				//dfghjl
				i++;
			}
			
			try {
				Query query = MultiFieldQueryParser.parse(squery, arrayFields,bool,new StandardAnalyzer());
				writer.deleteDocuments(query);
				writer.forceMergeDeletes();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			try{
				writer.commit();
				writer.close();
			}catch (CorruptIndexException e) {
				System.out.println("Graceful message: exception "+e);
				e.printStackTrace();
			}catch (IOException e) {
				System.out.println("Graceful message: exception "+e);
				e.printStackTrace();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
