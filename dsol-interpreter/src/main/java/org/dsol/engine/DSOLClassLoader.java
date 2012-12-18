package org.dsol.engine;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DSOLClassLoader extends URLClassLoader{

	public DSOLClassLoader(URL[] urls,ClassLoader parent) {
		super(urls, parent);
	}
	
	public static URL[] getJars(String dir){
		if(dir == null || dir.isEmpty()){
			return new URL[0];
		}
		File file = new File(dir);
		if(!file.exists()){
			file.mkdir();
		}
		return getJars(file);
	}
	
	private static URL[] getJars(File file){
		if(file.isFile()){
			try {
				return new URL[]{file.toURI().toURL()};
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}			
		}
		
		List<URL> urls = new ArrayList<URL>(); 
		for(File children:file.listFiles()){
			urls.addAll(Arrays.asList(getJars(children)));
		}
		return urls.toArray(new URL[0]);
	}
	
	

}
