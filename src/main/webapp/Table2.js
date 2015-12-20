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
		log("TSTableModel.data(): "+data+" "+data.ticketEntry);
		ticketList=data.ticketEntry;
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
	
	/** Column configurations */
	var colConfigs=[];
	var col2ConfigsMap=new Object();
	
	/** Called with configs per column.
	 * Config fields:
	 * accessor: 	field name in model objects
	 * headertext:	String displayed in column header
	 * renderer:	Function called as: renderer($td, model, row, col) where
	 * 				$td is the html element the data lives in.
	 * 				model is the TSTableModel
	 * 				row is the (integer) index into the model
	 * 				col is the index of the column in the model (the accessor)
	 * 				The default implementation does a 
     * 				$td.text(model.getValue(row, col));
     * minWidth:	minimum witdh of column
     * maxWidth:	maximum witdh of column, set to minWith for fixed with
	 */
	this.colConfig=function(configs) {
		// TODO check configs to be correct objects
		colConfigs=configs;
		col2ConfigsMap=new Object();
		for(var i=0; i<configs.length; i++) {
			if(!configs[i].renderer)
				configs[i].renderer=defaultRenderer;

			if(!configs[i].offset)
				configs[i].offset=0;
			
			if(!configs[i].minWidth)
				configs[i].minWidth=10;

			if(!configs[i].maxWidth)
				configs[i].maxWidth=1000;

			col2ConfigsMap[configs[i].accessor]=configs[i];
		}
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
		colConfig=col2ConfigsMap[col];
		log("creating <div> for row, sel="+$dRow+" row="+row+" col="+colConfig.accessor+" value="+model.getValue(row, colConfig.accessor));
		$newTD=$('<div>');
		$newTD.addClass(getClassname('tsCell'));
		$newTD.addClass(getClassname('tsDataCell'));
		$newTD.addClass(getClassname('tsCol_'+colConfig.accessor));
		$newTD.css('left', colConfig.offset);
		$dRow.append($newTD);
		log("using colRenderer: "+colConfig.renderer);
		colConfig.renderer.create($newTD, model, row, colConfig.accessor);
	}

	/** Rerenders the complete table, and replaces a previously created one by the new one.
	 * @private
	 */
	var render=function() {
		log("render...");
		$table=$('<div>', { id : tableID });
		$table.addClass('bordered');
		$table.addClass(getClassname('tsTable'));
		//$table.css('height', ''+(model.size()+1)*25);

		// create header row
		var $hRow=$('<div>');
		$hRow.addClass(getClassname('tsRow'));
		$hRow.addClass(getClassname('tsHeaderRow'));
		$hRow.css('top', '0px');
		for(var i=0; i<colConfigs.length; i++) {
			log("adding header: "+colConfigs[i].headertext);
			var $header=$('<div>', { text: colConfigs[i].headertext });
			$header.addClass(getClassname('tsCell'));
			$header.addClass(getClassname('tsHeaderCell'));
			$header.addClass(getClassname('tsCol_'+colConfigs[i].accessor));
			$header.css('left', ''+colConfigs[i].offset);
			$hRow.append($header);
		}
		$table.append($hRow);

		// add data rows
		for(var r=0; r<model.size(); r++) {
			log("adding data row: idx="+r);
			var $dRow=$('<div>');
			$dRow.addClass(getClassname('tsRow'));
			$dRow.addClass(getClassname('tsDataRow'));
			//$dRow.css('top', ''+((r+1)*25)); // 25==row heigth
			for(var c=0; c<colConfigs.length; c++) {
				log("adding data row/field: idx="+r+" field="+colConfigs[c].accessor+" value:"+model.getValue(r, colConfigs[c].accessor));
				createTD($dRow, r, colConfigs[c].accessor);
			}
			$table.append($dRow);
		}

		// replace the previously rendered table with the new one
		var $oldTable=$('#'+tableID);
		if($oldTable.size()==0) // first call
			domParent.prepend($table);
		else
			$oldTable.replaceWith($table);

		// recalculate colConfigs.offset (column width and position)
		// for DOM reasons this has to happen after adding the $table to the DOM
		for(var c=1; c<colConfigs.length; c++) {
			var maxW=colConfigs[c-1].minWidth;
			// find the max width of the cells of the previous column
			$('.'+getClassname('tsCol_'+colConfigs[c-1].accessor)).each(function(idx) {
				var w=$(this).width();
				if(w>maxW)
					maxW=w;
			});
			if(maxW>colConfigs[c-1].maxWidth)
				maxW=colConfigs[c-1].maxWidth;
			
			// the offset of a column is the sum of all offsets left of it
			colConfigs[c].offset=maxW+colConfigs[c-1].offset;
			colConfigs[c-1].width=maxW;
			log("offset of col:"+c+'='+colConfigs[c].offset);
		}

		var rowWidth=$('.'+getClassname('tsRow')).width();
		log("rowWidth: "+rowWidth);

		// set colOffsets (position of cell in row)
		for(var c=0; c<colConfigs.length; c++) {
			// all cells of that column
			$cellSet=$('.'+getClassname('tsCol_'+colConfigs[c].accessor));

			$cellSet.css('left', ''+colConfigs[c].offset);
			$cellSet.css('right', ''+(rowWidth-(colConfigs[c].offset+colConfigs[c].width)));
		}
		
		// calculate and set the row heights
		var topOffset=0;
		$('.'+getClassname('tsRow')).each(function(idx) {
			$(this).css('top', ''+topOffset);
			// find max height of cells in row
			var maxH=0;
			$(this).find('.'+getClassname('tsCell')).each(function(idx) {
				var h=$(this).height();
				if(maxH<h)
					maxH=h;
			});
			$(this).height(maxH);
			topOffset+=maxH;
		});
		// set the table height as the sum of the row heights
		$($table).height(topOffset);
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
					render();
				}
			});
		} else
			render();
	}

	// create the <style> for this table
	$('<style>.'+getClassname('tsTable')+
//			' { position: relative; overflow: hidden; box-sizing: border-box }</style>').appendTo('head');
			' { position: relative; }</style>').appendTo('head');
	$('<style>.'+getClassname('tsRow')+
			' { position: absolute; width: 100%; box-sizing: border-box }</style>').appendTo('head');
	$('<style>.'+getClassname('tsCell')+
			' { position: absolute; }</style>').appendTo('head');

	model.addListener(this);
	render();
}
