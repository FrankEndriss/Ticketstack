var DEBUG = true;
function log(logstring) {
	if(DEBUG)
		$("#ts_appFooter").prepend("<br/>"+logstring);
}


function TSTableEvent(type, data, idx1, idx2) {
	this.type=type;
	this.data=data;
	this.idx1=idx1 || -1;
	this.idx2=idx2 || -1;
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

	/** Reset the complete data list.
	 * @public */
	this.data=function(data) {
		this.ticketList=data;
		this.fireEvent(new TSTableEvent("anyChange", null));
	}

	/** @public */
	this.push=function(ticketEntry) {
		this.ticketList.push(ticketEntry);
		this.fireEvent(new TSTableEvent("inserted", ticketEntry, ticketList.length-1));
	}
	
	/** @public */
	this.insert=function(ticketEntry, idx) {
		this.ticketList.splice(idx, 0, ticketEntry);
		this.fireEvent(new TSTableEvent("inserted", ticketEntry, idx));
	}
	
	/** @public */
	this.remove=function(idx) {
		var ticket=ticketList[idx];
		this.ticketList.splice(idx, 1);
		this.fireEvent(new TSTableEvent("removed", ticket, idx));
	}

	/**  Moves an entry within the list
	 * @param fromIdx move the item at
	 * @public
	 **/
	this.move=function(fromIdx, offset) {
		var ticket=ticketList[fromIdx];
		this.ticketList.splice(fromIdx, 1);
		this.ticketList.splice(fromIdx+offset, 0, ticket);
		this.fireEvent(new TSTableEvent("moved", ticket, fromIdx, fromIdx+offset));
	}

	/** Returns the value to display at the table-field [row, field]
	 * field may be a property-name or an index.
	 */
	this.getValue=function(row, field) {
		return ticketList[row][field];
	}
	
	/** Call this method after the data of a row changed. If only one field changed,
	 * assign a value to field.
	 * @public
	 */
	this.rowChanged=function(row, field) {
		field=field || -1;
		this.fireEvent(new TSTableEvent("changed", ticketList[row], row, field));
	}

	/** @private */
	this.fireEvent=function(evt) {
		for(var i=0; i<this.listeners.length; i++) {
			this.listeners[i].tsTableEvent(evt);
		}
	}
	
}

/** global table counter, used for html tableID */
var tabC=0;

/** Constructor of a TSTable backed by a TSModel
 * @param htmlParent the htmlParent where the table lives in. Should be an empty div.
 * @param model the data to display
 * @returns the table
 */
function TSTable(htmlParent, model) {
	/** @private */
	//this.htmlParent=htmlParent;
	/** @private */
	//this.model=model;

	/** @readonly */
	var tableID="tstable_"+(++tabC);
	var tableSelector='#'+tableID;
	
	/** Rerenders the complete table.
	 * @public
	 */
	this.render=function() {
		try { // on first call this will throw an ex because the table does not exists
			$(tableSelector).remove();
		}catch(e) {
			log("ex while remove: "+tableSelector);
			// ignore
		}

		htmlParent.append('<table id="'+tableID+'" border=1></table>');

		// create header row
		$(tableSelector).append('<tr></tr>');
		for(var i=0; i<model.colHeaders.length; i++) {
			$(tableSelector+' > tr:first').append('<th></th>');
			$(tableSelector+' > th:eq('+i+')').text(model.colHeaders[i]);
		}

		// add data rows
		for(var r=0; r<model.size(); r++) {
			$(tableSelector).append('<tr></tr>');
			var selRow=tableSelector+' > tr:eq('+(r+1)+')';

			for(var c=0; c<model.columns.length; c++) {
				$(selRow).append('<td></td>');
				$(selRow+' > td:eq('+c+')').text(''+model.get(r, model.columns[c]));
			}
		}
	}

	/** Called by TSTableModel  */
	this.tsTableEvent=function(evt) {
		// TODO optimize for less display, ie move/remove/add rows
		this.render();
	}

	model.addListener(this);
	this.render();
}

// erzeugt die Tabelle mit den Tickets
/*
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
*/

/** Called if up-button of ticket ticketId was clicked */
/*
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
*/

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

function TicketstackBody(tableParent) {
    var model=new TSTableModel();
    
    this.loadData=function() {
		$.ajax({
			type: "GET",
			url: "http://localhost:8080/Ticketstack/rest/TicketEntryResource",
			success: function(data, status, jqXHR) {
				model.data(ticketlist_xml2json(data));
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Reload failed: "+textStatus);
			}
		});
    };

    log("creating table as child of: "+tableParent);

    new TSTable(tableParent, model);
    this.loadData();
}

//erzeugt den <body> der Seite, soll auf Seite mit leerem body aufgerufen werden.
function startTicketstackApp() {
	log("in startTicketstackApp()");
	// creates 3 divs in <body> of page
	var divIDs=[ "ts_appHeader", "ts_appBody", "ts_appFooter" ];
	for(var i=0; i<divIDs.length; i++) {
		$("body:first").append('<div id="'+divIDs[i]+'" class="ts_topLevelDiv" ></div>');
	}
    writeHeader();
    writeFooter();
    log("startup...");
    new TicketstackBody($("#ts_appBody"));
}
