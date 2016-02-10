var DEBUG = true;

/** Simple logging function
 * @param logstring
 */
function log(logstring) {
	if(DEBUG)
		$("#ts_appFooter").prepend( $('<div>', { text: logstring }) );
}


// schreibt die Überschrift
function writeHeader() {
	$("#ts_appHeader").append('<h2>Ticketstack</h2>');
}

//schreibt Content am Ende der Seite
function writeFooter() {
	$("#ts_appFooter").append(
        	'<p>TODO:'+
            '<br/>window.location='+window.location+
            '<br/>window.location.origin='+window.location.origin+
            '<p/>');

}

function TicketstackBody(tableParent, inputParent) {
	// columns of the table listing the tickets
    var colConfigs=[
                    {
                    	accessor: 'buttonUp',
                    	headertext: 'Up',
                    	minWidth: 50,
                    	renderer: new TSDefaultRenderer(
                    				function($td, model, row, col) {
                    					var ticketId=model.getValue(row, "ticket");
                    					if(row>0) { // not on first row
                    						$td.append($("<input>", {
                    							type: 'button',
                    							id:   'bUp'+row,
                    							value: "Up"
                    						})).click(function() { onUpClick(ticketId); });
                    					}
                    				}
                    		)
                    },
                    {
                    	accessor: 'buttonDown',
                    	headertext: 'Down',
                    	renderer: new TSDefaultRenderer(
                    				function($td, model, row, col) {
                    					var ticketId=model.getValue(row, "ticket");
                    					if(row<model.size()-1) { // not on last row
                    						$td.append($("<input>", {
                    							type: 'button',
                    							id:   'bDown'+row,
                    							value: "Down"
                    						})).click(function() { onDownClick(ticketId); });
                    					}
                    				}
                    		)	
                    },
                    {
                    	accessor: 'ticket',
                    	headertext: 'Ticket',
                    	renderer: new TSDefaultRenderer(
                    				function($td, model, row, col) {
                    					var ticketId=model.getValue(row, col);
                    					$td.append($("<a>", {
                    						href: 'https://support.neo-business.info/browse/'+ticketId,
                    						text: ticketId
                    					}))
                    					$td.children('a').css('white-space', 'nowrap');
                    				}
                    		)
                    },
                    {
                    	accessor: 'text',
                    	headertext: 'Text'
                    },
                    {
                    	accessor: 'buttonDel',
                    	headertext: 'Delete',
                    	renderer: new TSDefaultRenderer(
                    				function($td, model, row, col) {
                    					var ticket=model.getRow(row);
                    					$td.append($("<input>", {
                    						type: 'button',
                    						//id:   'bDel'+row,
                    						value: "Del"
                    					})).click(function() { onDelClick(ticket); });
                    				}
                    		)
                    }
    ];
    
    // data model of the table
    var model=new TSTableModel();

    // rest api url, should be externalized
	var baseRestUrl= ''+window.location+'api/';
	log('using baseRestUrl: '+baseRestUrl);

    /** Loads the complete list from the backend and puts the
     * data into the model. Which causes a redisplay of the data-table.
     */
    var loadData=function() {
		$.ajax({
			type: "GET",
			url: baseRestUrl,
   			contentType: 'application/json',
   			dataType: 'json',
			success: function(data, status, jqXHR) {
				log("server sent ticketlist: "+data);
				model.data(data);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Reload failed: "+textStatus);
			}
		});
    };

    /** Called if down-button of ticket ticketId was clicked */
    var onDownClick=function(ticketId) {
    	log("onDownClick("+ticketId+")");
    	$.ajax({
    		type: "POST",
    		url:  baseRestUrl+ticketId+"/down",
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
    		url:  baseRestUrl+ticketId+"/up",
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
    var onDelClick=function(ticket) {
    	log("onDelClick("+ticket.ticket+");");
    	var idx=model.indexOf(ticket);
    	log('index of '+ticket.ticket+' = '+idx);
    	model.remove(idx),
    	$.ajax({
    		type: "POST",
    		url:  baseRestUrl+ticket.ticket+"/delete",
    		success: function(data, status, jqXHR) {
    			log("onDelClick:success"+ticket.ticket);
    		},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Ticket delete failed: "+textStatus);
				loadData();
			}
    	});
	};

    log("creating table as child of: "+tableParent);

    // create the table
    var table=new TSTable(tableParent, model);
    // configure the columns
    table.colConfig(colConfigs);
    
    
    // create the input form for new tickets
    $form=$('<form>').css('width', '60%');
    $div=$('<div>').appendTo($form);
    
    $('<label>', {
    	for: 'ts_app_inp_ticket'
    }).text('TicketID').appendTo($div);

    $div.append($('<input>', {
    	type: 'text',
    	id: 'ts_app_inp_ticket',
    }));
    
    $('<label>', {
    	for: 'ts_app_inp_text'
    }).text('Text').appendTo($div);

    $div.append($('<textarea>', {
    	id: 'ts_app_inp_text',
    	label: 'Text',
    	rows: 10,
    	width: '100%'
    	//cols: 80
    }));

    $div.append($('<input>', {
    	id: 'addButton',
    	type: 'button',
    	value: 'Add'
    	}).click(function() {
    		// onAddClicked()
    		var newTicket=$('#ts_app_inp_ticket').val();
    		var newText=$('#ts_app_inp_text').val();

    		log("adding: ticket:"+newTicket+" text:"+newText);

    		$.ajax({
    			type: 'POST',
    			url: baseRestUrl,
    			contentType: 'application/json',
    			dataType: 'json',
    			data: JSON.stringify({ ticket: newTicket, text: newText, prio: -1 }),
    			success: function(data, status, jqXHR) {
    				// TODO optimize to insert row in model instead of reload data
    				loadData();
    			},
    			error: function(jqXHR, textStatus, errorThrown) {
    				alert("Ticket creation failed: "+textStatus+" "+errorThrown);
    			}
    		});
    	}));
    
    $div.addClass('bordered');
    inputParent.append($form);

    
    // initially load the table data from the backend
    loadData();
}

//erzeugt den <body> der Seite, soll auf Seite mit leerem body aufgerufen werden.
function startTicketstackApp() {
	log("in startTicketstackApp()");

	// creates 4 divs in <body> of page
	var divIDs=[ "ts_appHeader", "ts_appTable", "ts_appInput", "ts_appFooter" ];
	for(var i=0; i<divIDs.length; i++) {
		$div=$('<div id="'+divIDs[i]+'"></div>');
		$div.addClass('ts_topLevelDiv')
//		$div.css('align-content', 'stretch');
		$div.css('padding', '10')
		$("body:first").append($div);
	}

    writeHeader();
    writeFooter();
    log("startup...");
    new TicketstackBody($("#ts_appTable"), $('#ts_appInput'));
}
