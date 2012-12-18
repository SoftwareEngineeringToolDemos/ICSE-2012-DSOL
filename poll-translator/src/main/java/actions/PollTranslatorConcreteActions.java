package actions;

import java.util.ArrayList;
import java.util.List;

import org.dsol.annotation.Action;
import org.dsol.annotation.ReturnValue;

import domain.Options;
import domain.Poll;

public abstract class PollTranslatorConcreteActions{
	
	@Action(name="getPoll",service="poll")
	@ReturnValue("requestedPoll")
	public abstract Poll pollServiceAction(String pollId);

	@Action(service="languageDetector")
	@ReturnValue("detectedLanguage")
	public abstract String detectLanguage(String text);

	@Action(name="translatePoll")
	@ReturnValue("translatedPoll")
	public Poll translatePoll(Poll poll, 
                              String pollLanguage,
                              String targetLanguage){
		
	    Poll translatedPoll = new Poll(poll);

	    String title = translate(poll.getTitle(),  
	                             pollLanguage, 
	                             targetLanguage);
	    
	    String description = translate(	poll.getDescription(),  
						                pollLanguage, 
						                targetLanguage);

	    
	    List<String> translatedOptions = new ArrayList<String>();
	    for(String option:poll.getOptions().getOption()){
	    	translatedOptions.add(translate(option, pollLanguage, targetLanguage));
	    }
	    
	    
	    translatedPoll.setTitle(title);
	    translatedPoll.setOptions(new Options(translatedOptions));
	    translatedPoll.setDescription(description);
	    
	    return translatedPoll;
	}
	
	@Action(service="translator")
	public abstract String translate(String text, 
	                                 String textLanguage,  
	                                 String targetLanguage);	
}
