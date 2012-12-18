package org.dsol.api;

import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.helpers.IOUtils;

public class ResourceImpl implements Resource {

	@Override
	public String getScript(String name) {
		return getResource("/js/"+name);
	}

	@Override
	public String getStyle(String name) {
		return getResource("/css/"+name);
	}

	@Override
	public byte[] getImage(String name) {
		try {
			return IOUtils.readBytesFromStream(getClass().getResourceAsStream("/css/ui-lightness/images/"+name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	@Override
	public String getHtml(String name) {
		return getResource("/html/"+name);
	}
	
	protected String getResource(String name) {
		if(!name.startsWith("/")){
			name = "/"+name;	
		}
		return convertStreamToString(getClass().getResourceAsStream(name));
	}
	
	protected String convertStreamToString(InputStream is){
		try {
			return IOUtils.readStringFromStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "<html><body>ERROR GENERATING PAGE</body></html>";
	}
}
