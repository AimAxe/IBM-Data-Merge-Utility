/*
 * Copyright 2015 IBM
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
 *
 */
package com.ibm.tk;

import java.util.Map;

/**
 * A simple wrapper for the Template Select Query builder 
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     InsertRows
 * @see 	ReplaceRow
 * @see 	ReplaceCol
 */
public class SqlQuery {
	private String selectColumns;
	private String fromTables;
	private String whereCondition;

	/**
	 * Simple constructor 
	 *
	 */
	public SqlQuery(String columns, String table, String where ) {
		this.selectColumns = columns;
		this.fromTables = table;
		this.whereCondition = where;
	}
	
	/**
	 * Get the select query string. Note the WHERE clause will
	 * process the replace values Hash before returning. 
	 *
	 */
	public String queryString(Map<String,String> replaceValues) {
		String queryString = "SELECT " + this.selectColumns + " FROM " + this.fromTables;
		if ( !this.whereCondition.isEmpty() ) {

			// run replace stack over where condition
			String where = this.whereCondition;
			for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
				  where = where.replace(entry.getKey(), entry.getValue());
				}
			queryString += " WHERE " + where;
		}
		return queryString;
	}
}
