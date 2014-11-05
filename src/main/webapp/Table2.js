/** Baseclass for renderers of TSTable cells
 * @param create function($td, model, row, col) called on creation of a cell
 * @param update function($td, model, row, col) called on data updates of the model
 * The default implementation sets on create the text of the $td to model.getValue(row, col). On update all children of $td are removed
 * and create is called.
 * So, your minimum implementation should implement create.
 */
function TSDefaultRenderer(create, update) {
	this.create=create || function($td, model, row, col) {
		$td.text(model.getValue(row, col));
	};
	this.update=update || function($td, model, row, col) {
   		$td.children().remove();
   		this.create($td, model, row, col);
	};
}

function TSTableEvent(type, data, idx1, idx2) {
	this.type=type;
	this.data=data;
	this.idx1=idx1;
	this.idx2=idx2;
}

function TSTableModel() {
	/** @private */
	var ticketList=[];
	/** @private */
	var listeners=[];
	/** Strings to display as column headers
	 * @private */
	this.colHea=[];
	/** Names of the properties of the values to display in the columns
	 * @private */
	this.cols=[];
	
	this.setColumns=function(columns) {
		this.cols=columns;
		this.fireEvent(new TSTableEvent("metaChange"));
	}

	this.setColHeaders=function(colHeaders) {
		this.colHea=colHeaders;
		this.fireEvent(new TSTableEvent("metaChange"));
	}

	/** @public */
	this.addListener=function(listener) {
		listeners.push(listener);
	}

	/** @public */
	this.size=function() {
		return ticketList.length;
	}

	/** Reset the complete data list.
	 * @public */
	this.data=function(data) {
		log("TSTableModel.data(): "+data);
		ticketList=data;
		this.fireEvent(new TSTableEvent("dataChange"));
	}

	/** @public */
	this.push=function(ticketEntry) {
		ticketList.push(ticketEntry);
		this.fireEvent(new TSTableEvent("inserted", ticketEntry, ticketList.length-1));
	}
	
	/** @public */
	this.insert=function(ticketEntry, idx) {
		ticketList.splice(idx, 0, ticketEntry);
		this.fireEvent(new TSTableEvent("inserted", ticketEntry, idx));
	}
	
	this.indexOf=function(ticket) {
		for(var idx=0; idx<ticketList.length; idx++)
			if(ticket==ticketList[idx])
				return idx;
		return -1;
	}

	/** @public */
	this.remove=function(idx) {
		var ticket=ticketList[idx];
		ticketList.splice(idx, 1);
		log("firing event(removed, "+ticket+", "+idx+")");
		this.fireEvent(new TSTableEvent("removed", ticket, idx));
	}

	/**  Moves an entry within the list
	 * @param fromIdx move the item at
	 * @public
	 **/
	this.move=function(fromIdx, offset) {
		var ticket=ticketList[fromIdx];
		ticketList.splice(fromIdx, 1);
		ticketList.splice(fromIdx+offset, 0, ticket);
		this.fireEvent(new TSTableEvent("moved", ticket, fromIdx, fromIdx+offset));
	}

	/** Returns the object to display at the table-row[row]
	 * @param row the index of the row
	 */
	this.getRow=function(row) {
		return ticketList[row];
	}

	/** Returns the value to display at the table-field [row, field]
	 * field may be a property-name or an index.
	 * Aequivalent to getRow(row)[field]
	 */
	this.getValue=function(row, field) {
		return this.getRow(row)[field];
	}
	
	/** Call this method after the data of a row changed. If only one field changed,
	 * assign a value to field.
	 * @public
	 */
	this.rowChanged=function(row, field) {
		this.fireEvent(new TSTableEvent("changed", ticketList[row], row, field));
	}

	/** @private */
	this.fireEvent=function(evt) {
		for(var i=0; i<listeners.length; i++) {
			listeners[i].tsTableEvent(evt);
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
	var $table;
	var defaultRenderer=new TSDefaultRenderer();
	
	/** Map of render objects capable of creating and updating the td-tags within the table */
	var colRenderers=new Object();
	
	/** Map of column names to offsets (in pixel).
	 * Initial 100px per column. */
	var colOffsets=new Object();
	for(var c=0; c<model.cols.length; c++) {
		colOffsets[model.cols[c]]=100*c;
	}

	/**  @return a table instance specific classname based on the category.
	 */
	var getClassname=function(category) {
		return category+tableID;
	}

	/** Called when any td of the table must be created.
	 * Calls the column renderers to do so.
	 */
	var createTD=function($dRow, row, col) {
		log("creating <div> for row, sel="+$dRow+" row="+row+" col="+col+" value="+model.getValue(row, col));
		$newTD=$('<div>');
		$newTD.addClass(getClassname('tsCell'));
		$newTD.addClass(getClassname('tsDataCell'));
		$newTD.addClass(getClassname('tsCol_'+col));
		$newTD.css('left', colOffsets[col]);
		$dRow.append($newTD);
		if(colRenderers[col]) {
			log("using colRenderer: "+colRenderers[col]);
			colRenderers[col].create($newTD, model, row, col);
		} else { 
			log("using default renderer: "+defaultRenderer);
			defaultRenderer.create($newTD, model, row, col);
		}
	}

	/** Set a renderer for a column.
	 * Called as: renderer($td, model, row, col) where
	 * $td is the html element the data lives in.
	 * model is the TSTableModel
	 * row is the (integer) index into the model
	 * col is the index of the column in the model
	 * 
	 * The default implementation does a 
     * $td.text(model.getValue(row, col));
	 * 
	 * @public
	 */
	this.setColRenderer=function(col, renderer) {
		colRenderers[col]=renderer;
	}

	/** Rerenders the complete table, and replaces a previously created one by the new one.
	 * @private
	 */
	var render=function() {
		log("render...");
		$table=$('<div>', { id : tableID });
		$table.addClass('bordered');
		$table.addClass(getClassname('tsTable'));
		$table.css('height', ''+(model.size()+1)*25);

		// create header row
		var $hRow=$('<div>');
		$hRow.addClass(getClassname('tsRow'));
		$hRow.addClass(getClassname('tsHeaderRow'));
		$hRow.css('top', '0px');
		for(var i=0; i<model.colHea.length; i++) {
			log("adding header: "+model.colHea[i]);
			var $header=$('<div>', { text: model.colHea[i] });
			$header.addClass(getClassname('tsCell'));
			$header.addClass(getClassname('tsHeaderCell'));
			$header.addClass(getClassname('tsCol_'+model.cols[i]));
			$header.css('left', colOffsets[model.cols[i]]);
			$hRow.append($header);
		}
		$table.append($hRow);

		// add data rows
		for(var r=0; r<model.size(); r++) {
			log("adding data row: idx="+r);
			var $dRow=$('<div>');
			$dRow.addClass(getClassname('tsRow'));
			$dRow.addClass(getClassname('tsDataRow'));
			$dRow.css('top', ''+((r+1)*25));
			for(var c=0; c<model.cols.length; c++) {
				log("adding data row/field: idx="+r+" field="+model.cols[c]+" value:"+model.getValue(r, model.cols[c]));
				createTD($dRow, r, model.cols[c]);
			}
			$table.append($dRow);
		}

		// replace the previously rendered table with the new one
		var $oldTable=$('#'+tableID);
		if($oldTable.size()==0) // first call
			domParent.prepend($table);
		else
			$oldTable.replaceWith($table);

		// recalculate colOffsets (column width and position)
		for(var c=1; c<model.cols.length; c++) {
			var maxW=0;
			// find the max width of the cells of the previous column
			$('.'+getClassname('tsCol_'+model.cols[c-1])).each(function(idx) {
				var w=$(this).width();
				if(w>maxW)
					maxW=w;
			});
			// the offset of a column is the sum of all offsets left of it
			colOffsets[model.cols[c]]=maxW+colOffsets[model.cols[c-1]];
		}

		// set colOffsets (position of cell in row)
		for(var c=0; c<model.cols.length; c++)
			$('.'+getClassname('tsCol_'+model.cols[c])).css('left', ''+colOffsets[model.cols[c]]);
		
	}
	
	/** Called by TSTableModel  */
	this.tsTableEvent=function(evt) {
		log("tsTableEvent, evt.idx1="+evt.idx1);
		// TODO optimize for less display, ie move/remove/add rows
		if(evt.type==='removed') {
			$tr=$table.find('.'+getClassname('tsDataRow')).eq(evt.idx1);
			log("$table.remove on event, idx="+evt.idx1);
			$tr.fadeOut({
				complete: function() {
					$tr.remove();
				}
			});
		} else
			render();
	}

	// create the <style> for this table
	
	$('<style>.'+getClassname('tsTable')+
			' { position: relative; overflow: hidden; box-sizing: border-box }</style>').appendTo('head');
	$('<style>.'+getClassname('tsRow')+
			' { position: absolute; width: 100%; height: 25px; box-sizing: border-box }</style>').appendTo('head');
	$('<style>.'+getClassname('tsCell')+
			' { position: absolute; width: "100px"; }</style>').appendTo('head');

	model.addListener(this);
	render();
}
