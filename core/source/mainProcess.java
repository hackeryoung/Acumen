package source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;

public class mainProcess {
	public static void main(String[] args) throws IOException {
		String class_path = mainProcess.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String parent_path = class_path.split("core")[0];
		String data_path = parent_path + "raw_data/";
		String matrix_path = parent_path+"Matrix/";
		String file_dic_path = parent_path+"File_Dictionary/";
		String filter_path = parent_path + "useless_words/Useless_words.txt";
		//System.out.println(class_path);
		//System.out.println(data_path);
		
		/**
		 * @author ZHU Renjie
		 * @created on 30/10/2014
		 */
		HashMap<String, Integer> useless_words = new HashMap<String, Integer>();
		BufferedReader reader = new BufferedReader(new FileReader(filter_path));
		String line = null;
		for (int i=0; (line = reader.readLine()) != null; i++){
			if (!useless_words.containsKey(line))
				useless_words.put(line, i);	     
		}
		
		File folder = new File(data_path);
		File[] categories_path = folder.listFiles();
		Classifier[] categories = new Classifier[categories_path.length];
		Classifier [] files = null;
		/*Jason*/
		int [] file_num_per_cat = new int[categories_path.length];//the file count in each category
		/*----*/
		
		for(int i=0; i < categories_path.length; i++) {
			//System.out.println(categories_path[i]);
			String[] parts = categories_path[i].toString().split("/");
			String category_name = parts[parts.length - 1];
			
			//System.out.println(category_name);
			
			IO io = new IO(categories_path[i].toString(), true);
			categories[i] = CountFrequency.BuildDictionary(category_name, io.documentProcessing(), useless_words);
			
			//for testing
//			int x = GetInformationGain.num_file_word_appear(parent_path+"File_Dictionary/"+category_name+"_FileDic/");
			//System.out.println(categories[i].getCategory());
			//System.out.println(categories[i].getCountTable().get("the"));
			//V2, add the function of sorting and outputting countable
//			CountFrequency.sortValue(categories[i].getCountTable(), categories[i].getCategory());
			
			
			/**
			 * Write the category and file word count into files
			 * @author Jason Y J WANG
			 * @created on 28/10/2014
			 */
			CountFrequency.writeDic(categories[i].getCountTable(), categories[i].getCategory(), categories[i].getCategory(), parent_path);
			
			File [] files_in_one_cat = (new File(data_path+"/"+category_name)).listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.toLowerCase().endsWith(".txt");
			    }
			});
			
			file_num_per_cat[i] = files_in_one_cat.length;
			files = new Classifier[file_num_per_cat[i]];
			
			
			IO file_io = null;
			for(int j = 0; j < file_num_per_cat[i]; j++){
				file_io = new IO(files_in_one_cat[j].toString(), false);
				files[i] = CountFrequency.BuildDictionary(files_in_one_cat[j].getName(), file_io.documentProcessing(), useless_words);
				CountFrequency.writeDic(files[i].getCountTable(), files_in_one_cat[j].getName(), categories[i].getCategory(), parent_path);
			}

			WordCountMatrix.createMatrix(category_name, parent_path+"File_Dictionary/"+category_name+"_FileDic/", parent_path, file_num_per_cat[i]);
			
		}
		
		/**
		 * Write inforamtion gain into files
		 * @author Jason Y J WANG
		 * @created on 31/10/2014
		 */
		GetInformationGain ig = new GetInformationGain(file_dic_path);
		ig.getIGMatrix(matrix_path);
		System.out.println("Informaiton Gain generated.");
		
	}
}