	function resetNewAction(){
		document.forms["form_abstract_actions"].reset();
		$("#params").html("");
		$("#post").html("");
		$("#pre").html("");
	}

	function showForm(){
		$("#form_action_div").dialog('open');
	}

	function add_param(){
		$("#params").append("<div><input type='text' name='param_vals'/></div>");
	}
	
	function add_pre(){
		$("#pre").append("<div><input type='text' name='pre_vals'/></div>");
	}
	
	function add_post(){
		$("#post").append("<div><input type='text' name='post_vals'/></div>");
	}
	
	function remove(action){
		$("#delete_"+action).hide();
		$("#cancel_delete_"+action).show();
		$('#action_info_'+action).addClass("removed");
		$('#ck_enabled_'+action).attr("disabled", true);
		deleted_actions.push(action);
	}
	
	function back(action){
		$("#delete_"+action).show();
		$("#cancel_delete_"+action).hide();
		$('#action_info_'+action).removeClass("removed");
		$('#ck_enabled_'+action).removeAttr("disabled");
		
		var deleted_action_aux = deleted_actions;
		deleted_actions = new Array();
		for(var i = 0;i < deleted_action_aux.length;i++){
			if(deleted_action_aux[i] != action){
				deleted_actions.push(deleted_action_aux[i]);
			}
		}
	}
	
	function save(applyForRunningInstances){
		var actions_to_send = create_actions_to_send();
		
		var new_concrete_action_classes = [];
		for(var i=0;i<concrete_actions_classes.length;i++){
			if(jQuery.inArray( concrete_actions_classes[i], deleted_classes ) == -1){
				new_concrete_action_classes.push(concrete_actions_classes[i]);
			}
		}
		new_concrete_action_classes = new_concrete_action_classes.concat(get_values("form_concrete_actions","concrete_action_field"));
		
		var data = {"actions":actions_to_send, "concrete_action_classes":new_concrete_action_classes};		
 		var encoded = $.toJSON(data);
 		$.ajax({
			  url: getSaveUrl(applyForRunningInstances),
			  type: "POST",
			  contentType: "application/json",
			  data: encoded,
			  success: function() {
				  reload = true;
				  show_message('Success','<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>New orchestration model saved successfully.');
			 },
			  error: function() {
				  show_message('Error','<span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 50px 0;"></span>Error while saving changes.');
			  },
			});
	}
	
	function getSaveUrl(applyForRunningInstances){
		return window.location.pathname.replace("app","management")+'?all='+applyForRunningInstances;
	}
	
	
	function plan(){
		var actions_to_send = create_actions_to_send();
		
		var message = ""; 
		var initial_state = $("#initial_state").val();
		var goal = $("#goal").val();
		
		if(jQuery.trim(initial_state).length == 0){
			initial_state= "true";
		}
		initial_state= "start(" + initial_state +")";
		if(jQuery.trim(goal).length == 0){
			message = message + "<p>Please, inform the goal.</p>";
		}
		goal= "goal(" + goal +")";
		if(message.length != 0){
			show_message('Attention',message);
			return;
		}
		
		console.log(goal);
		
		var data = {"initial_state":initial_state,"goal":goal,"actions":actions_to_send,"removed_actions":removed_steps};		
 		var encoded = $.toJSON(data);
 		
 		console.log(data);
 		
 		$.ajax({
			  url: getPlanUrl(window.location.pathname),
			  type: "POST",
			  contentType: "application/json",
			  data: encoded,
			  success: function(result) {
				  if(result["error"]){
					  $("#planResult").html("<h1>Error creating plan</h1>");
					  $("#planResult").append("<div>"+result["error"]+"</h1>");
				  }
				  else{
					  $("#planResult").html("<h1>Plan found</h1>");
					  
					  levels = result["plan"]; 
					  for(var w = 0; w < levels.length; w++){
						  var steps = levels[w];
						  
						  var levelDescriptionDiv = "<div class='levelDescription'> -->&nbsp;&nbsp;";
						  if(levels.length == 1){
							  levelDescriptionDiv += "Run";
						  }
						  else{
							  if(w == 0){
								  levelDescriptionDiv += "Initially, ";
							  }
							  else if(w == (levels.length - 1)){
								  levelDescriptionDiv += "And finally, ";
							  }
							  else{
								  levelDescriptionDiv += "Then, ";
							  }
							  
							  levelDescriptionDiv += "run";							  
						  }

						  if(steps.length > 1){
							  levelDescriptionDiv += " in parallel";
						  }
						  
						  levelDescriptionDiv += "</div>";  
						  $("#planResult").append(levelDescriptionDiv);
						  for(var i = 0; i < steps.length; i++){
							  var stepDiv = "<div id='step_"+w+"_"+i+"'>";
							  var planStep = "<span class='actionName'>"+steps[i]["name"]+"</span>";

							  planStep = planStep+ "&nbsp;(";
							  var params = steps[i]["params"];
							  for(var j = 0;j<params.length;j++){
								  if(j != 0){
									  planStep = planStep + ", "  
								  }
								  planStep = planStep + params[j];
							  }
							  planStep = planStep + ")";
							  stepDiv = stepDiv + planStep;
							  stepDiv = stepDiv +'<a title="Mark step '+planStep+' as faulty" style="float:left;" class="ui-icon ui-icon-close" href="javascript:remove_step('+w+','+i+');"></a>'
							  stepDiv = stepDiv +"</div>";
							  $("#planResult").append(stepDiv);  
						  }  
					  }
				  }
			 },
			  error: function(error) {
				 show_message('Error','Error during planning');
			  },
			});
	}
	
	function getPlanUrl(currentLocation){
		return currentLocation.replace("app","management")+"/test_plan";
	}
	
	function remove_step(level, index){
		$("#step_"+level+"_"+index).addClass("removed_class");
		removed_steps.push(levels[level][index]);
	}
	
	function create_actions_to_send(){
		var actions_to_send = new Array();

		for(var i = 0;i < actions.length;i++){
			if(jQuery.inArray( actions[i]["name"], deleted_actions ) == -1){
				actions_to_send.push(actions[i]);
			}
		}
		actions_to_send = actions_to_send.concat(new_actions);
		return actions_to_send;
	}
	
	function add(){
		var action_name = $("#action_name").val();
		var params = get_values("form_abstract_actions","param_vals");
		var preconds = get_values("form_abstract_actions","pre_vals");
		var postconds = get_values("form_abstract_actions","post_vals");
		var seam = document.getElementById("seam").checked;
		if(!seam){
			seam = false;
		}
		
		var message = "";
		if(jQuery.trim(action_name).length == 0){
			message = "<p>Action name is mandatory</p>";
		}
		if(postconds.length == 0){
			message = message + "<p>You must include at least one post condition.</p>";
		}

		if(message.length != 0){
			show_message('Attention',message);
			return;
		}
		
		var action_data = {"name":action_name,"seam":seam,"params":params,"pre":preconds,"post":postconds,"enabled":true};
		add_action_to_table(action_data);
		new_actions.push(action_data);
		
		document.forms["form_abstract_actions"].reset();
	}
	
	function load_from_file(actions_to_load){
		actions_to_load = actions_to_load["actions"];
		for(var i=0;i<actions_to_load.length;i++){
			if(!action_exists(actions_to_load[i]["name"])){
				add_action_to_table(actions_to_load[i]);
				new_actions.push(actions_to_load[i]);
			}
		}
	}
	
	function action_exists(action_name){
		for(var i=0;i<actions.length;i++){
			if(actions[i]["name"] == action_name){
				return true;
			}
		}
		return false;
	}
	
	function get_values(form_name,name){
		var values = document.forms[form_name][name];
		var result = new Array();
		if(values){
			if(values.length){
				for(var i=0;i<values.length;i++){
					var val = jQuery.trim(values[i].value);
					if(val.length > 0){
						result.push(val);	
					}						
				}
			}
			else{
				var val = jQuery.trim(values.value);
				if(val.length > 0){
					result.push(val);
				}
			}
		}
		return result;
	}
	
   	var actionsHTML = "";
   	function append(text){
   		actionsHTML = actionsHTML + text;
   	}
   	
   	function add_action_to_table(action){
   		var actionName = action['name'];
   		var seam = action['seam'];
   		var enabled = action['enabled'];
   		
   		var actionDiv = document.createElement('div');	
   		actionDiv.setAttribute('class', 'actions');
   		actionDiv.setAttribute('id', actionName); 
   		
   		
   		var actions_info = document.createElement('div');
   		actions_info.setAttribute('id', 'action_info_'+actionName);
   		actions_info.setAttribute('class', 'action_info');
   		
   		
   		var actionSignature = actionName;
   		actionSignature = actionSignature + ' (';
  		var params = action['params'];
  		for(var j = 0;j < params.length;j++){
  			if(j != 0){
  				actionSignature = actionSignature + ', ';
  			}
  			actionSignature = actionSignature + params[j];
  		}
  		actionSignature = actionSignature + ')';   		
   		
  		var div_action_name = document.createElement('div');
  		div_action_name.setAttribute('class','inner_div');
  		div_action_name.innerHTML = '<em>'+(seam?'seam ':'')+'action </em>'+actionSignature;

  		//Preconds
  		var preconds = action['pre'];
  		var preconditions = '';
  		for(var j = 0;j < preconds.length;j++){
  			if(j != 0){
  				preconditions = preconditions + ', ';		
  			}
  			preconditions = preconditions + preconds[j];
  		}  		
  		var div_action_preconds = document.createElement('div');
  		div_action_preconds.setAttribute('class','inner_div');
  		div_action_preconds.innerHTML = '<em>pre: </em>'+preconditions;
  		
  		//Postconds
  		var postconds = action['post'];
  		var postconditions = '';
  		for(var j = 0;j < postconds.length;j++){
  			if(j != 0){
  				postconditions = postconditions + ', ';		
  			}
  			postconditions = postconditions + postconds[j];
  		}  		
  		var div_action_postconds = document.createElement('div');
  		div_action_postconds.setAttribute('class','inner_div');
  		div_action_postconds.innerHTML = '<em>post: </em>'+postconditions;
  		
  		var enabled_input = document.createElement('input');
  		enabled_input.setAttribute('type','checkbox');
  		enabled_input.setAttribute('id','ck_enabled_'+actionName);
  		if(enabled){
  			enabled_input.setAttribute('checked','checked');	
  		}
  		enabled_input.onclick = function(){enable(event.target)};
  		
  		var label_enabled_input = document.createElement('label');
  		label_enabled_input.setAttribute('for', 'ck_enabled_'+actionName);
  		
  		label_enabled_input.innerHTML = '<em>enabled: </em>';
  		
  		
  		var div_enabled = document.createElement('div');
  		div_enabled.setAttribute('class','inner_div');
  		div_enabled.appendChild(label_enabled_input);
  		div_enabled.appendChild(enabled_input);
  		
  		actions_info.appendChild(div_action_name);
  		actions_info.appendChild(div_action_preconds);
  		actions_info.appendChild(div_action_postconds);
  		actions_info.appendChild(div_enabled);
  		
   		actionDiv.appendChild(actions_info);
   		
   		
   		var div_buttons = document.createElement('div');
   		div_buttons.setAttribute('class','buttons');
   		
   		var concrete_actions_button = document.createElement('a');
   		concrete_actions_button.setAttribute('href',"javascript:showConcreteActions('"+actionName+"');");
   		concrete_actions_button.setAttribute('id',"concrete_actions_"+actionName);
   		concrete_actions_button.setAttribute('class',"concrete_link");
   		concrete_actions_button.innerHTML = 'Concrete actions'; 

   		var delete_button = document.createElement('a');
   		delete_button.setAttribute('href',"javascript:remove('"+actionName+"');");
   		delete_button.setAttribute('id',"delete_"+actionName);
   		delete_button.setAttribute('class',"remove_link");
   		delete_button.innerHTML = 'Delete';
   		
   		var cancel_delete_button = document.createElement('a');
   		cancel_delete_button.setAttribute('href',"javascript:back('"+actionName+"');");
   		cancel_delete_button.setAttribute('id',"cancel_delete_"+actionName);
   		cancel_delete_button.setAttribute('class',"remove_link");
   		cancel_delete_button.innerHTML = 'Cancel Delete';
   		cancel_delete_button.style.display = 'none';
   		
   		div_buttons.appendChild(concrete_actions_button);
   		div_buttons.appendChild(delete_button);
   		div_buttons.appendChild(cancel_delete_button);
   		actionDiv.appendChild(div_buttons);
   		
   		$("#actions_container").append(actionDiv);
   		$("#concrete_actions_"+actionName).button();
   		$("#delete_"+actionName).button();
   		$("#cancel_delete_"+actionName).button();
   		/*actionsHTML = "";
		
   		var actionName = action['name'];
   		var seam = action['seam'];
   		var enabled = action['enabled'];
   		
   		append('<div id="div_'+actionName+'" class="actions');
   		if(seam){
   			append(" seam_actions");
   		}
   		append('">');
   		append('<div class="put_back" id="back_'+actionName+'" ><a href="javascript:back(\''+actionName+'\');">Put me back, please! :)</a></div>');
   		append('<div id="inner_div_'+actionName+'">');
   		append('<div><em>');
   		
   		
  		if(seam){
  			append('seam ');
  		}
  		append('action&nbsp;</em>');
  		append(actionName);
  		append('&nbsp;(');
  		var params = action['params'];
  		for(var j = 0;j < params.length;j++){
  			if(j != 0){
  				append(',&nbsp;');		
  			}
  			append(params[j]);
  		}
  		append(')');
  		append('</div>');

  		// preconditions
  		append('<div>');
  		append('<em>pre:</em>&nbsp;');
  		
  		var preconds = action['pre'];
  		for(var j = 0;j < preconds.length;j++){
  			if(j != 0){
  				append(',&nbsp;');		
  			}
  			append(preconds[j]);
  		}  		
  		append('</div>');

  		// postconditions
  		append('<div>');
  		append('<em>post:</em>&nbsp;');
  		
  		
  		var postconds = action['post'];
  		for(var j = 0;j < postconds.length;j++){
  			if(j != 0){
  				append(',&nbsp;');		
  			}
  			append(postconds[j]);
  		}  		
  		append('</div>');
  		append('<div>');
  		append('<em>enabled:</em>&nbsp;<input type="checkbox" id="ck_enabled_'+actionName+'" onclick="enable(event.target)"');
  		if(enabled){
  			append('checked="checked"');
  		}
  		append('/>');
  		append('</div>');
  		if(!seam){
	  		append('<div>');
	  		append('<a href="javascript:showConcreteActions(\''+actionName+'\');" class="concrete_link">See available concrete actions</a>');
	  		append('</div>');
  		}
  		append('<div class="remove" id="remove_'+actionName+'"><a title="Remove action from model" class="remove_link" href="javascript:remove(\''+actionName+'\');">Remove action from model</a></div>');
  		
  		append('</div>');
  		console.log(actionsHTML);
  		
  		
  		
  		$("#actions_container").append(actionsHTML);
  	*/	
   	}
   	
   	function add_class_field(){
   		$("#concrete_actions_container").append('<li><input type="text" name="concrete_action_field" class="concrete_input"/></li>');
   	}
   	
   	function add_concrete_action_class(class_name, id){
   		$("#concrete_actions_container").append("<li style='width:300px;' id='class_"+id+"'><div>"+class_name+"<a title='remove class "+class_name+"' class='ui-icon ui-icon-close' style='float:right' href='javascript:remove_class(\""+class_name+"\",\"class_"+id+"\");'>X</a><br clear='all'/></div></li>")
   	}
   	
   	function remove_class(class_name, id){
   		$("#"+id).addClass("removed_class");
   		deleted_classes.push(class_name);
   	}
   	
   	function enable(checkbox){
   		var enabled = checkbox.checked;
   		var actionName = checkbox.id.substring(11);
   		var all_actions = actions.concat(new_actions);
   		
   		for(var i=0;i<all_actions.length;i++){
   			if(all_actions[i]["name"] == actionName){
   				all_actions[i]["enabled"] = enabled;
   			}
   		}
   	}
   	
   	function showConcreteActions(action){
   		$("#dialog_concrete_actions").html("");
   		var methods = concrete_actions[action];
   		if(methods){
   	   		for(var i = 0; i < methods.length;i++){
   	   			$("#dialog_concrete_actions").append("<div class='method'>"+methods[i]+"</div>");
   	   		}   			
   		}
   		else{
   			$("#dialog_concrete_actions").append("No concrete actions attached to this actions");
   		}
  		$("#dialog_concrete_actions").dialog("open");
   		
   	}
   	
   	function showQoS(actionName){
   		$("#dialog_qos").html("");
   		qos = getAction(actionName)["qos"];
   		lastActionQoS = actionName;
   		if(qos.length > 0){
   	  	   for(var i = 0; i < qos.length; i++){
   	  		   var qosName = qos[i]["name"];
   	  		   $("#dialog_qos").append("<div class='method'>"+qosName+"&nbsp;:&nbsp;"+"<input id='input_qos_"+qosName+"' type='text' name='"+qosName+"' value='"+qos[i]["value"]+"'/></div>");   
   	  	   }
   		}
   		else{
   			$("#dialog_qos").append("No qos defined.");
   		}
  		$("#dialog_qos").dialog("open");
   		
   	}
   	
   	function getAction(actionName){
   		for(var i = 0; i< actions.length;i++){
   			var action = actions[i];
   			if(action["name"] == actionName){
   				return action;
   			}
   		}
   		return {};
   	}