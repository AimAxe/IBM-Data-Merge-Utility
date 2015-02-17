<?php

/*
 * Editor server script for DB table reportReplace
 * Automatically generated by http://editor.datatables.net/generator
 */

// DataTables PHP library
include( "lib/DataTables.php" );

// Alias Editor classes so they are easy to use
use
	DataTables\Editor,
	DataTables\Editor\Field,
	DataTables\Editor\Format,
	DataTables\Editor\Join,
	DataTables\Editor\Validate;


// Build our Editor instance and process the data coming from _POST
Editor::inst( $db, 'reportReplace', 'idreportReplace' )
	->fields(
		Field::inst( 'reportReplace.idreportReplace' ),
		Field::inst( 'reportReplace.idreport' ),
		Field::inst( 'reportReplace.fromValue' ),
		Field::inst( 'reportReplace.toValue' )
	)
	->where( 'reportReplace.idreport', $_POST['reportId'] )
	->process( $_POST )
	->json();
