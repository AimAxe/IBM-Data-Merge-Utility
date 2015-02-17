<?php

/*
 * Editor server script for DB table replace
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
Editor::inst( $db, 'collection', 'idcollection' )
	->fields(
		Field::inst( 'idcollection' )->set( false ),
		Field::inst( 'name' ),
		Field::inst( 'tableName' ),
		Field::inst( 'columnName' ),
		Field::inst( 'sampleValue' )
	)
	->process( $_POST )
	->json();
	
	
	
	
	