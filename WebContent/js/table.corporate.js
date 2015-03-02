/*
 * Editor client script for DB table replace
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function($){
	var DTypeEditor; // use a global for the submit and return data rendering in the examples

	$(document).ready(function() {
		DTypeEditor = new $.fn.dataTable.Editor( {
			ajax: "php/table.corporate.php",
	        table: "#corporate",
	        fields: [ 
		        {
	                label: "From Value",
	                name: "fromValue"
		        },
		        {
	                label: "To Value",
	                name: "toValue"
	            }	        
		    ]
	    } );
	 
	    $('#corporate').DataTable( {
	        dom: "Tfrtip",
	        ajax: "php/table.corporate.php",
	        columns: [
	            { data: "fromValue" },
	            { data: "toValue" 	}
	        ],
	        tableTools: {
	            sRowSelect: "os",
	            aButtons: [
	                { sExtends: "editor_create", editor: DTypeEditor },
	                { sExtends: "editor_edit", 	 editor: DTypeEditor },
	                { sExtends: "editor_remove", editor: DTypeEditor }
	            ]
	        }
	    } );
	} );
}(jQuery));
