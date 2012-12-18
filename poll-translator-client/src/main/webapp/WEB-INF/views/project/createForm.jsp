<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>.:: TReX ::.</title>
		<link rel="stylesheet" href='<c:url value="/resources/css/960.css" />' type="text/css" media="screen" charset="utf-8" />
		<!--<link rel="stylesheet" href="css/fluid.css" type="text/css" media="screen" charset="utf-8" />-->
		<link rel="stylesheet" href="<c:url value="/resources/css/template.css" />" type="text/css" media="screen" charset="utf-8" />
		<link rel="stylesheet" href="<c:url value="/resources/css/colour.css" />" type="text/css" media="screen" charset="utf-8" />
	</head>
	<body>
	<h1 id="head">TReX</h1>
	<ul id="navigation">
			<li><span class="active">My Projects</span></li>
			<li><a href="#">News</a></li>
			<li><a href="#">Users</a></li>
		</ul>
			<form:form modelAttribute="project" action="project" method="post">
			<div id="content" class="container_16 clearfix">
				<div class="grid_16">
					<h2>Create new project</h2>
					<p></p><form:errors cssClass="error"></form:errors></p>
				</div>

				<div class="grid_8">
					<p>
						<label for="name">Name* <small><form:errors path="name" /></small></label>
						<form:input path="name" /> 
					</p>
				</div>

				<div class="grid_16">
					<p class="submit">
						<input type="submit" value="Post" />
						<input type="reset" value="Reset" />
					</p>
				</div>
			</div>
			  	<%--<fieldset>		
					<legend>Account Fields</legend>
					<p>
						<form:label	for="name" path="name" cssErrorClass="error">Name</form:label><br/>
						<form:input path="name" /> <form:errors path="name" />			
					</p>
					<p>	
						<form:label for="balance" path="balance" cssErrorClass="error">Balance</form:label><br/>
						<form:input path="balance" /> <form:errors path="balance" />
					</p>
					<p>
						<form:label for="equityAllocation" path="equityAllocation" cssErrorClass="error">Equity Allocation</form:label><br/>
						<form:input path="equityAllocation" /> <form:errors path="equityAllocation" />
					</p>
					<p>	
						<form:label for="renewalDate" path="renewalDate" cssErrorClass="error">Renewal Date</form:label><br/>
						<form:input path="renewalDate" /> <form:errors path="renewalDate" />
					</p>
					<p>	
						<input type="submit" />
					</p>
				</fieldset>--%>
			</form:form>

		
		<div id="foot">
					<a href="#">Contact Me</a>
		</div>
	</body>
</html>