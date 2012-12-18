package org.dsol.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Util {
    public static InputStream getInputStream(String file){
        try {
			if (file.contains("classpath:")) {
			        // try to find in the classpath
			        file = file.replace("classpath:", "");
			        return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
			}
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
				//File not found
		}
        return null;
    }
}
