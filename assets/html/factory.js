function FactoryXMLHttpRequest() {
	if(window.XMLHttpRequest) {
		return new XMLHttpRequest();
}
else if(window.ActiveXObject) {
	var msxmls = new Array(
		'Msxml2.XMLHTTP.5.0',
		'Msxml2.XMLHTTP.4.0',
		'Msxml2.XMLHTTP.3.0',
		'Microsoft.XMLHTTP');
	for (var i = 0; i < msxmls.length; i++) {
		try {
		return new ActiveXObject(msxmls[i]);
		} catch(e) {
}
}
}
throw new Error("Nie moge stworzyc instancji XMLHttpRequest");
}