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
package com.ibm.util.merge.directive.provider;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import com.ibm.util.merge.ConnectionFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.ibm.util.merge.MergeException;

/**
 * @author flatballflyer
 *
 */
public class ProviderCsv extends ProviderHttp implements Cloneable {
	public ProviderCsv() {
		super();
		this.setType(Provider.TYPE_CSV);
	}
	
	/**
	 * Simple clone method
	 * @see com.ibm.util.merge.directive.provider.Provider#clone(com.ibm.util.merge.directive.Directive)
	 */
	public ProviderCsv clone() throws CloneNotSupportedException {
		ProviderCsv provider = (ProviderCsv) super.clone();
		return provider;
	}

	/**
	 * Retrieve the data (superclass HTTP Provider) and parse the CSV data
	 * @param cf
	 */
	public void getData(ConnectionFactory cf) throws MergeException {
		super.getData(cf);
		this.reset();
		DataTable table = new DataTable();
		CSVParser parser = null;
		try {
			parser = new CSVParser(new StringReader(this.getFetchedData()), CSVFormat.EXCEL.withHeader());
			for (String colName : parser.getHeaderMap().keySet() ) {
				table.addCol(colName);
			}
		    for (CSVRecord record : parser) {
				ArrayList<String> row = table.getNewRow();
		    	for (String field : record) {row.add(field);}
		    }
		    parser.close();
		} catch (IOException e) {
			throw new MergeException(e, "CSV Parser Stringreader IO Exception", this.getFetchedData());
		}
		if (table.size() > 0) {this.getTables().add(table);}
	}
}
