<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
<title>.:: TReX ::.</title>
<link rel="stylesheet" href='<c:url value="/resources/css/960.css" />'
	type="text/css" media="screen" charset="utf-8" />
<!--<link rel="stylesheet" href="css/fluid.css" type="text/css" media="screen" charset="utf-8" />-->
<link rel="stylesheet"
	href="<c:url value="/resources/css/template.css" />" type="text/css"
	media="screen" charset="utf-8" />
<link rel="stylesheet"
	href="<c:url value="/resources/css/colour.css" />" type="text/css"
	media="screen" charset="utf-8" />
</head>
<body>

	<h1 id="head">Steal My Admin Template</h1>

	<ul id="navigation">
		<li><span class="active">Overview</span></li>
		<li><a href="#">News</a></li>
		<li><a href="#">Users</a></li>
	</ul>

	<div id="content" class="container_16 clearfix">
		<div class="grid_20">
			<p>
				<label>Your Projects<small>The list of your projects</small></label>
			</p>
		</div>

		<div class="grid_16">
			<table>
				<thead>
					<tr>
						<th>Name</th>
						<th>No. of versions</th>
						<th colspan="2" width="10%">Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="project" items="${projects}">
						<tr>
							<td>${project.name}</td>
							<td>0</td>
							<td><a href="#" class="edit">Edit</a></td>
							<td><a href="#" class="delete">Delete</a></td>
						</tr>
					</c:forEach>

				</tbody>
			</table>
		</div>
	</div>
</body>
</html>