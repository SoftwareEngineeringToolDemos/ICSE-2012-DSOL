package it.polimi.doodletranslator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import service.Poll;
import service.PollTranslator;
import service.PollTranslatorDSOLService;

@Controller
@RequestMapping(value="/translator")
public class DoodleController {

	
	@RequestMapping(method=RequestMethod.GET)
	public String createForm(@RequestParam("pollId") String pollId, 
								@RequestParam("lang") String lang,
								ModelMap model) {
		
		PollTranslator pollTranslator = new PollTranslatorDSOLService().getPollTranslatorDSOLPort();
		Poll translatedPoll = pollTranslator.getTranslatedPoll(pollId, lang);
		model.put("poll", translatedPoll);
		return "doodle";
	}
	
}
