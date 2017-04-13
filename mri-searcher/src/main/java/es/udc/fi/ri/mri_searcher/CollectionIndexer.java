package es.udc.fi.ri.mri_searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CollectionIndexer {
	private static final FieldType TYPE_STORED = new FieldType();
	static final IndexOptions options = IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
	static{
		TYPE_STORED.setIndexOptions(options);
		TYPE_STORED.setTokenized(true);
		TYPE_STORED.setStored(true);
		TYPE_STORED.setStoreTermVectors(true);
		TYPE_STORED.setStoreTermVectorPositions(true);
		TYPE_STORED.freeze();
	}
	private static final FieldType TYPE_STORED2 = new FieldType();
	static{
		TYPE_STORED2.setIndexOptions(options);
		TYPE_STORED2.setTokenized(false);
		TYPE_STORED2.setStored(true);
		TYPE_STORED2.setStoreTermVectors(true);
		TYPE_STORED2.setStoreTermVectorPositions(true);
		TYPE_STORED2.freeze();
	}
		
			//addFields
			//INDEXAMOS TODOS LOS CAMPOS PARA CADA DOCUMENTO OBTENIDO
			private static void addFields(IndexWriter writer,List<List<String>> documents, File file){
				int i = 1; //PARA POSICION DEL DOC DENTRO DEL ARCHIVO SGM
				for(List<String> document : documents){
					try {
						Document doc = new Document();
						doc.add(new Field(".I",document.get(0),TYPE_STORED));
						doc.add(new Field(".T",document.get(1),TYPE_STORED));
						doc.add(new Field(".A",document.get(2),TYPE_STORED));
			  			doc.add(new Field(".B",document.get(3),TYPE_STORED));
					  	doc.add(new Field(".W",document.get(4),TYPE_STORED));
					  	doc.add(new Field("seqDocNumber", Integer.toString(i),TYPE_STORED));
					  	i++;
					  	writer.addDocument(doc);
					  	
					} catch (UnknownHostException e) {
						System.out.println("Can't resolve hostname");
					} catch (IOException  e2){
						System.out.println("Can't write the document");
					}
				}
			}
		//IndexDocs
			// Parsea los documentos que se quieren indexar, y los envia addFields para que los añada al indice
			private static void indexDocs(IndexWriter writer, File file){

		    	if (file.canRead()){ //SI EL ARCHIVO NO SE PUEDE LEER, NO PODEMOS INDEXARLO
		    		//SI SE TRATA DE UN DIRECTORIO HAY QUE RECORRERLO
		    		if(file.isDirectory()){
		    			File documents[] = file.listFiles();
		    			if(documents != null){
		    				for(int i= 0; i<documents.length; i++){
		    					//LLAMAMOS DE NUEVO A INDEXDOCS CON EL CONTENIDO DEL DIRECTORIO
		    					indexDocs(writer, documents[i]);
		    				}
		    			}
		    		}else if(Utilities.ValidateName(file.getName())==1){
		    			
		    			//EN CASO DE QUE SEA UN DOCUMENTO INDIVIDUAL, LO INDEXAMOS
		    			//PARA INDEXAR PRIMERO LO ABRIMOS Y CODIFICAMOS
		    			//InputStreamReader PASA DE BYTES A CARACTERES SEGUN CODIFICACION	
		    			InputStreamReader input = Utilities.openDocument(file);
		    			
		    			
		    			if (input!=null){
		    				//BufferedReader LEE UN TEXTO EN FORMATO InputStreamReader
		    				//PROVEE FORMA EFICIENTE DE LECTURA POR LINEAS
		    				BufferedReader buffer = new BufferedReader(input);
		    				//StringBuffer ALMACENA EL CONTENIDO
		    				//PARECIDO A STRING PERO SOPORTA THREADS
		    				//ES EL TIPO DE ENTRADA DE PARSER REUTERS
		    				StringBuffer fileContent = new StringBuffer();
		    				String text = null;
		    				//PREPARAMOS BUFFER PARA DARSELO AL PARSER
		    				try {
								while((text=buffer.readLine()) != null){
									fileContent.append(text).append("\n");
								}
								
							} catch (IOException e) {
								System.out.println("Failure while reading doc");
							}
		    				
		    				//PARSEAMOS EL CONTENIDO DEL DOCUMENTO
		    				//OBTENEMOS UNA LISTA DE LISTAS. UNA LISTA PARA CADA FIELD
		    				List<List<String>> documents;
							try {
								documents = CranfieldParser.parseString(fileContent);
								//AÑADIMOS ESTOS FIELDS AL INDICE
			    				addFields(writer, documents,file);	
							} catch (Exception e) {
								System.out.println("Cranfield Parser error while parsing");
								e.printStackTrace();
							}
		    				
		    				System.out.println("File "+file.getPath()+" procesed ..");
		    			}
		    		}
		    	}
			}
		
		public static void index(String indexPath,List<String> docsPaths,String openmode){
			try{
				Directory dir;
				dir = FSDirectory.open(Paths.get(indexPath));
				Analyzer analyzer = new StandardAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			   
				if (openmode.equals("create")){
					iwc.setOpenMode(OpenMode.CREATE);
				}else if(openmode.equals("append")){
					iwc.setOpenMode(OpenMode.APPEND);
				}else if(openmode.equals("create_or_append")){
					iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				}
		   	 
				IndexWriter writer = new IndexWriter(dir, iwc);

				
				for(String docPath: docsPaths){
					File docsDir = new File(docPath);
					if(!docsDir.exists()||!docsDir.canRead()){
						System.out.println("Document directory "+docsDir.getAbsolutePath()+" does not exist or is not readable");
						System.exit(1);
					}
					indexDocs(writer, docsDir);
				}
				writer.close();
				
			 }catch (IOException e){
				 
			 }
		}
}
