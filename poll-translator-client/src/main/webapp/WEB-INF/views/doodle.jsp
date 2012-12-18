<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" lang="en"
	xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="application-name" content="Doodle">
	<meta name="msapplication-tooltip" content="Start Doodle in Site Mode">
		<meta name="msapplication-starturl" content="http://doodle-test.com/">
			<meta name="msapplication-window" content="width=1280;height=800">
				<meta name="msapplication-task"
					content="name=Schedule an event; action-uri=http://doodle-test.com/polls/wizard.html; icon-uri=/static/2012060100/graphics/doodle.ico">
					<meta name="msapplication-task"
						content="name=Make a choice; action-uri=http://doodle-test.com/polls/textWizard.html; icon-uri=/static/2012060100/graphics/doodle.ico">
						<meta name="msapplication-task"
							content="name=MyDoodle; action-uri=http://doodle-test.com/mydoodle/dashboard.html; icon-uri=/static/2012060100/graphics/doodle.ico">
							<meta name="msapplication-task"
								content="name=Blog; action-uri=http://en.blog.doodle.com/; icon-uri=/static/2012060100/graphics/doodle.ico">

								<title>Doodle: ${poll.title}</title>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/files/jquery-ui-1.8.20.custom.css" />">
	<link rel="stylesheet" type="text/css"
		href="<c:url value="/resources/files/doodle.css" />">
		<link rel="stylesheet" type="text/css"
			href="<c:url value="/resources/files/fullcalendar.css" />">
			<link rel="stylesheet" type="text/css"
				href="<c:url value="/resources/files/calendar.css" />">
				<link rel="stylesheet" type="text/css"
					href="<c:url value="/resources/files/polls.css" />">

					<style type="text/css" title="premium"></style>
					</script>

					<script type="text/javascript"
						src="<c:url value="/resources/files/jquery.min.js" />"></script>
					<script>
						//         
						window.jQuery
								|| document
										.write('<script src="/static/2012060100/js/jQuery/jquery-1.7.1.min.js"><\/script>');
						//
					</script>
					<script type="text/javascript">
						//         
						window.abTest = {
							"locale" : "en",
							loggedIn : "true"
						};
						//
					</script>
					<link rel="shortcut icon" type="image/x-icon"
						href="http://www.doodle-test.com/static/2012060100/graphics/doodle.ico">
						<link rel="apple-touch-icon"
							href="http://www.doodle-test.com/static/2012060100/graphics/mob/touch2/doodle-57.png">
							<link rel="apple-touch-icon" sizes="72x72"
								href="http://www.doodle-test.com/static/2012060100/graphics/mob/touch2/doodle-72.png">
								<link rel="apple-touch-icon" sizes="114x114"
									href="http://www.doodle-test.com/static/2012060100/graphics/mob/touch2/doodle-114.png">


									<script type="text/javascript"
										src="<c:url value="/resources/files/osd.js" />"></script>
</head>
<body class=" hasGoogleVoiceExt">

	<script type="text/javascript"
		src="<c:url value="/resources/files/common.js" />"></script>
	<script type="text/javascript">
		//         
		d.staticpath.versioned = "/static/2012060100";
		d.staticpath.unversioned = "/static";
		d.utils.setupErrorReporting();
		//
	</script>

	<div id="dTip"></div>

	<div id="background" class="doodlead"></div>
	<div id="container">
		<div id="page">
			<div id="banner" class="doodlead">
				<div id="div-gpt-ad-1322494907436-2"
					style="width: 1162px; height: 90px;">
					<iframe
						id="google_ads_iframe_/1021472/Doodle_Crunching_Leaderboard_0"
						name="google_ads_iframe_/1021472/Doodle_Crunching_Leaderboard_0"
						width="728" height="90" scrolling="no" marginwidth="0"
						marginheight="0" frameborder="0" style="border: 0px;"></iframe>
					<iframe
						id="google_ads_iframe_/1021472/Doodle_Crunching_Leaderboard_0__hidden__"
						name="google_ads_iframe_/1021472/Doodle_Crunching_Leaderboard_0__hidden__"
						width="0" height="0" scrolling="no" marginwidth="0"
						marginheight="0" frameborder="0"
						style="border: 0px; visibility: hidden; display: none;"></iframe>
				</div>
			</div>
			<br><div id="skyleftcontainer" class="transborder">
					<div id="skyleft" class="doodlead">
						<div id="div-gpt-ad-1331663301023-0"
							style="width: 160px; height: 600px; margin-top: 8px;">
							<iframe
								id="google_ads_iframe_/1021472/Doodle_Crunching_Skyscraper_Left_0"
								name="google_ads_iframe_/1021472/Doodle_Crunching_Skyscraper_Left_0"
								width="160" height="600" scrolling="no" marginwidth="0"
								marginheight="0" frameborder="0" style="border: 0px;"></iframe>
							<iframe
								id="google_ads_iframe_/1021472/Doodle_Crunching_Skyscraper_Left_0__hidden__"
								name="google_ads_iframe_/1021472/Doodle_Crunching_Skyscraper_Left_0__hidden__"
								width="0" height="0" scrolling="no" marginwidth="0"
								marginheight="0" frameborder="0"
								style="border: 0px; visibility: hidden; display: none;"></iframe>
						</div>
					</div>
				</div>
				<div class="doodleshadow">
					<div class="shadowlt"></div>
					<div class="shadowtop"></div>
					<div class="shadowrt"></div>
					<div class="shadowleft"></div>
					<div id="doodleIEcontainer">
						<div id="doodlecontainer">
							<div id="doodle">
								<div id="header" class="fixedContent clearfix">
									<div id="logo">
										<a href="http://www.doodle-test.com/"> <img class="logo"
											src="<c:url value="/resources/files/doodleR.png" />"
											width="157" height="35" alt="Doodle"></a>
									</div>

									<div id="login">
										<div id="loginLinks">
											<a class="mydoodle"
												href="http://www.doodle-test.com/mydoodle/dashboard.html">leandro.shp@gmail.com</a><span
												class="actions"><span> | </span><a
												id="statusDashboard"
												href="http://www.doodle-test.com/mydoodle/dashboard.html">MyDoodle</a><span>
													| </span><a id="statusManageAccount"
												href="http://www.doodle-test.com/mydoodle/manageAccount.html">Manage
													account</a><span> | </span><a id="statusLogout"
												href="http://www.doodle-test.com/#">Sign out</a></span>
										</div>
									</div>
									<form id="httpsLoginForm"
										action="https://www.doodle-test.com/np/mydoodle/ajax/login"
										method="POST" target="httpsLoginFrame" style="display: none">
										<input type="text" id="afEmail" name="eMailAddress"> <input
											type="password" id="afPwd" name="password"> <input
												type="hidden" id="afLocale" name="locale"> <input
													type="hidden" id="toHttps" name="toHttps"> <input
														type="submit" value="Login">
									</form>
								</div>
								<noscript>&lt;div id="content"
									class="contentPart"&gt; &lt;h3 class="spaceABefore red
									spaceCAfter"&gt;This page requires JavaScript to render the
									poll table.&lt;/h3&gt;&lt;a
									href="/kiss/d2smeh9bisf3e4am?participantKey=&amp;amp;locale=en&amp;amp;timeZone=Europe%2FRome"&gt;Switch
									to the fallback version&lt;/a&gt; &lt;/div&gt;</noscript>
								<div id="content" style="">
									<script type="text/javascript">
										//         
										// not shown if js disabled
										$("#content").show();
										//
									</script>
									<div id="anonymousHeader" class="contentPart" style="">
										<div id="anonymousMessage"></div>
									</div>

									<div id="pollHeader" class="contentPart" style="">
										<div class="clearfix fixedContent">
											<div id="pollInfos" class="fixedContent">
												<h2 id="pollTitle" style="font-weight: bold;">${poll.title}</h2>
												<div id="pollDetails" class="grey clearfix spaceEAfter">
													<p>
														<a
															href="http://www.doodle-test.com/polls/textWizard.html?pollId=d2smeh9bisf3e4am&adminKey=undefined">
															Edit </a> your poll
													</p>
													<span> | </span>
													<div id="noPart" title="0 participants">
														<div class="icon partIcon"></div>
														<span id="countParticipants">0</span>
													</div>
													<span> | </span>
													<div id="noCmt" title="0 Comments">
														<div class="icon commentIcon"></div>
														<span id="countComments">0</span>
													</div>
													<span> | </span>
													<div id="latestActivity" title="Latest activity">
														<div class="icon timeIcon"></div>
														<span id="lastActivity">218 days ago</span>
													</div>
												</div>
												<div id="pollDescription" class="spaceCBefore">${poll.description}</div>
											</div>
										</div>
									</div>
									<div id="tabsContainer" class="clearfix" style="">
										<div style="width: 814px;">
											<div id="tabs" class="clearfix">
												<div id="tableTab" class="tab activeTab">
													<div class="lTab"></div>
													<div class="mTab">Table view</div>
													<div class="rTab"></div>
												</div>
												<div id="adminTab" class="tab">
													<div class="lTab"></div>
													<div class="mTab">Administration</div>
													<div class="rTab"></div>
												</div>
												<a id="tabPrint" href="http://www.doodle-test.com/#"
													alt="Print View" title="Print View" style="display: block;"></a>
												<div>
													<div id="tabbusy" style="height: 10px"></div>
												</div>
											</div>
										</div>
									</div>

									<div id="pollInformation" class="contentPart yellowBG" style="">
										<div>
											<div class="infoBox">
												<h5 class="orange spaceEAfter">No one has participated
													in your poll yet</h5>
												<p class="spaceDAfter grey">
													<div style="margin-top: -15px;margin-bottom: 15px;color: #6F6F6F;">
														Share this link with all those who should cast their
														votes. Do not forget to cast your vote too.<br><a
															href="http://doodle-test.com/d2smeh9bisf3e4am">http://doodle-test.com/d2smeh9bisf3e4am</a>
													</div>
												</p>
												<div class="socialHookShare spaceCAfter clearfix">
													<div class="inviteByEmail">Invite by e-mail</div>
													<div class="fbshare">Share</div>
													<div class="tweet"></div>
												</div>
											</div>
										</div>
									</div>

									<div id="beforeTable" class="contentPart" style="">
										<div id="inlineCloseHeader">
											<span class="text">Most popular option: </span><span
												class="date">undefined</span><span class="spacer"> |
											</span><a id="closeArrow" class="expander">Close poll</a>
										</div>
									</div>

									<div id="pollArea" class="contentPart" style="">
										<form class="clearfix">
											<div id="ptContainer">
												<table cellspacing="0" cellpadding="0" class="poll textPoll"
													summary="${poll.title}">
													<tbody>
														<tr class="header date month">
															<th class="nonHeader partCount">0 participants</th>
															<c:forEach var="option" items="${poll.options.option}">
															<th class="xsep"><p>${option}</p></th>	
															</c:forEach>
															
														</tr>
														<tr class="participation  twoLevel partMyself">
															<td class="pname"><div style="width: 182px">
																	<label class="hiddenAcc" for="pname" title="Your name"></label><input
																		type="text" name="name" id="pname" value="Leandro Pinto"
																		class="rescueData inputText long" maxlength="64">
																</div></td>
															<c:forEach var="option" items="${poll.options.option}">
															<td id="box0" class="xsep" title="${option}"><label
																class="hiddenAcc" for="option0">${option}</label><input
																type="checkbox" name="p" id="option0"></td>	
															</c:forEach>	
											
														</tr>
													</tbody>
												</table>
											</div>
											<div id="belowTable" class="spaceDBefore spaceCAfter"
												style="">
												<input type="submit" class="submit" id="save" value="Save">
											</div>
										</form>
									</div>

									<div id="calendarView" style="display: none;"></div>

									<div id="adminView" style="display: none;"></div>

									<div id="sponsoredLinks" class="infoBox yellowBG"
										style="display: none;"></div>



									<div id="cmtsContainer" class="contentPart" style="">
										<div id="cmtsPart" class="clearfix">
											<div class="cmtsHeader">
												<h3 id="cmtTitle">Comment</h3>
												<div class="cmtsLink fixedContent clearfix">
													<a id="cmtAddArrow" class="expander"
														href="http://www.doodle-test.com/#">Add a comment</a>
												</div>
												<div id="cmtsAdd" class="cmtsAdd" style="display: none;">
													<div id="cmtatorAvatar" class="avatarSmall"></div>
													<form id="cmtForm" class="cmtForm">
														<label class="hiddenAcc" for="newCmttator"
															title="Your name"></label><input id="newCmtator"
															name="commentator" type="text" maxlength="64"
															class="newCmtator commentData rescueData"><label
															class="hiddenAcc" for="newCmt" title="Comment"></label>
														<div>
																<textarea id="newCmt" name="comment"
																	class="newCmt rescueData"></textarea>
																<span id="countCmt"></span>
															</div>
															<div class="follow">
																<input id="follow" type="checkbox" name="follow"
																	class="commentData rescueData" checked="checked"><label
																	for="follow">Inform me about further
																		participants, comments and any other events in this
																		poll.</label>
															</div>
															<p id="formError" class="error" style="display: none;"></p>
															<div>
																<input id="submitComment" name="submit" type="submit"
																	class="submit impressAds" value="Save comment">
															</div>
													</form>
												</div>
											</div>
											<div id="cmts"></div>
										</div>
									</div>

									<script type="text/javascript">
										//         
										d.l10n = {
											"insertNameInInputfieldAndSelect" : "Enter your name in the input field below and select the options of your choice.",
											"pollClosed2" : "Poll closed",
											"closePoll" : "Close poll",
											"hiddenPollCommentsExplanation" : "This is a hidden poll. The comments are only shown to the poll initiator.",
											"no" : "No",
											"noCalendar" : "No calendar",
											"calError" : "Calendar error",
											"manage" : "Manage",
											"youHaveAlreadyParticipated" : "You have already participated in this poll.",
											"loginNow" : "Sign in now",
											"emailaddress" : "E-mail address",
											"columnCountOfColumnConstraint" : "{0}&nbsp;(of&nbsp;{1})",
											"commentPl" : "Comments",
											"ok" : "OK",
											"premiumAdminAsks1" : "The administrator of this poll, {0}, asks for additional information from you.",
											"lastNames" : "Last name(s)",
											"working" : "Working",
											"refresh" : "Refresh",
											"pollIsViewedOptimized" : "This poll is viewed optimized for your browser",
											"deleteComment" : "Delete comment",
											"saveAndReturnToPoll" : "Save and return to poll",
											"requiresMyDoodle" : "requires MyDoodle",
											"openPrintView" : "Open print view",
											"learnMoreMain" : "Learn more",
											"poweredBy" : "powered by",
											"previousMonth" : "previous month",
											"noComments" : "No comments",
											"informByEmail" : "Inform by e-mail",
											"protectedS" : "protected",
											"calendarExport" : "Calendar export",
											"month" : "month",
											"expandToParticipate" : "Expand the view to participate in the poll.",
											"comment" : "Comment",
											"myDoodleAccount" : "MyDoodle account",
											"pollIsClosed" : "Poll is closed",
											"pleaseEnterComment" : "Please enter a comment in the text area.",
											"inviteByEmail" : "Invite by e-mail",
											"hiddenPoll" : "Hidden poll",
											"pollIsBigger" : "This poll is bigger than usual",
											"editedPollAndInvalidatedParticipants" : "Request existing participants to edit their entry. You could also manually revert your changes to make the question marks disappear again.",
											"calendarView" : "Calendar view",
											"followingMonth" : "following month",
											"additionalInformationRequest" : "Additional information request",
											"saveComment" : "Save comment",
											"useClassicView" : "Please use the old view for this poll.",
											"somebodyElseParticipated" : "If not, somebody else has participated with your invitation. In that case, please contact the poll initiator.",
											"yourName" : "Your name",
											"map" : "Map",
											"verifyEmail" : "Verify your e-mail address",
											"del" : "Delete",
											"weatherAtLocation" : "Weather at location",
											"toggleCalendar" : "Show / hide events from \"{0}\"",
											"saveToCalendar" : "On save synchronize to",
											"missingNameErrorMessage" : "You did not provide any name.",
											"cellPhoneNumber" : "Phone number",
											"showCompletePoll" : "Show complete poll (slow!)",
											"needInvitationDesc" : "Please contact {0} if you wish to participate.",
											"pleaseEnterYourName" : "Please enter your name in the input field.",
											"advertisement" : "Advertisement",
											"clickForMoreDetails" : "Please click here for more details.",
											"closeAsInClosePoll" : "Close",
											"closedPollMessage" : "This poll has been closed. Participation is no longer possible.",
											"proposedDates" : "Proposed dates",
											"yes" : "Yes",
											"followingWeek" : "forward one week",
											"needInvitationTitle" : "You need an invitation in order to participate in this poll",
											"reopenPoll" : "Reopen poll",
											"yourCalendars" : "Your calendars",
											"postalAddress" : "Postal address",
											"exportPollToExcel" : "Export poll to Excel",
											"calendars" : "Calendars",
											"getHereByCar" : "Get here by car",
											"comments" : "Comments",
											"week" : "week",
											"dayWithTimeSlot" : "day with proposed time slot",
											"save" : "Save",
											"featureRequiresPaidSubscription" : "This feature requires a paid Doodle subscription.",
											"pollRequestsExtraInformation" : "This poll requests extra information from participants.",
											"findInCommon" : "Mutually agree on a choice",
											"editYourPoll" : "{0} Edit {1} your poll",
											"cancel" : "Cancel",
											"where" : "Where",
											"confirmCommentDeletion" : "Do you really want to delete the following comment?",
											"connectYourCalendar" : "Connect your calendar",
											"poll" : "Poll",
											"uploadOwnAvatar" : "Upload own avatar",
											"requiresMyDoodleExplanation" : "In order to use this feature you need a MyDoodle account and must be signed in.",
											"addComment" : "Add a comment",
											"undefinedStr" : "undefined",
											"none" : "None",
											"moreAboutCalendarConnect" : "More about Calendar Connect",
											"hiddenPollCommentsExplanationHint" : "Comments of other users are not visible to you.",
											"firstNames" : "First name(s)",
											"openAccount" : "Open account",
											"closedPollFinalOptionIs" : "{0} chose",
											"exceptionTitle" : "An error has occurred",
											"participantPl" : "{0} participants",
											"showAllOptions" : "Show all {0} options",
											"commentSg" : "Comment",
											"editOrDelete" : "However, you can edit your entry or delete your entry in order to participate anew.",
											"returnToPoll" : "Return to poll",
											"autofill" : "Autofill",
											"latestActivity" : "Latest activity",
											"ifneedbeExplanation" : "(Yes) is short for Ifneedbe. Choose (Yes) if this option would also be OK, albeit not preferred.",
											"exp" : "Export",
											"exampleTextPollMessage" : "This is an example text poll.",
											"hiddenPollExplanation" : "This is a hidden poll. The participants and the result are only shown to the poll initiator.",
											"several" : "several",
											"pollInitiatedBy" : "Poll initiated by {0}",
											"day" : "day",
											"printView" : "Print View",
											"getHereByPublicTransit" : "Get here by public transport",
											"administration" : "Administration",
											"calendarDoesNotWork" : "This calendar is broken.",
											"nOfmInvitees" : "{0} of {1} invitees",
											"recommendCalendarConnect" : "We recommend you use Doodle\u2019s Calendar Connect instead.",
											"notLoggedInYetMessage" : "You are not signed in yet.",
											"ifneedbe" : "Ifneedbe",
											"today" : "today",
											"participantSg" : "{0} participant",
											"editEntry" : "Edit entry",
											"editInCalendar" : "Edit in calendar",
											"add" : "Add",
											"ie8isSlow" : "Internet Explorer 8 is very slow in rendering big tables. To prevent performance problems only your participation is shown. All other participations have been hidden.",
											"deleteEntry" : "Delete entry",
											"share" : "Share",
											"participantN" : "Participant {0}",
											"pleaseValidateEmailAddress" : "You have not yet activated your e-mail address. Please activate your e-mail address first by clicking on the link in the e-mail.",
											"calendarsPublic" : "Public calendars",
											"premiumAdminAsks2" : "The additional information cannot be viewed by other participants.",
											"optionsSkipped" : "{0} options skipped",
											"printToCompare" : "Do you need to print the poll in order to compare with your calendar?",
											"moreOnThat" : "More on that",
											"youHaveAnsweredSuggestions" : "You have answered {0} of {1} proposals.",
											"resendValidationEmail" : "Resend activation e-mail",
											"confirmParticipantDeletion" : "Do you really want to delete {0}?",
											"nobodyParticipatedYet" : "No one has participated in your poll yet",
											"exampleDatePollMessage" : "This is an example date/time poll.",
											"editedPollAndInvalidatedParticipantsTitle" : "You changed the poll and invalidated current votes (see question marks)",
											"subscribeToEvents" : "Inform me about further participants, comments and any other events in this poll.",
											"pleaseEnterValue" : "Please enter a value.",
											"maxTextLength" : "The entered value is longer than the allowed maximum of {0} characters.",
											"tableView" : "Table view",
											"pollNotSupported" : "The following poll is not supported by the new design",
											"whoIsMissingShort" : "who's missing?",
											"mostPopularOption" : "Most popular option",
											"previousWeek" : "back one week",
											"linkParticipationExplanationLong" : "Share this link with all those who should cast their votes. Do not forget to cast your vote too."
										};
										d.poll = {
											"location" : {},
											"socialHookFacebook" : "http://facebook.com/sharer.php?u=http%3A%2F%2Fdoodle-test.com%2Fd2smeh9bisf3e4am",
											"state" : "OPEN",
											"prettyUrl" : "http://doodle-test.com/d2smeh9bisf3e4am",
											"type" : "text",
											"showAds" : true,
											"skip" : 0,
											"participants" : [],
											"id" : "d2smeh9bisf3e4am",
											"title" : "What would you like to drink tonight?",
											"lastActivity" : "218 days ago",
											"socialHookTwitter" : "http://twitter.com/share?text=What+would+you+...+%7C+Please+participate+in+the+Doodle+poll%3A&url=http%3A%2F%2Fdoodle-test.com%2Fd2smeh9bisf3e4am",
											"socialHookEmail" : "mailto: ?subject=What%20would%20you%20like%20to%20drink%20tonight%3F&body=I%20would%20like%20to%20invite%20you%20to%20the%20Doodle%20poll%20%22What%20would%20you%20like%20to%20drink%20tonight%3F%22.%0A%0APlease%20follow%20the%20link%20in%20order%20to%20participate%20in%20the%20poll%3A%0Ahttp%3A%2F%2Fdoodle-test.com%2Fd2smeh9bisf3e4am",
											"createdWithYahoo" : false,
											"isByInvitationOnly" : false,
											"levels" : 2,
											"descriptionHTML" : "${poll.description}",
											"features" : {
												"pickSubCalendar" : false,
												"smsLink" : false,
												"hideAds" : false,
												"avatar" : false,
												"requireAuth" : false,
												"hideDoodleFor" : false,
												"useCustomDecoration" : false,
												"useCustomURL" : false,
												"useSSL" : false,
												"useCustomLogo" : false,
												"customTheme" : false,
												"useCustomCSS" : false,
												"quickReply" : false,
												"extraInformation" : false
											},
											"grantWrite" : true,
											"followEventsStream" : true,
											"optionsAvailable" : "yyyy",
											"eventLimit" : 100,
											"hasTimeZone" : false,
											"optionsHash" : "3f5fe33ba3baf27dccfea78062e11044",
											"optionsText" : [ "Wine", "Beer",
													"Water", "Orange Juice" ],
											"grantRead" : true,
											"category" : "any",
											"initiatorName" : "Leandro Pinto",
											"grantAdminNotification" : true,
											"skips" : [],
											"weightedOptionsHtml" : [ [
													[ "Wine", 1, "x" ],
													[ "Beer", 1, "x" ],
													[ "Water", 1, "x" ],
													[ "Orange Juice", 1, "" ] ] ],
											"hasQOptions" : false,
											"example" : false,
											"comments" : []
										};
										if (d.utils.rescue.noAccordeon === "true") {
											d.poll.skip = 0;
										}

										d.calConfig = {
											"geoIpTimeZone" : "Europe/Rome",
											"defaultTimeZone" : "GMT-12:00",
											"firstDayOfWeek" : 0,
											"lastDayOfWeek" : 6,
											"dn" : [ "Sunday", "Monday",
													"Tuesday", "Wednesday",
													"Thursday", "Friday",
													"Saturday" ],
											"dns" : [ "Sun", "Mon", "Tue",
													"Wed", "Thu", "Fri", "Sat" ],
											"dnm" : [ "S", "M", "T", "W", "T",
													"F", "S" ],
											"mn" : [ "January", "February",
													"March", "April", "May",
													"June", "July", "August",
													"September", "October",
													"November", "December" ],
											"mns" : [ "Jan", "Feb", "Mar",
													"Apr", "May", "Jun", "Jul",
													"Aug", "Sep", "Oct", "Nov",
													"Dec" ],
											"dateTimeFormatLong" : "dddd, MMMM d, yyyy h:mm TT",
											"dateTimeFormat" : "M/d/yy h:mm TT",
											"dateFormat" : "M/d/yy",
											"dateFormatLong" : "dddd, MMMM d, yyyy",
											"timeFormat" : "h:mm TT",
											"isAMPM" : true
										};
										d.publicCalendars = [];

										d.initialize();

										d.refetchUserInfo();

										if (window.location.hash === ""
												&& d.poll.adminKey) {
											d.nav.navigateTo("#admin");
										}

										// KISS
										var trackData = {
											"Context" : "Poll",
											"Poll: ID" : d.poll.id,
											"Poll: Example Poll" : d.poll.example,
											"Poll: Comments" : d.poll.comments ? d.poll.comments.length.length
													: 0,
											"Poll: Options" : d.poll.optionsText.length,
											"Poll: User has write access" : d.poll.grantWrite,
											"Poll: Hidden" : d.poll.grantRead,
											"Poll: Has TimeZone" : d.poll.hasTimeZone,
											"Poll: Level" : d.poll.levels,
											"Poll: Type" : d.poll.type,
											"Poll: Participants" : d.poll.participants ? d.poll.participants.length
													: 0,
											"Poll: State" : d.poll.state
													.toLowerCase(),
											"Poll: Column Constraint" : d.poll.columnConstraint,
											"Poll: Row Constraint" : d.poll.rowConstraint,
											"Poll: Ask Address" : d.poll.askExtra
													&& d.poll.askExtra.address,
											"Poll: Ask eMail" : d.poll.askExtra
													&& d.poll.askExtra.email,
											"Poll: Ask Phone" : d.poll.askExtra
													&& d.poll.askExtra.phone,
											"Poll: By Invitation" : d.poll.isByInvitationOnly,
											"Poll: Category" : d.poll.category,
											"Poll: has location" : d.poll.location
													&& typeof d.poll.location.name !== "undefined",
											"Poll: Shown with accordeon" : d.poll.skip === 1,
											"Poll: Keywords" : "drink, tonight, like"
										};
										kiss.track("Viewed Poll", trackData);
										// End of KISS

										//
									</script>
								</div>
								<div id="footer" class="contentPart fixedContent">
									<div>
										<div class="footerlinks">
											<a id="languageExpander" class="expander">English</a><span>|</span>
											<div>
												<a href="http://www.doodle-test.com/">Home</a>
											</div>
											<span>|</span>
											<div>
												<a href="http://en.blog.doodle.com/">Blog</a>
											</div>
											<span>|</span>
											<div>
												<a href="http://www.doodle-test.com/about/about.html">About</a>
											</div>
											<span>|</span>
											<div>
												<a href="http://www.doodle-test.com/about/advertising.html">Advertising</a>
											</div>
											<span>|</span>
											<div>
												<a href="http://www.doodle-test.com/about/media.html">Media</a>
											</div>
											<span>|</span>
											<div>
												<a href="http://www.doodle-test.com/about/tos.html">Terms</a>
											</div>
											<span>|</span>
											<div>
												<a href="http://www.doodle-test.com/about/policy.html">Privacy</a>
											</div>
											<span>|</span>
											<div>
												<a href="http://www.doodle-test.com/about/help.html">Help</a>
											</div>
											<span>|</span>
											<div>
												<a href="http://www.doodle-test.com/about/contact.html">Contact</a>
											</div>
											<div style="white-space: nowrap;">Â© Doodle AG</div>
										</div>
										<div id="languageSelector" style="display: none;"></div>
									</div>

								</div>
							</div>
						</div>
					</div>
					<div class="shadowright"></div>
					<div class="shadowlb"></div>
					<div class="shadowbottom"></div>
					<div class="shadowrb"></div>
				</div>
				<div id="skyrightcontainer" class="transborder">
					<div id="skyright" class="doodlead">
						<div id="div-gpt-ad-1322494907436-4"
							style="width: 160px; height: 600px;">
							<iframe
								id="google_ads_iframe_/1021472/Doodle_Crunching_Skyscraper_0"
								name="google_ads_iframe_/1021472/Doodle_Crunching_Skyscraper_0"
								width="160" height="600" scrolling="no" marginwidth="0"
								marginheight="0" frameborder="0" style="border: 0px;"></iframe>
							<iframe
								id="google_ads_iframe_/1021472/Doodle_Crunching_Skyscraper_0__hidden__"
								name="google_ads_iframe_/1021472/Doodle_Crunching_Skyscraper_0__hidden__"
								width="0" height="0" scrolling="no" marginwidth="0"
								marginheight="0" frameborder="0"
								style="border: 0px; visibility: hidden; display: none;"></iframe>
						</div>
					</div>
				</div>
		</div>
		<script type="text/javascript">
							//<![CDATA[
							d.utils.theming.data = {"keepTheme":false};
							d.utils.theming.init();
							//]]>
						</script>
	</div>
	<script type="text/javascript">
			//<![CDATA[
					googletag.cmd.push(function() {
						googletag.display('div-gpt-ad-1331663301023-0');

						googletag.display('div-gpt-ad-1322494907436-4');
						googletag.display('div-gpt-ad-1322494907436-2');
					});
			//]]>
			</script>

	<script type="text/javascript">
				//<![CDATA[
				$().ready(function(){
					setInterval("d.nav.navigateTo()", 300);
					$(".impressAds").click(d.ads.impressAds);
				});
				
				$.scrollDepth({
				    elements: ['#footer'] 
				  });
				//]]>
			</script>
	<iframe id="calAdsTracking" width="0" height="0" style="display: none;"></iframe>

	<!--
Worker:         doodle-test.com
Total time:     111 ms
SELECT:         3
Slow:           0
DB Bytes:       14588
-->
	<script type="text/javascript">
//<![CDATA[
if (typeof (document.addEventListener) === 'function') {document.addEventListener('keyup', function(evt){if (evt.ctrlKey && evt.keyCode == 'M'.charCodeAt(0)) {alert('Worker:         doodle-test.com\nTotal time:     111 ms\nSELECT:         3\nSlow:           0\nDB Bytes:       14588\n');}}, false);}
//]]>
</script>
</body>
</html>
