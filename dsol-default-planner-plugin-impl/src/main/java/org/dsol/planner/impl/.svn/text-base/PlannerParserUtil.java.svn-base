package org.dsol.planner.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Scanner;

public class PlannerParserUtil {
	
	public static String parseFileContent(File file) throws IOException {
		String directory = file.getParent();
		Scanner scanner = new Scanner(file);
		StringWriter stringWriter = new StringWriter();
		BufferedWriter out = new BufferedWriter(stringWriter);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.contains("-include(")) {
				String fileToInclude = directory
						+ File.separator
						+ line.substring(line.indexOf("(") + 1,
								line.length() - 1);
				out.write(parseFileContent(new File(fileToInclude)));
			} else {
				out.write(line);
				out.newLine();
			}
		}

		stringWriter.close();
		out.close();

		return stringWriter.toString();
	}
	
	public static InputStream getInputStream(String file) throws IOException{
		if (file.contains("classpath:")) {
			// try to find in the classpath
			file = file.replace("classpath:", "");
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
		}
		return new ByteArrayInputStream(PlannerParserUtil.parseFileContent(new File(file)).getBytes());
	}

}
