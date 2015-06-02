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
package com.ibm.util.merge.directive;
import org.apache.log4j.Logger;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.Provider;
import com.ibm.util.merge.directive.provider.DataTable;

/**
 * @author Mike Storey
 *
 */
public abstract class ReplaceRow extends Directive implements Cloneable {
	private static final Logger log = Logger.getLogger( ReplaceRow.class.getName() );
	
	/**
	 * Simple constructor
	 */
	public ReplaceRow() {
		super();
	}

	/**
	 * clone constructor, deep-clone of notLast and onlyLast collections
	 * @see com.ibm.util.merge.directive.Directive#clone(com.ibm.util.merge.Template)
	 */
	public ReplaceRow clone(Template owner) throws CloneNotSupportedException {
		return (ReplaceRow) super.clone();
	}

	/**
	 * @throws MergeException
	 */
	public void executeDirective() throws MergeException {
		Provider provider = this.getProvider();
		provider.getData();

		// Make sure we got some data
		if ( this.getProvider().size() < 1 ) {
			throw new MergeException("No Data Found",provider.getQueryString()); 
		}

		// Make sure we don't have a multi-table result.
		if ( provider.size() > 1 ) {
			throw new MergeException("Multi-Talbe Empty Result set returned by Directive",provider.getQueryString()); 
		}
		DataTable table = provider.getTable(0);

		// Make sure we don't have an empty result set
		if ( table.size() == 0 ) {
			throw new MergeException("Empty Result set returned by Directive",provider.getQueryString()); 
		}

		// Make sure we don't have a multi-row result set
		if ( table.size() > 1 ) {
			throw new MergeException("Multiple rows returned when single row expected", provider.getQueryString());
		}
		
		// Add the replace values
		for (int col=0; col < table.cols(); col++) {
			this.getTemplate().addReplace(table.getCol(col),table.getValue(0, col));
		}
		
		log.info("Values added by Replace Row:" + String.valueOf(table.cols()));
	}

}