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
package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.*;

import com.ibm.util.merge.directive.*;
import com.ibm.util.merge.directive.provider.*;

public class TemplateTest {
	private Template template;
	private String templateString = "Testing {Foo} Template <tkBookmark name=\"BKM1\"/> and {empty} <tkBookmark name=\"BKM2\"/> save to {folder}";
	private String mergeOutput = "Testing Test Foo Value Template  and NOT  save to /tmp/output/";
	private String testInsert1 = "Testing {Foo} Template Test<tkBookmark name=\"BKM1\"/> and {empty} <tkBookmark name=\"BKM2\"/> save to {folder}";
	private String testInsert2 = "Testing {Foo} Template Test<tkBookmark name=\"BKM1\"/> and {empty} Test<tkBookmark name=\"BKM2\"/> save to {folder}";
	private String replaceTest = "Testing Fixed Template <tkBookmark name=\"BKM1\"/> and {empty} <tkBookmark name=\"BKM2\"/> save to {folder}";
	private String replacePattern = "Testing {Foo} Template Gone and {empty} Gone save to {folder}";
	private String testJsonBase = 			"\"collection\":\"root\",\"columnValue\":\"none\",\"name\":\"default\",\"description\":\"\",\"outputFile\":\"\",\"content\":\"Testing {Foo} Template \\u003ctkBookmark name\\u003d\\\"BKM1\\\"/\\u003e and {empty} \\u003ctkBookmark name\\u003d\\\"BKM2\\\"/\\u003e save to {folder}\",\"directives\":[{\"from\":\"Foo\",\"to\":\"Test Foo Value\",\"sequence\":0,\"type\":1,\"softFail\":false,\"description\":\"Test Replace1\"}"; 
	private String testReplaceJson = 		"{" + testJsonBase + "]}";
	private String testRequireJson = 		"{" + testJsonBase + ",{\"tags\":[\"Foo\",\"empty\"],\"sequence\":1,\"type\":0,\"softFail\":false,\"description\":\"TestRequire\"}]}"; 
	private String testInsertSubsTagJson = 	"{" + testJsonBase + ",{\"collectionName\":\"\",\"collectionColumn\":\"\",\"notLast\":[\"empty\"],\"onlyLast\":[],\"sequence\":1,\"type\":2,\"softFail\":false,\"description\":\"TestInsertSubsTag\",\"provider\":{\"tag\":\"Foo\",\"condition\":0,\"list\":false,\"value\":\"\",\"type\":2}}]}"; 
	private String testInsertSubsCsvJson = 	"{" + testJsonBase + ",{\"collectionName\":\"\",\"collectionColumn\":\"\",\"notLast\":[\"empty\"],\"onlyLast\":[],\"sequence\":1,\"type\":21,\"softFail\":false,\"description\":\"TestInsertSubsCsv\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":3}}]}";
	private String testInsertSubsSqlJson = 	"{" + testJsonBase + ",{\"collectionName\":\"\",\"collectionColumn\":\"\",\"notLast\":[\"empty\"],\"onlyLast\":[],\"sequence\":1,\"type\":10,\"softFail\":false,\"description\":\"TestInsertSubsSql\",\"provider\":{\"source\":\"\",\"columns\":\"A,B,C,1,2,3,4,5,6\",\"from\":\"\",\"where\":\"\",\"type\":1}}]}"; 
	private String testInsertSubsHtmlJson = "{" + testJsonBase + ",{\"collectionName\":\"\",\"collectionColumn\":\"\",\"notLast\":[\"empty\"],\"onlyLast\":[],\"sequence\":1,\"type\":31,\"softFail\":false,\"description\":\"TestInsertSubsHtml\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":4}}]}"; 
	private String testReplaceRowCsvJson = 	"{" + testJsonBase + ",{\"sequence\":1,\"type\":22,\"softFail\":false,\"description\":\"TestReplaceRowCsv\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":3}}]}"; 
	private String testReplaceRowSqlJson = 	"{" + testJsonBase + ",{\"sequence\":1,\"type\":11,\"softFail\":false,\"description\":\"TestReplaceRowSql\",\"provider\":{\"source\":\"\",\"columns\":\"A,B,C,1,2,3,4,5,6\",\"from\":\"\",\"where\":\"\",\"type\":1}}]}"; 
	private String testReplaceRowHtmlJson = "{" + testJsonBase + ",{\"sequence\":1,\"type\":32,\"softFail\":false,\"description\":\"TestReplaceRowHtml\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":4}}]}"; 
	private String testReplaceColCsvJson = 	"{" + testJsonBase + ",{\"fromColumn\":\"Foo\",\"toColumn\":\"\",\"sequence\":1,\"type\":23,\"softFail\":false,\"description\":\"TestReplaceColCsv\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":3}}]}"; 
	private String testReplaceColSqlJson = 	"{" + testJsonBase + ",{\"fromColumn\":\"Foo\",\"toColumn\":\"\",\"sequence\":1,\"type\":12,\"softFail\":false,\"description\":\"TestReplaceColSql\",\"provider\":{\"source\":\"\",\"columns\":\"A,B,C,1,2,3,4,5,6\",\"from\":\"\",\"where\":\"\",\"type\":1}}]}"; 
	private String testReplaceColHtmlJson = "{" + testJsonBase + ",{\"fromColumn\":\"Foo\",\"toColumn\":\"\",\"sequence\":1,\"type\":33,\"softFail\":false,\"description\":\"TestReplaceColHtml\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":4}}]}"; 
	private String testMarkupHtmlJson = 	"{" + testJsonBase + ",{\"pattern\":\"TestPattern\",\"sequence\":1,\"type\":34,\"softFail\":false,\"description\":\"TestMarkupSubsHtml\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\",\"type\":4}}]}"; 
	
	@Before
	public void setUp() throws Exception {
		ReplaceValue directive = new ReplaceValue();
		directive.setFrom("Foo");
		directive.setTo("Test Foo Value");
		directive.setDescription("Test Replace1");
		template = new Template();
		template.setIdtemplate(22);
		template.addDirective(directive);
		template.addReplace("empty","NOT");
		template.addReplace("folder","/tmp/output/");
		template.setContent(templateString);
		template.setCollection("root");
		template.setName("default");
		template.setColumnValue("none");
	}

	@Test
	public void testWrap() {
		String test = Template.wrap("Test");
		assertEquals("{Test}", test);
	}

	@Test
	public void testClone() throws MergeException {
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("{Seed}", "Value");
		Template that = template.clone(replace);
		that.setColumnValue("");
		assertNotEquals(template, that);
		assertEquals(template.getCollection(), 			that.getCollection());
		assertEquals(template.getName(), 				that.getName());
		assertNotEquals(template.getColumnValue(), 		that.getColumnValue());
		assertEquals(template.getDescription(), 		that.getDescription());
		assertEquals(template.getContent(), 			that.getContent());
		assertNotEquals(template.getBookmarks(), 		that.getBookmarks());
		assertEquals(template.getBookmarks().size(), 	that.getBookmarks().size());
		assertNotEquals(template.getDirectives(),		that.getDirectives());
		assertEquals(template.getDirectives().size(), 	that.getDirectives().size());
		assertTrue(that.hasReplaceKey("{Seed}"));
		assertEquals("Value", that.getReplaceValue("{Seed}"));
		template.setContent("Foo");
		assertNotEquals(template.getContent(), that.getContent());
		assertTrue(that.getReplaceValues().containsKey(Template.TAG_OUTPUTFILE));
		assertTrue(that.getReplaceValues().containsKey(Template.TAG_STACK));
	}

	@Test
	public void testMerge() throws MergeException {
		String output = template.merge();
		assertEquals(mergeOutput, output);
	}

	@Test
	public void testInsertText() {
		assertEquals(templateString, template.getContent());
		assertEquals(2, template.getBookmarks().size());
		assertEquals(23, template.getBookmarks().get(0).getStart());
		assertEquals(61, template.getBookmarks().get(1).getStart());
		
		template.insertText("Test", template.getBookmarks().get(0));
		assertEquals(testInsert1, template.getContent());
		assertEquals(27, template.getBookmarks().get(0).getStart());
		assertEquals(65, template.getBookmarks().get(1).getStart());
		
		template.insertText("Test", template.getBookmarks().get(1));
		assertEquals(testInsert2, template.getContent());
		assertEquals(27, template.getBookmarks().get(0).getStart());
		assertEquals(69, template.getBookmarks().get(1).getStart());
	}

	@Test
	public void testReplaceThisPass() {
		try {
			template.replaceThis("{Foo}", "Fixed");
		} catch (MergeException e) {
			fail("ReplaceThis Returned Exception" + e.getError());
		}
		assertEquals(replaceTest, template.getContent());
	}

	@Test
	public void testReplaceThisFail()  {
		try {
			template.replaceThis("{Foo}", "Not {Foo} Embeded");
		} catch (MergeException e) {
			assertNotNull(e);
			return;
		}
		fail("Embeded Replace failed to throw exception");
	}

	@Test
	public void testReplaceAllThis() {
		template.replaceAllThis(Template.BOOKMARK_PATTERN , "Gone");
		assertEquals(replacePattern, template.getContent());
	}

	@Test
	public void testHasReplaceKey() {
		assertTrue(template.hasReplaceKey("{empty}"));
		assertFalse(template.hasReplaceKey("FooBar"));
	}

	@Test
	public void testHasReplaceValue() {
		assertTrue(template.hasReplaceValue("{empty}"));
		assertFalse(template.hasReplaceValue("FooBar"));
	}

	@Test
	public void testAddEmptyReplace() {
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("empty");
		tags.add("Bar");
		template.addEmptyReplace(tags);
		assertTrue(template.hasReplaceKey("{empty}"));
		assertTrue(template.hasReplaceKey("{Bar}"));
		assertFalse(template.hasReplaceValue("{empty}"));
		assertFalse(template.hasReplaceValue("{Bar}"));
	}

	@Test
	public void testReplaceProcess() {
		String test = "{Foo} - to {empty}";
		assertEquals("{Foo} - to NOT", template.replaceProcess(test));
	}

	@Test
	public void testGetReplaceValuePass() {
		try {
			String answer = template.getReplaceValue("{empty}");
			assertEquals("NOT", answer);
		} catch (MergeException e) {
			fail("GetReplace threw an exception!");
		}
	}

	@Test
	public void testGetReplaceValueFail() {
		try {
			assertEquals("NEVER", template.getReplaceValue("{Missing}"));
		} catch (MergeException e) {
			assertNotNull(e);
			return;
		}
		fail("GetReplace failed to throw an exception!");
	}

	@Test
	public void testSetContent() {
		assertEquals(templateString, template.getContent());
		assertEquals(2, template.getBookmarks().size());
	}

	@Test
	public void testGetOutputTypeTar() {
		assertEquals(ZipFactory.TYPE_GZIP, template.getOutputType());
	}

	@Test
	public void testGetOutputTypeZip() {
		template.addReplace("DragonOutputType", "zip");
		assertEquals(ZipFactory.TYPE_ZIP, template.getOutputType());		
	}

	@Test
	public void testIsEmptyFalse() {
		assertFalse(template.isEmpty());		
	}

	@Test
	public void testIsEmptyTrue() throws MergeException {
		template.setContent("   ");
		assertTrue(template.isEmpty());		
	}
	
	@Test
	public void testReplceAsJson() {
		String asJson = template.asJson(false);
		assertEquals(testReplaceJson, asJson);
	}
	
	@Test
	public void testReqireAsJson() {
		Require directive = new Require();
		directive.setDescription("TestRequire");
		directive.setTags("Foo,empty");
		template.addDirective(directive);

		String asJson = template.asJson(false);
		assertEquals(testRequireJson, asJson);
		assertNull(directive.getProvider());
	}
	
	@Test
	public void testInsertSubsTagAsJson() {
		InsertSubsTag directive = new InsertSubsTag();
		directive.setDescription("TestInsertSubsTag");
		directive.setNotLast("empty");
		ProviderTag providerTag = (ProviderTag) directive.getProvider();
		providerTag.setTag("Foo");
		template.addDirective(directive);
		
		String asJson = template.asJson(false);
		assertEquals(this.testInsertSubsTagJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderTag", directive.getProvider().getClass().getName());
	}
	
	@Test
	public void testInsertSubsCsvAsJson() {
		InsertSubsCsv directive = new InsertSubsCsv();
		directive.setDescription("TestInsertSubsCsv");
		directive.setNotLast("empty");
		ProviderCsv providerCsv = (ProviderCsv) directive.getProvider();
		providerCsv.setStaticData("A,B,C\n1,2,3\n4,5,6");
		template.addDirective(directive);
		
		String asJson = template.asJson(false);
		assertEquals(this.testInsertSubsCsvJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderCsv", directive.getProvider().getClass().getName());
	}
	
	@Test
	public void testReplaceRowCsvAsJason() {
		ReplaceRowCsv directive = new ReplaceRowCsv();
		directive.setDescription("TestReplaceRowCsv");
		ProviderCsv providerCsv = (ProviderCsv) directive.getProvider();
		providerCsv.setStaticData("A,B,C\n1,2,3\n4,5,6");
		template.addDirective(directive);
		
		String asJson = template.asJson(false);
		assertEquals(this.testReplaceRowCsvJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderCsv", directive.getProvider().getClass().getName());
	}
	
	@Test
	public void testReplaceColCsvAsJson() {
		ReplaceColCsv directive = new ReplaceColCsv();
		directive.setDescription("TestReplaceColCsv");
		directive.setFromColumn("Foo");
		ProviderCsv providerCsv = (ProviderCsv) directive.getProvider();
		providerCsv.setStaticData("A,B,C\n1,2,3\n4,5,6");
		template.addDirective(directive);
		
		String asJson = template.asJson(false);
		assertEquals(this.testReplaceColCsvJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderCsv", directive.getProvider().getClass().getName());
	}
	
	@Test
	public void testInsertSubsSqlAsJson() {
		InsertSubsSql directive = new InsertSubsSql();
		directive.setDescription("TestInsertSubsSql");
		directive.setNotLast("empty");
		ProviderSql providerSql = (ProviderSql) directive.getProvider();
		providerSql.setColumns("A,B,C,1,2,3,4,5,6");
		template.addDirective(directive);
		
		String asJson = template.asJson(false);
		assertEquals(this.testInsertSubsSqlJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderSql", directive.getProvider().getClass().getName());
	}
	
	@Test
	public void testReplaceRowSqlAsJson() {
		ReplaceRowSql directive = new ReplaceRowSql();
		directive.setDescription("TestReplaceRowSql");
		ProviderSql providerSql = (ProviderSql) directive.getProvider();
		providerSql.setColumns("A,B,C,1,2,3,4,5,6");
		template.addDirective(directive);
		
		String asJson = template.asJson(false);
		assertEquals(this.testReplaceRowSqlJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderSql", directive.getProvider().getClass().getName());
	}
	
	@Test
	public void testReplaceColSqlAsJson() {
		ReplaceColSql directive = new ReplaceColSql();
		directive.setDescription("TestReplaceColSql");
		directive.setFromColumn("Foo");
		ProviderSql providerSql = (ProviderSql) directive.getProvider();
		providerSql.setColumns("A,B,C,1,2,3,4,5,6");
		template.addDirective(directive);

		String asJson = template.asJson(false);
		assertEquals(this.testReplaceColSqlJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderSql", directive.getProvider().getClass().getName());
	}
	
	@Test
	public void testInsertSubsHtmlAsJson() {
		InsertSubsHtml directive = new InsertSubsHtml();
		directive.setDescription("TestInsertSubsHtml");
		directive.setNotLast("empty");
		ProviderHtml providerHtml = (ProviderHtml) directive.getProvider();
		providerHtml.setStaticData("A,B,C\n1,2,3\n4,5,6");
		template.addDirective(directive);

		String asJson = template.asJson(false);
		assertEquals(this.testInsertSubsHtmlJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderHtml", directive.getProvider().getClass().getName());
	}
	
	@Test
	public void testReplaceRowHtmlAsJson() {
		ReplaceRowHtml directive = new ReplaceRowHtml();
		directive.setDescription("TestReplaceRowHtml");
		ProviderHtml providerHtml = (ProviderHtml) directive.getProvider();
		providerHtml.setStaticData("A,B,C\n1,2,3\n4,5,6");
		template.addDirective(directive);

		String asJson = template.asJson(false);
		assertEquals(this.testReplaceRowHtmlJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderHtml", directive.getProvider().getClass().getName());
	}
	
	@Test
	public void testReplaceColHtmlAsJson() {
		ReplaceColHtml directive = new ReplaceColHtml();
		directive.setDescription("TestReplaceColHtml");
		directive.setFromColumn("Foo");
		ProviderHtml providerHtml = (ProviderHtml) directive.getProvider();
		providerHtml.setStaticData("A,B,C\n1,2,3\n4,5,6");
		template.addDirective(directive);

		String asJson = template.asJson(false);
		assertEquals(this.testReplaceColHtmlJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderHtml", directive.getProvider().getClass().getName());
	}
	
	@Test
	public void testReplaceMarkupHtmlAsJson() {
		ReplaceMarkupHtml directive = new ReplaceMarkupHtml();
		directive.setDescription("TestMarkupSubsHtml");
		directive.setPattern("TestPattern");
		ProviderHtml providerHtml = (ProviderHtml) directive.getProvider();
		providerHtml.setStaticData("A,B,C\n1,2,3\n4,5,6");
		template.addDirective(directive);

		String asJson = template.asJson(false);
		assertEquals(testMarkupHtmlJson, asJson);
		assertEquals("com.ibm.util.merge.directive.provider.ProviderHtml", directive.getProvider().getClass().getName());
	}

	@Test
	public void testAddDirective() {
		assertEquals(1, template.getDirectives().size());
		assertEquals(template, template.getDirectives().get(0).getTemplate());
		assertEquals(template.getIdtemplate(), template.getDirectives().get(0).getIdTemplate());
	}

	@Test
	public void testEquals() throws MergeException {
		Template that = template.clone(new HashMap<String,String>());
		assertEquals("root.default.none", template.getFullName());
		assertEquals(template, that);
	}

	@Test
	public void testNotEquals() throws MergeException {
		Template that = template.clone(new HashMap<String,String>());
		that.setColumnValue("");
		assertNotEquals(template, that);
	}

}
