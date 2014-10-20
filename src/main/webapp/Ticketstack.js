var DEBUG = true;
function log(logstring) {
	if(DEBUG)
		$("#ts_appFooter").prepend("<br/>"+logstring);
}


var xmlHttp = null;

function TSTableEvent(type, data, idx=-1, idxTo=-1) {
	this.type=type;
	this.data=data;
	this.idx=idx;
	this.idxTo=idxTo;
}

function TSTableModel() {
	/** @private */
	this.ticketList=[];
	/** @private */
	this.listeners=[];
	/** Strings to display as column headers
	 * @private */
	this.colHeaders=[];
	/** Names of the properties of the values to display in the columns
	 * @private */
	this.columns=[];
	
	/** @public */
	this.addListener=function(listener) {
		this.listeners.push(listener);
	}

	/** @public */
	this.size=function() {
		return this.ticketList.length;
	}

	/** @public */
	this.push=function(ticketEntry) {
		this.ticketList.push(ticketEntry);
		this.fireEvent(new TableChangeEvent("appended", ticketEntry));
	}
	
	/** @public */
	this.insert=function(ticketEntry, idx) {
		this.ticketList.splice(idx, 0, ticketEntry);
		this.fireEvent(new TableChangeEvent("inserted", ticketEntry, idx));
	}
	
	/** @public */
	this.remove=function(idx) {
		this.ticketList.splice(idx, 1);
		this.fireEvent(new TableChangeEvent("removed", idx));
	}

	/**  Moves an entry within the list
	 * @param fromIdx move the item at
	 * @public
	 **/
	this.move=function(fromIdx, offset) {
		var ticket=ticketList[fromIdx];
		this.ticketList.splice(fromIdx, 1);
		this.ticketList.splice(fromIdx+offset, 0, ticket);
		this.fireEvent(new TableChangeEvent("moved", ticket, fromIdx, fromIdx+offset));
	}

	/** Returns the value to display at the table-field [row, field]
	 * field may be a property-name or an index.
	 */
	this.getValue=funtion(row, field) {
		return ticketList[row][field];
	}

	/** @private */
	this.fireEvent=function(evt) {
		for(var i=0; i<listeners.length; i++) {
			listeners[i].tsTableEvent(evt);
		}
	}
	
}

var tabC=1;

/** Constructor of a TSTable backed by a TSModel
 * @param htmlParent the htmlParent where the table lives in. Should be an empty div.
 * @param model the data to display
 * @returns the table
 */
function TSTable(htmlParent, model) {
	this.htmlParent=htmlParent;
	this.model=model;
	this.tableID="tstable_"+(++tabC);
	
	/** Rerenders the complete table.
	 */
	this.render=function() {
		htmlParent.remove($(tableID));
		htmlParent.append('<table id="'+tableID+'" border=1></table>');

		// create header row
		var hRow='<tr>';
		for(int i=0; i<model.colHeaders.length; i++) {
			$(tableId).append('<th></th>');
			$(tableId).('th:'+i).text(model.colHeaders[i]);
		}

		hRow+='</tr>';
		$(tableID).append(hRow);
		
		// add data rows
		for(var r=0; r<model.size(); r++) {
			$(tableId).append('<tr></tr>');
			var jQrow=$(tableId).('tr:'+(r+1));

			for(c=0; c<model.columns.length; c++) {
				jQrow.append('<td></td>');
				jQrow.('td:'+c).text(''+model.get(r, model.columns[c]));
			}
		}
	}

	this.tsTableEvent(evt) {
		// TODO optimize for less display, ie move/remove/add rows
		this.render();
	}

	model.addListener(this);
	this.render();
}

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
				:'<td> </td>')+
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
   	xmlHttp = new XMLHttpRequest();
   	xmlHttp.onreadystatechange = onUpClickReturned(xmlHttp);
   	xmlHttp.open( "POST", "http://localhost:8080/Ticketstack/rest/TicketEntryResource/"+ticketId+"/up", true);
   	xmlHttp.send( null );
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
