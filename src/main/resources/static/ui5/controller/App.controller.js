sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageToast",
	'sap/ui/model/json/JSONModel'
], function(Controller, MessageToast, JSONModel) {
	"use strict";

	// due to the magic of java script the usage if "this" is sometimes difficult...
	// however, thisController is set in the initilizer
	var thisController;

//	var baseRestUrl='http://localhost:8087/api/';
	// This URL should be nicely configurable somewhere
	var baseRestUrl=(""+window.location).replace("/index.html", "").replace("/ui5", "/api/");
	
	return Controller.extend("ticketstack.controller.App", {
		
		// this object is used as the data object for the JSONModel object
		data: {
				"title" : "inline Sample Tickets",
				"tickets" : [ ]
		},

		onInit: function() {
			thisController=this;
			this.reloadData();
		},
	
		// this.reloadData() may be called at any time to reload
		// the tickets table.
		reloadData: function() {
			thisController.loadData(function(resultData) {
				// set the loaded data array on the model
				// note that we need to call getView().setModel(...)
				// to trigger a visible update of the table
				// TODO find another way by creating the JSONModel only once
				thisController.data.tickets=resultData;
				var oModel=new JSONModel(thisController.data);
				thisController.getView().setModel(oModel);
				
				// TODO
				// find first row of table, and remove or invisible 'Up'-Button
				// find last row of table, and remove or invisible 'Down'-Button
				// Since this does not work very well we might use another aproach:
				// https://blogs.sap.com/2016/06/14/sapui5-table-how-to-create-different-control-templates-in-one-column/
				// This uses a factory function, which creates the ColumnListItem for every row.
				// Within that function, the model object is available.
				// MessageToast.show("reloaded table model");
			})
		},

		loadData: function(successCallback) {
			jQuery.ajax({
				type: "GET",
				url: baseRestUrl,
	   			contentType: 'application/json',
	   			dataType: 'json',
				success: function(resultData, status, jqXHR) {
					for(var i=0; i<resultData.length; i++) 
						resultData[i].href="https://support.neo-business.info/browse/"+resultData[i].ticket;
					successCallback(resultData);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					alert("Reload failed: "+textStatus);
				}
			});
	    },
	    
		// method used in Button click handlers
		// to get the model object of the clicked row
		getContextObject: function(evt) {
			var model=thisController.getView().getModel();
			var path=evt.getSource().getBindingContext().getPath();
			return model.getProperty(path);
		},

		// this is called on click to "Up"
		onUpClicked: function(evt) {
			var oTicketEntry=thisController.getContextObject(evt);
	    	jQuery.ajax({
	    		type: "POST",
	    		url:  baseRestUrl+oTicketEntry.ticket+"/up",
	    		success: function(data, status, jqXHR) {
	    			// TODO optimize to swap row prios instead of reload data
	    			thisController.reloadData();
	    		},
				error: function(jqXHR, textStatus, errorThrown) {
					alert("Ticket up failed: "+textStatus);
				}
	    	});
		},
	    
		// this is called on click to "Up"
		onDownClicked: function(evt) {
			var oTicketEntry=thisController.getContextObject(evt);
	    	jQuery.ajax({
	    		type: "POST",
	    		url:  baseRestUrl+oTicketEntry.ticket+"/down",
	    		success: function(data, status, jqXHR) {
	    			// TODO optimize to swap row prios instead of reload data
	    			thisController.reloadData();
	    		},
				error: function(jqXHR, textStatus, errorThrown) {
					alert("Ticket up failed: "+textStatus);
				}
	    	});
		},

		// this is called on click to "Del"
		onDelClicked: function(evt) {
			var oTicketEntry=thisController.getContextObject(evt);
	    	jQuery.ajax({
	    		type: "POST",
	    		url:  baseRestUrl+oTicketEntry.ticket+"/delete",
	    		success: function(data, status, jqXHR) {
	    			// TODO optimize to swap row prios instead of reload data
	    			thisController.reloadData();
	    		},
				error: function(jqXHR, textStatus, errorThrown) {
					alert("Ticket delete failed: "+textStatus);
				}
	    	});
		}
	});
});