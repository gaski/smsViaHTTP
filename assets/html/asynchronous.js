function Asynchronous() {
this._xmlhttp = new FactoryXMLHttpRequest();
}

function Asynchronous_call(url) {
	
var instance = this;
this._xmlhttp.open('GET', url, true);
this._xmlhttp.onreadystatechange = function() {
	switch(instance._xmlhttp.readyState) {
		case 1:
			instance.loading();
		
			break;
		case 2:
			instance.loaded();
			break;
		case 3:
			instance.interactive();
			break;
		case 4:
			instance.complete(instance._xmlhttp.status, 
					instance._xmlhttp.statusText, 
					instance._xmlhttp.responseText, 
					instance._xmlhttp.responseXML);
					
			break;
	}
}
this._xmlhttp.send(null);
}

function Asynchronous_loading() {
}
function Asynchronous_loaded() {
}
function Asynchronous_interactive() {
}
function Asynchronous_complete(status, statusText, responseText, responseHTML) {
}

Asynchronous.prototype.loading = Asynchronous_loading;
Asynchronous.prototype.loaded = Asynchronous_loaded;
Asynchronous.prototype.interactive = Asynchronous_interactive;
Asynchronous.prototype.complete = Asynchronous_complete;

Asynchronous.prototype.call = Asynchronous_call;