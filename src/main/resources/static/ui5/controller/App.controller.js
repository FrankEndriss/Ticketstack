sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageToast",
	'sap/ui/model/json/JSONModel'
], function(Controller, MessageToast, JSONModel) {
	"use strict";

	return Controller.extend("ticketstack.controller.App", {
		onInit: function() {
			// TODO load the data from the server
			var oModel=new JSONModel({
				"title" : "inline Sample Tickets",
				"tickets" : [
				{
					"ticket"	: "no ticket",
					"text"		: "no text",
					"prio"		: "-1"
				},
				{
					"ticket"	: "other non ticket",
					"text"		: "this one without text, too",
					"prio"		: "-2"
				}
				]
			});

			this.getView().setModel(oModel);
			//var oTable=this.getView().byId("ticketsTable");
			//oTable.setModel();
			MessageToast.show("my controller was initialized");
		}
	});
});