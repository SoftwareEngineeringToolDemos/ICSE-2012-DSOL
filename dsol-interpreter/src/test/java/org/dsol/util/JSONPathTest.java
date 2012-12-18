package org.dsol.util;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;

public class JSONPathTest {
	
	@Test
	public void testJSONPAth() throws Exception {
		
		String jsonString = "{\"responseData\":{\"translatedText\":\"Ciao mondo\"},\"responseDetails\":\"\",\"responseStatus\":200,\"matches\":[{\"id\":\"424913311\",\"segment\":\"Hello World\",\"translation\":\"Ciao mondo\",\"quality\":\"74\",\"reference\":\"\",\"usage-count\":50,\"subject\":\"All\",\"created-by\":\"\",\"last-updated-by\":null,\"create-date\":\"2011-12-29 19:14:22\",\"last-update-date\":\"2011-12-29 19:14:22\",\"match\":1},{\"id\":\"0\",\"segment\":\"Hello World\",\"translation\":\"Ciao a tutti\",\"quality\":\"70\",\"reference\":\"Machine Translation provided by Google, Microsoft, Worldlingo or the MyMemory customized engine.\",\"usage-count\":1,\"subject\":\"All\",\"created-by\":\"MT!\",\"last-updated-by\":null,\"create-date\":\"2012-05-28\",\"last-update-date\":\"2012-05-28\",\"match\":0.85}]}";
		Assert.assertEquals("Ciao mondo",JsonPath.compile("$.responseData.translatedText").read(jsonString));
		Object result = JsonPath.read(jsonString, "$.matches");
		System.out.println(new Gson().toJson(result));
	}
	
	

}
