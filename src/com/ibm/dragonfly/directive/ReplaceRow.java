/*
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
 *
 */
package com.ibm.dragonfly.directive;
import com.ibm.dragonfly.Template;
import com.ibm.dragonfly.tkException;
import com.ibm.dragonfly.tkSqlException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * <p>This class represents a replaceRow directive which loads the Replace hashmap
 * with the values of "{columnName}"-> column.Value in a single row sql result set.</p>
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Directive
 * @see     Merge
 */
public class ReplaceRow {
	private static final Logger log = Logger.getLogger( ReplaceRow.class.getName() );
	private String description;
	private SqlQuery theQuery;

	/**
	 * <p>Constructor</p>
	 *
	 * @param  sql Result Set row
	 * @throws Exception - Malformed Bookmark
	 * @return The new bookmark object
	 * @throws SQLException - Empty Row or Missing Column - Template Data Error
	 */
	public ReplaceRow(ResultSet dbRow) throws tkException  {
		try {
			this.description	= dbRow.getString("description");
			this.theQuery 	= new SqlQuery( dbRow.getString("jndiSource"),
											dbRow.getString("selectColumns"),
											dbRow.getString("fromTables"),
											dbRow.getString("whereCondition"));
		} catch (SQLException e) {
			throw new tkException("Replace Row Error: "+e.getMessage(), "Invalid Directive Data");
		}		
	}

	/**
	 * <p>Get Values will execute the SQL query add values to the provided HashMap</p>
	 * 
	 * @param  Reference to current Replace Values hashmap
	 * @throws SQLException on Datasource Connection and execution
	 * @throws tkException - Data Source Error if result set rows != 1 
	 * @throws tkSqlException - Database Connection Error
	 */
	public void getValues(Template target) throws tkException, tkSqlException {
		log.fine("Adding Replace Row to " + target.getFullName());
		try {
			ResultSet rs = this.theQuery.getResultSet(target.getReplaceValues());
			rs.next();
			if ( rs.isBeforeFirst() ) {
				throw new tkException(
						"Empty Result set returned by:" + 
						this.theQuery.getQueryString(target.getReplaceValues()), 
						"Data Source Error");
			}
			if ( !rs.isLast() ) {
				throw new tkException(
						"Multiple rows returned when single row expected:" + 
						this.theQuery.getQueryString(target.getReplaceValues()), 
						"Data Source Error");
			}
			
			target.addRowReplace(rs);
			this.theQuery.close();
		} catch (SQLException e) {
			throw new tkException("Replace Row Error: "+e.getMessage(), "Invalid Merge Data");
		}
	}		

	/**
	 * <p>Get Description</p>
	 *
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}
}