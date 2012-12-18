function show_message(title,message){
	$("#messages" ).dialog( "option", "title", title );
	$("#messages").html("<p>"+message+"</p>");
	$("#messages").dialog('open');
}
