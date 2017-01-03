sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageToast",
	'sap/ui/model/json/JSONModel'
], function(Controller, MessageToast, JSONModel) {
	"use strict";

	// due to the magic of java script the usage if "this" is sometimes difficult...
	// however, thisController is set in the initilizer
	var thisController;
	
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
	
//		baseRestUrl: ''+window.location+'api/',
		baseRestUrl: 'http://localhost:8087/api/',

		// this.reloadData() may be called at any time to reload
		// the tickets table.
		reloadData: function() {
			thisController.loadData(this.baseRestUrl, function(resultData) {
				thisController.data.tickets=resultData;
				var oModel=new JSONModel(thisController.data);
				thisController.getView().setModel(oModel);
				MessageToast.show("reloaded table model");
			})
		},

		loadData: function(restUrl, successCallback) {
			jQuery.ajax({
				type: "GET",
				url: restUrl,
	   			contentType: 'application/json',
	   			dataType: 'json',
				success: function(resultData, status, jqXHR) {
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

		// this is call on click to "Up"
		onUpClicked: function(evt) {
			MessageToast.show(thisController.getContextObject(evt).ticket + " up clicked")
		},
	    
		// this is call on click to "Up"
		onDownClicked: function(evt) {
			MessageToast.show(thisController.getContextObject(evt).ticket + " down clicked")
		},

		// this is call on click to "Up"
		onDelClicked: function(evt) {
			MessageToast.show(thisController.getContextObject(evt).ticket + " del clicked")
		}
	});
});