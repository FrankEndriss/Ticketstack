var DEBUG = true;

/** Simple logging function
 * @param logstring
 */
function log(logstring) {
	if(DEBUG)
		$("#ts_appFooter").prepend( $('<div>', { text: logstring }) );
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
	this.colHea=[];
	/** Names of the properties of the values to display in the columns
	 * @private */
	this.cols=[];
	
	this.setColumns=function(columns) {
		this.cols=columns;
		this.fireEvent(new TSTableEvent("anyChange", null));
	}

	this.setColHeaders=function(colHeaders) {
		this.colHea=colHeaders;
		this.fireEvent(new TSTableEvent("anyChange", null));
	}

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
		log("TSTableModel.data(): "+data);
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
		return this.ticketList[row][field];
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
 * @param domParent the domParent where the table lives in. Should be an empty div.
 * @param model the data to display
 * @returns the table
 */
function TSTable(domParent, model) {

	var tableID="tstable_"+(++tabC);
	
	/** Map of functions creating the td-tags within the table */
	var tdCreators=new Object();
	
	/** Called when any td of the table must be created.
	 * Calls the column renderers to do so.
	 */
	var createTD=function($dRow, row, col) {
		log("creating <td> for row, sel="+$dRow+" row="+row+" col="+col+" value="+model.getValue(row, col));
		if(tdCreators[col]) {
			log("using tdCreator: "+tdCreators[col]);
			tdCreators[col]($dRow, model, row, col);
		} else { // default implementation
			$dRow.append($('<td>').text(model.getValue(row, col)));
		}
	}

	/** Set a renderer for a column.
	 * Called as: renderer(rowSelector, model, row, col) where
	 * rowSelector is a selector string to get the row
	 * model is the TSTableModel
	 * row is the (integer) index into the model
	 * col is the index of the column
	 * 
	 * The default implementation does a 
     * $(rowSelector).append( $('<td>', { text: model.getValue(row, col) }) );
	 * 
	 * @public
	 */
	this.setColRenderer=function(row, renderer) {
		tdCreators[row]=renderer;
	}

	/** Rerenders the complete table.
	 * @public
	 */
	this.render=function() {
		log("render...");
		var $table=$('<table>', { id : tableID, border: 1 });

		// create header row
		var $hRow=$('<tr>');
		$table.append($hRow);

		for(var i=0; i<model.colHea.length; i++) {
			log("adding header: "+model.colHea[i]);
			var $header=$('<th>', { text: model.colHea[i] });
			$hRow.append($header);
		}

		// add data rows
		for(var r=0; r<model.size(); r++) {
			log("adding data row: idx="+r);
			var $dRow=$('<tr>');
			$table.append($dRow);
			for(var c=0; c<model.cols.length; c++) {
				log("adding data row/field: idx="+r+" field="+model.cols[c]+" value:"+model.getValue(r, model.cols[c]));
				createTD($dRow, r, model.cols[c]);
			}
		}

		var $oldTable=$('#'+tableID);
		if($oldTable.size()==0) // first call
			domParent.prepend($table);
		else
			$oldTable.replaceWith($table);
	}
	
	/** Called by TSTableModel  */
	this.tsTableEvent=function(evt) {
		// TODO optimize for less display, ie move/remove/add rows
		this.render();
	}

	model.addListener(this);
	this.render();
}

function ticketlist_xml2json(xmlData) {
	log("ticketlist_xml2json, xmlData="+xmlData+" len="+xmlData.childNodes.length);
	var tickets=xmlData.getElementsByTagName("ticketEntry");
	log("tickets "+tickets);
	var res=new Array();
	for(var i=0; i<tickets.length; i++) {
		log("creating json ticketentry: "+ticketentry+" ticket:"+tickets[i].getElementsByTagName("ticket")[0].nodeValue);
		var lTicket=tickets[i].getElementsByTagName("ticket");
		log("getElementsByTagName(ticket): "+lTicket);
		var ticketentry={ ticket: tickets[i].getElementsByTagName("ticket")[0].childNodes[0].nodeValue,
							text: tickets[i].getElementsByTagName("text")[0].childNodes[0].nodeValue,
							prio: tickets[i].getElementsByTagName("prio")[0].childNodes[0].nodeValue
		};
		log("created json ticketentry: "+ticketentry+" ticket:"+ticketentry["ticket"]);
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
    model.setColHeaders([ "Up", "Down", "Ticket", "Text", "Delete" ]);
    model.setColumns([ "buttonUp", "buttonDown", "ticket", "text", "buttonDel" ]);
    
    /** Loads the complete list from the backend and puts the
     * data into the model. Which causes a redisplay of the data-table.
     */
    var loadData=function() {
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

    /** Called if down-button of ticket ticketId was clicked */
    var onDownClick=function(ticketId) {
    	log("onUpClick("+ticketId+")");
    	$.ajax({
    		type: "POST",
    		url:  "http://localhost:8080/Ticketstack/rest/TicketEntryResource/"+ticketId+"/down",
    		success: function(data, status, jqXHR) {
    			// TODO optimize to swap row prios in model instead of reload data
    			loadData();
    		},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Ticket down failed: "+textStatus);
			}
    	});
	};

    /** Called if up-button of ticket ticketId was clicked */
    var onUpClick=function(ticketId) {
    	log("onUpClick("+ticketId+")");
    	$.ajax({
    		type: "POST",
    		url:  "http://localhost:8080/Ticketstack/rest/TicketEntryResource/"+ticketId+"/up",
    		success: function(data, status, jqXHR) {
    			// TODO optimize to swap row prios instead of reload data
    			loadData();
    		},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Ticket up failed: "+textStatus);
			}
    	});
	};

    /** Called if del-button of ticket ticketId was clicked */
    var onDelClick=function(ticketId) {
    	log("onDelClick("+ticketId+")");
    	$.ajax({
    		type: "POST",
    		url:  "http://localhost:8080/Ticketstack/rest/TicketEntryResource/"+ticketId+"/delete",
    		success: function(data, status, jqXHR) {
    			// TODO optimize to delete row in model instead of reload data
    			loadData();
    		},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Ticket delete failed: "+textStatus);
			}
    	});
	};

    log("creating table as child of: "+tableParent);

    // create the table
    var table=new TSTable(tableParent, model);
    
    // set a column renderer to display a link, not only the text
    table.setColRenderer("ticket", function($dRow, model, row, col) {
    	var ticketId=model.getValue(row, col);
    	var $field=$('<td>');
    	$dRow.append($field);
    	$field.append($("<a>", {
				href: 'https://support.neo-business.info/browse/'+ticketId,
				text: ticketId
    		}));
    });
    
    // set a column renderer to display a button
    table.setColRenderer("buttonUp", function($dRow, model, row, col) {
    	var ticketId=model.getValue(row, "ticket");
    	var $field=$('<td>');
    	$dRow.append($field);
    	if(row>0) { // not on first row
    		$field.append($("<input>", {
					type: 'button',
    				id:   'bUp'+row,
    				value: "Up"
    			})).click(function() { onUpClick(ticketId); });
    	}
    });

    // set a column renderer to display a button
    table.setColRenderer("buttonDown", function($dRow, model, row, col) {
    	var ticketId=model.getValue(row, "ticket");
    	var $field=$('<td>');
    	$dRow.append($field);
    	if(row<model.size()-1) { // not on last row
    		$field.append($("<input>", {
					type: 'button',
    				id:   'bDown'+row,
    				value: "Down"
    			})).click(function() { onDownClick(ticketId); });
    	}
    });

    // set a column renderer to display a button
    table.setColRenderer("buttonDel", function($dRow, model, row, col) {
    	var ticketId=model.getValue(row, "ticket");
    	var $field=$('<td>');
    	$dRow.append($field);
    	$field.append($("<input>", {
				type: 'button',
    			id:   'bDel'+row,
    			value: "Del"
    		})).click(function() { onDelClick(ticketId); });
    });

    // create the input form
    
    // initially load the table data from the backend
    loadData();
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
