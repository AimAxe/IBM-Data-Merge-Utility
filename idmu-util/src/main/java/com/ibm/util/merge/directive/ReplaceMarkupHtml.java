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

import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.directive.provider.ProviderHtml;

public class ReplaceMarkupHtml extends AbstractDirective {
	protected String pattern;
	protected String fromKey;
	protected String toKey;

	/**
	 * Simple Constructor
	 */
	public ReplaceMarkupHtml() {
		super();
		this.setDescription("Add Replace from HTML Markup");
		setType(Directives.TYPE_HTML_REPLACE_MARKUP);
		setProvider(new ProviderHtml());
	}
	
	public ReplaceMarkupHtml asNew() {
		ReplaceMarkupHtml to = new ReplaceMarkupHtml();
		to.copyFieldsFrom((AbstractDirective)this);		
		to.setPattern(	this.getPattern());
		to.setFromKey(	this.getFromKey());
		to.setToKey(	this.getToKey());
		return to;
	}

	/**
	 * Add the replace value
	 * @throws MergeException
	 * @param tf
	 * @param rtc
	 */
	@Override
	public void executeDirective(MergeContext rtc) throws MergeException {
		getProvider().getData(rtc);
		// TODO HTML Markup - Execute
		// for each match, get from/to and call this.owner.addReplace()
	}

	public String getToKey() {
		return toKey;
	}

	public String getFromKey() {
		return fromKey;
	}

	public String getPattern() {
		return pattern;
	}

	public void setToKey(String toKey) {
		this.toKey = toKey;
	}

	public void setFromKey(String fromKey) {
		this.fromKey = fromKey;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

}
