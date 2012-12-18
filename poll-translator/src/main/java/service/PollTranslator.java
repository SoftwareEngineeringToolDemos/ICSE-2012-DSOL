package service;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import domain.Poll;

@WebService
public interface PollTranslator{

	@WebResult(name="translatedPoll")
	public Poll getTranslatedPoll(@WebParam(name="pollId") String id,
								  @WebParam(name="language") String desiredLanguage);	
}
