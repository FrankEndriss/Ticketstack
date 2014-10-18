var DEBUG = true;
function log(logstring) {
	if(DEBUG)
		$("#ts_appFooter").prepend("<br/>"+logstring);
}


var xmlHttp = null;

// erzeugt die Tabelle mit den Tickets
function writeList() {

	log("DEBUG: retrieving data: readyState="+xmlHttp.readyState+" status="+xmlHttp.status);

	if(xmlHttp.readyState!=4 || xmlHttp.status!=200) {
		log("return");
		return;
	} else 
		log("going on...");
	
   	log("responseType="+xmlHttp.responseType);
   	log("Content-Type="+xmlHttp.getResponseHeader("Content-Type"));

	// DEBUG
    log("retrieved data: readyState="+xmlHttp.readyState+" status="+xmlHttp.status);
	log(xmlHttp.responseXML);
	// END DEBUG
	
	// List<TicketEntry>
	var jsonData;
	try {
		jsonData=ticketlist_xml2json(xmlHttp.responseXML.getElementsByTagName("ticketEntry"));
		jsonData.sort(function(a, b) {
			return a.prio-b.prio;
		});
	} catch(ex) {
		log("ex while searching ticketEntry: "+ex);
		return
	}

	log("did eval responseText, len="+jsonData.length);
		
	$("#ts_appBody").append('<table id="tsTable" border=1></table>');
	$("#tsTable").append('<tr><th>OneUp</th><th>Ticket</th><th>Text</th></tr>');

// data is defined in 'Ticketstack.json'
	for(var i=0; i<jsonData.length; i++) {
		var ticketId=jsonData[i].ticket;
		$("#tsTable").append('<tr>'+
				(i>0?
				'<td><input type="button" id="bUp'+i+'" value="Up" onclick="onUpClick('+"'"+ticketId+"'"+')" /></td>'
				:'<td>" "</td>')+
				'<td><a href="https://support.neo-business.info/browse/'+ticketId+'">'+ticketId+'</a></td>'+
				'<td>'+jsonData[i].text+'</td></tr>');
		//$("#bUp"+i).click(function() {
			//var lTicketId=ticketId;
			//onUpClick(lTicketId);
		//});
	}
}

/** Called if up-button of ticket ticketId was clicked */
function onUpClick(event) {
	log("onUpClick("+event+")");
	/*
   	xmlHttp = new XMLHttpRequest();
   	xmlHttp.onreadystatechange = onUpClickReturned(xmlHttp);
   	xmlHttp.open( "POST", "http://localhost:8080/Ticketstack/rest/TicketEntryResource/"+ticketId+"/up", true);
   	xmlHttp.send( null );
   	*/
}

function onUpClickReturned(xmlHttp) {
	if(xmlHttp.readyState!=4 || xmlHttp.status!=200) {
		log("onUpClickReturned, return");
		return;
	} else 
		loadData();
}

function ticketlist_xml2json(xmlData) {
	var res=new Array();
	for(var i=0; i<xmlData.length; i++) {
		var ticketentry={ ticket: xmlData[i].getElementsByTagName("ticket")[0].childNodes[0].nodeValue,
							text: xmlData[i].getElementsByTagName("text")[0].childNodes[0].nodeValue,
							prio: xmlData[i].getElementsByTagName("prio")[0].childNodes[0].nodeValue
		};
		res.push(ticketentry);
	}
	return res;
}


function loadData() {

	try {
    	xmlHttp = new XMLHttpRequest();
    	//xmlHttp.overrideMimeType("application/json");
    	//xmlHttp.responseType="json";
    	// TODO: call onLoadDataReturn instead of writeList, and there writeList, so no global var is needed
    	xmlHttp.onreadystatechange = writeList;
    	xmlHttp.open( "GET", "http://localhost:8080/Ticketstack/rest/TicketEntryResource", true);
    	//xmlHttp.setRequestHeader("Accept", "application/json");
    	xmlHttp.send( null );
	}catch(ex) {
		log("ex while XMLHttpRequest: "+ex);
	}

    // return jsonHttp.responseText;
}

// schreibt die Überschrift
function writeHeader() {
	$("#ts_appHeader").append('<h2>Ticketstack</h2>');
}

//schreibt Content am Ende der Seite
function writeFooter() {
	$("#ts_appFooter").append(
        	'<p>TODO:'+
            '<br/>-mit Buttons hoch/runter, erfordert separate Datenhaltung'+
            '<br/>-Wiedervorlage an bestimmtem Datum, also extra-Liste per Datum'+
            '<p/>');

}

//erzeugt den <body> der Seite, soll auf Seite mit leerem body aufgerufen werden.
function startTicketstackApp() {
	log("in startTicketstackApp()");
	// creates 3 divs in <body> of page
	var divIDs=[ "ts_appHeader", "ts_appBody", "ts_appFooter" ];
	for(var i=0; i<divIDs.length; i++) {
		$("body:first").append('<div id="'+divIDs[i]+'" class="ts:topLevelDiv" ></div>');
	}
    writeHeader();
    loadData();
    writeFooter();
}
