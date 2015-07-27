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
package com.ibm.util.merge.persistence;

import com.ibm.util.merge.template.Template;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractPersistence {
	private final HashMap<String, String> packages = new HashMap<String,String>();
	private final String defaultPackage;
	
	public AbstractPersistence(String defaultPackageName) {
		this.defaultPackage = defaultPackageName;
	}
	
	public void addCollection(String collectionName, String packageName) {
		packages.putIfAbsent(collectionName, packageName);
	}
	
	public String getPackage(String collectionName) {
		if (packages.containsKey(collectionName)) {
			return packages.get(collectionName);
		} else
			return this.defaultPackage;
	}
		
	public void saveTemplate(Template template) {
		String packageName = this.getPackage(template.getCollection());
		this.saveTemplate(template, packageName);
	}

	public abstract void saveTemplate(Template template, String packageName);
	public abstract List<Template> loadAllTemplates();
	public abstract void deleteTemplate(Template template);
}
