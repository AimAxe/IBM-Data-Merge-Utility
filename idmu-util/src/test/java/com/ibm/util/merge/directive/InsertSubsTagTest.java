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

import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.TestUtils;
import com.ibm.util.merge.directive.provider.ProviderTag;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.util.merge.template.Template;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class InsertSubsTagTest extends InsertSubsTest {
    private String subTemplate = "{\"collection\":\"root\",\"name\":\"sub\",\"content\":\"Foo Is: {Foo}, \"}";
    private String masterTemplate = "{\"collection\":\"root\",\"name\":\"master\",\"content\":\"Test \\u003ctkBookmark name\\u003d\\\"sub\\\" collection\\u003d\\\"root\\\"/\\u003e\"}";
    private String masterOutput = "Test Foo Is: SomeValue1, Foo Is: SomeValue2, Foo Is: SomeValue3, <tkBookmark name=\"sub\" collection=\"root\"/>";
    private ProviderTag myProvider;
    private InsertSubsTag myDirective;
    private JsonProxy jsonProxy;
    private MergeContext rtc;

    @Before
    public void setUp() throws Exception {
        rtc = TestUtils.createDefaultRuntimeContext();
        jsonProxy = new DefaultJsonProxy();
        directive = new InsertSubsTag();
        myDirective = (InsertSubsTag) directive;
        myProvider = (ProviderTag) myDirective.getProvider();
        TemplateFactory tf = rtc.getTemplateFactory();
        tf.reset();
        Template template2 = jsonProxy.fromJSON(subTemplate, Template.class);
        tf.cache(template2);
        Template template1 = jsonProxy.fromJSON(masterTemplate, Template.class);
        tf.cache(template1);
        template = tf.getMergableTemplate("root.master.", "", new HashMap<String,String>());
        template.addDirective(myDirective);
        myProvider.setTag("Foo");
        myProvider.setList(true);
        myProvider.setCondition(ProviderTag.CONDITION_EXISTS);
        template.addReplace("Foo", "SomeValue1,SomeValue2,SomeValue3");
    }

    @Test
    public void testExecuteDirectiveExistsList() throws MergeException {
        directive.executeDirective(rtc);
        assertEquals(masterOutput, template.getContent());
    }
}
