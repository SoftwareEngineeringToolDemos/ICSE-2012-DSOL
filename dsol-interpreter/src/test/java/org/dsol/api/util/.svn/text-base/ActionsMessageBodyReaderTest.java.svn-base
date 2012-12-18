package org.dsol.api.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ws.rs.WebApplicationException;

import org.dsol.api.util.ActionsMessageBodyReader;
import org.dsol.management.Actions;
import org.junit.Assert;
import org.junit.Test;

public class ActionsMessageBodyReaderTest {

	@Test
	public void testIsReadable() {
		ActionsMessageBodyReader actionsMessageBodyReader = new ActionsMessageBodyReader();
		Assert.assertTrue(actionsMessageBodyReader.isReadable(Actions.class, null,null,null));
	}
	
	@Test
	public void testReadFrom() throws WebApplicationException, IOException {
		ActionsMessageBodyReader actionsMessageBodyReader = new ActionsMessageBodyReader();
		
		
		String data = "{\"actions\":[{\"name\":\"action1\",\"seam\":false,\"enabled\":true,\"params\":[],\"pre\":[],\"post\":[\"end\"]},{\"name\":\"action2\",\"seam\":false,\"enabled\":true,\"params\":[],\"pre\":[],\"post\":[\"step1\"]},{\"name\":\"action3\",\"seam\":false,\"enabled\":true,\"params\":[],\"pre\":[\"step1\"],\"post\":[\"end\"]}],\"concrete_action_classes\":[\"action.ConcreteActions\",\"action.ConcreteActions2\"]}";
		ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
		
		Actions actions = actionsMessageBodyReader.readFrom(null,null,null,null,null, bis);
		
		Assert.assertNotNull(actions);
		Assert.assertNotNull(actions.getActions());
		Assert.assertEquals(3, actions.getActions().size());
		
		Assert.assertNotNull(actions.getConcrete_action_classes());
		
		Assert.assertEquals(2, actions.getConcrete_action_classes().size());
		Assert.assertEquals("action.ConcreteActions", actions.getConcrete_action_classes().get(0));
		Assert.assertEquals("action.ConcreteActions2", actions.getConcrete_action_classes().get(1));
	}

}
