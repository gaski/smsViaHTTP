	var active_conversation = -1; //active thread id
	var numbers = []; //thread_id -> number
	var messages_count = []; //thread_id -> messages count
		
	var getThreads = new Asynchronous(); //get all threads
	var getConversation = new Asynchronous(); //get one conversation
	var checkForNewMessages = new Asynchronous(); //check if there are new messages
	var getNewMessages = new Asynchronous(); // get new messages
	
	function setActiveConversation(id) {
		if (active_conversation >= 0)
			document.getElementById("threads").getElementsByTagName("ul")[0].getElementsByTagName("li")[active_conversation-1].className = "";
		document.getElementById("threads").getElementsByTagName("ul")[0].getElementsByTagName("li")[id-1].className = "active";
		document.getElementById("threads").getElementsByTagName("ul")[0].getElementsByTagName("li")[id-1].getElementsByTagName('span')[0].className = "";
		active_conversation = id;
		getConversation.call(id + "_get_conversation.act");
	}
	
	function pageLoaded() {
		getThreads.call('get_threads.act'); 
		setTimeout('checkForNew()', 1000); 
	}
	
	getThreads.loading = function() {
		document.getElementById("threads").innerHTML = '<img src="loading.gif" />';
	}
	
	getConversation.loading = function() {
		document.getElementById("conversation").innerHTML = '<img src="loading.gif" />';
	}	
	
	getThreads.complete = function(status, statusText, responseText, responseXML) {
		document.getElementById("threads").innerHTML = '<ul></ul>';
		x=responseXML.getElementsByTagName("conv");
		var s;
		for (i=0;i<x.length;i++)
		{	
			document.getElementById("threads").getElementsByTagName("ul")[0].innerHTML +=  "<li onClick='setActiveConversation(" + x[i].attributes.getNamedItem("id").nodeValue + ");'>" + x[i].attributes.getNamedItem("name").nodeValue + "<span> (" + x[i].attributes.getNamedItem("msg").nodeValue + ")</span></li>";
			numbers[x[i].attributes.getNamedItem("id").nodeValue] = x[i].attributes.getNamedItem("number").nodeValue;
			messages_count[x[i].attributes.getNamedItem("id").nodeValue] = x[i].attributes.getNamedItem("msg").nodeValue;
		}
		document.getElementById("threads").scrollTop = document.getElementById("threads").scrollHeight;
	}
	
	getConversation.complete = function(status, statusText, responseText, responseXML) {
		document.getElementById("conversation").innerHTML = '';
		x = responseXML.getElementsByTagName("message");
		for (i=0;i<x.length;i++) {
			document.getElementById("conversation").innerHTML += "<div class='message_" + x[i].attributes.getNamedItem("type").nodeValue + "'><p class='message_time'>" + x[i].attributes.getNamedItem("time").nodeValue + "</p><p class='message_body'>" + x[i].childNodes[0].nodeValue + "</p></div>"
		}
		document.getElementById("conversation").scrollTop = document.getElementById("conversation").scrollHeight;
	}
	
	checkForNewMessages.complete = function(status, statusText, responseText, responseXML) {
		if (responseText == "true") { 
			getNewMessages.call("get_new.act");		
		}
		setTimeout('checkForNew()', 1000);
	}
	
	getNewMessages.complete = function(status, statusText, responseText, responseXML) {
		var thread_id = -2;
		x = responseXML.getElementsByTagName("message");
		
		
		for (i=0;i<x.length;i++) {
				for (j=0;j<numbers.length;j++) {
					if ((numbers[j] == x[i].attributes.getNamedItem("from").nodeValue) || ("+48" + numbers[j] == x[i].attributes.getNamedItem("from").nodeValue) || (numbers[j] == "+48" + x[i].attributes.getNamedItem("from").nodeValue)) {
						document.getElementById("threads").getElementsByTagName("ul")[0].getElementsByTagName("li")[j-1].getElementsByTagName('span')[0].className = "span_new";
						document.getElementById("threads").getElementsByTagName("ul")[0].getElementsByTagName("li")[j-1].getElementsByTagName('span')[0].innerHTML = " (" + messages_count[j] + ")";
						thread_id = j;
						messages_count[j]++;
					}
				} //tutaj skonczyles
			if (thread_id == active_conversation) {
				document.getElementById("conversation").innerHTML += "<div class='message_2'><p class='message_time'>" + x[i].attributes.getNamedItem("time").nodeValue + "</p><p class='message_body'>" + x[i].childNodes[0].nodeValue + "</p></div>";
				document.getElementById("conversation").scrollTop = document.getElementById("conversation").scrollHeight;
				document.getElementById("threads").getElementsByTagName("ul")[0].getElementsByTagName("li")[active_conversation-1].getElementsByTagName('span')[0].className = "";
			} else {
				document.getElementById("newest").innerHTML += "<div class='new'><p class='new_time'>" + x[i].attributes.getNamedItem("time").nodeValue + "</p><p class='new_from'>" + x[i].attributes.getNamedItem("from").nodeValue + "</p><p class='new_body'>" + x[i].childNodes[0].nodeValue + "</p></div>";
				document.getElementById("newest").scrollTop = document.getElementById("newest").scrollHeight;
			}
		}	
	}

	function checkForNew() {
		checkForNewMessages.call("is_changed.act");
	}
	
	