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

import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.ProviderTag;

public class InsertSubsTag extends InsertSubs implements Cloneable {

	/**
	 * Simple Constructor
	 */
	public InsertSubsTag() {
		super();
		this.setType(TYPE_TAG_INSERT);
		this.setProvider(new ProviderTag());
	}

	/** 
	 * Simple Clone constructor
	 * @see com.ibm.util.merge.directive.InsertSubs#clone()
	 */
	public InsertSubsTag clone(Template owner) throws CloneNotSupportedException {
		return (InsertSubsTag) super.clone();
	}
}