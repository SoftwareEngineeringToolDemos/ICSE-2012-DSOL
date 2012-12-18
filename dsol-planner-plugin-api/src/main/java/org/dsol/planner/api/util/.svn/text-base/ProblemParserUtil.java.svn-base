package org.dsol.planner.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dsol.planner.api.Fact;
import org.dsol.planner.api.Goal;

public class ProblemParserUtil {
	
	public static List<Goal> parseGoals(InputStream problem) throws IOException {
		return parseGoals(extractGoalAsString(problem));
	}
	
	public static List<Goal> parseGoals(String goals) {

		String patternStr = "goal(.*)";

		// Compile and use regular expression
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(goals);
		boolean matchFound = matcher.find();

		if (matchFound) {
			// the first group must be the whole string, and the second the
			// string inside the round brackets
			// for example, goal(a,b,c), the first group is goal((a),(b),(c))
			// and the second ((a),(b),(c)) that is what matters here
			String allGoals = matcher.group(1).trim();
			allGoals = allGoals.substring(1, allGoals.length() - 1);// removing
																	// round
																	// brackets
																	// ((a),(b),(c))
																	// ->
																	// (a),(b),(c)
			List<String> aux = Arrays.asList(allGoals.split(" or "));
			List<Goal> parsedGoals = new ArrayList<Goal>();
			for (String goal : aux) {
				goal = goal.trim();// remove blank spaces
				if(goal.startsWith("(")){
					goal = goal.substring(1, goal.length() - 1);// remove round
					// bracket from
					// begin and end (a)
					// -> a					
				}
				parsedGoals.add(parseGoal(goal.trim()));
			}
			return parsedGoals;
		}
		//empty goal
		Goal emptyGoal = new Goal();
		emptyGoal.add(new Fact("true"));

		return Arrays.asList(emptyGoal);
	}
	
	public static Goal parseGoal(String goalStr) {

		String patternStr = "([^\\)]+\\)?\\s*,?)";

		// Compile and use regular expression
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(goalStr);
		boolean matchFound = matcher.find();

		Goal goal = new Goal();

		while (matchFound) {
			String goalFound = matcher.group().trim();
			if(goalFound.contains("(")){
				int commaPos = goalFound.indexOf(","); 
				while(goalFound.contains(",") && commaPos < goalFound.indexOf("(")){// this means that the matches contains more than one goal, where the first is a goal without parameters, and only the last contains parameter
					String goalAux = goalFound.substring(0,commaPos);
					goal.add(new Fact(goalAux));
					goalFound = goalFound.substring(commaPos + 1);
					commaPos = goalFound.indexOf(",");
				}
				if(goalFound.endsWith(",")){
					goalFound = goalFound.substring(0, goalFound.length() - 1);
				}
				goal.add(new Fact(goalFound));				
			}
			else{
				String[] goals = goalFound.split(",");
				for(String goalAux:goals){
					goal.add(new Fact(goalAux));
				}
			}
			
			matchFound = matcher.find();
		}
		return goal;
	}
	
	/**
	 * This method does not close the stream. Do it by yourself!
	 * @param problem
	 * @return
	 * @throws IOException
	 */
	public static String extractGoalAsString(InputStream problem) throws IOException{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(problem));

		StringBuilder goalsAux = new StringBuilder();
		boolean goalsReached = false;

		while ((line = br.readLine()) != null) { // while loop begins
													// here
			if (line.contains("goal")) {
				goalsReached = true;
			}
			if (goalsReached) {
				goalsAux.append(line.trim());
				goalsAux.append(" ");
			}
		}

		return goalsAux.toString().trim();
	}

	public static String getInitialState(InputStream problem) throws IOException{
		String line = null;
		StringBuilder initialState = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(problem));
		boolean goalsReached = false;
		while ((line = br.readLine()) != null && !goalsReached) {
			if (line.contains("goal")) {
				goalsReached = true;
			} else {
				initialState.append(line);
			}
		}

		return initialState.toString();
	}
	
}
