/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.renderkit.html;

import java.io.StringWriter;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlMessage;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.myfaces.test.utils.HtmlCheckAttributesUtil;
import org.apache.myfaces.test.utils.HtmlRenderedAttr;
import org.apache.shale.test.base.AbstractJsfTestCase;
import org.apache.shale.test.mock.MockRenderKitFactory;
import org.apache.shale.test.mock.MockResponseWriter;

public class HtmlMessageRendererTest extends AbstractJsfTestCase
{
    private static final String ERROR_CLASS = "errorClass";
    private static final String WARN_CLASS = "warnClass";
    private static final String INFO_CLASS = "infoClass";
    
    private MockResponseWriter writer;
    private HtmlMessage message;
    private HtmlForm form;
    private HtmlInputText inputText;

    public HtmlMessageRendererTest(String name)
    {
        super(name);
    }
    
    public static Test suite() {
        return new TestSuite(HtmlMessageRendererTest.class);
    }

    public void setUp() throws Exception
    {
        super.setUp();

        writer = new MockResponseWriter(new StringWriter(), null, null);
        message = new HtmlMessage();
        form = new HtmlForm();
        inputText = new HtmlInputText();
        
        facesContext.setResponseWriter(writer);

        facesContext.getViewRoot().setRenderKitId(MockRenderKitFactory.HTML_BASIC_RENDER_KIT);
        facesContext.getRenderKit().addRenderer(
                message.getFamily(),
                message.getRendererType(),
                new HtmlMessageRenderer());
        facesContext.getRenderKit().addRenderer(
                inputText.getFamily(),
                inputText.getRendererType(),
                new HtmlTextRenderer());
        facesContext.getRenderKit().addRenderer(
                form.getFamily(),
                form.getRendererType(),
                new HtmlFormRenderer());        
        
        inputText.setParent(form);
        inputText.setId("myInputId");
        
        message.setErrorClass(ERROR_CLASS);
        message.setWarnClass(WARN_CLASS);
        message.setInfoClass(INFO_CLASS);
        message.setParent(form);
        
        form.getChildren().add(inputText);
        form.getChildren().add(message);
        
        facesContext.addMessage(inputText.getClientId(facesContext), 
                new FacesMessage("Validation message here."));
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }    
    
    public void testHtmlPropertyPassTru() throws Exception
    {
        HtmlRenderedAttr[] attrs = {
            //_UniversalProperties
            new HtmlRenderedAttr("dir"), 
            new HtmlRenderedAttr("lang"), 
            new HtmlRenderedAttr("title"),
            //_EventProperties
            new HtmlRenderedAttr("onclick"), 
            new HtmlRenderedAttr("ondblclick"), 
            new HtmlRenderedAttr("onkeydown"), 
            new HtmlRenderedAttr("onkeypress"),
            new HtmlRenderedAttr("onkeyup"), 
            new HtmlRenderedAttr("onmousedown"), 
            new HtmlRenderedAttr("onmousemove"), 
            new HtmlRenderedAttr("onmouseout"),
            new HtmlRenderedAttr("onmouseover"), 
            new HtmlRenderedAttr("onmouseup"),
            //_StyleProperties
            new HtmlRenderedAttr("style"), 
            new HtmlRenderedAttr("styleClass", "styleClass", "class=\"infoClass\""),
        };
        
        facesContext.addMessage("test1", new FacesMessage(FacesMessage.SEVERITY_WARN, "warnSumary", "detailWarnSummary"));
        message.setStyle("left: 48px; top: 432px; position: absolute");
        message.setFor("myInputId");
        
        MockResponseWriter writer = (MockResponseWriter)facesContext.getResponseWriter();
        HtmlCheckAttributesUtil.checkRenderedAttributes(
                message, facesContext, writer, attrs);
        if(HtmlCheckAttributesUtil.hasFailedAttrRender(attrs)) {
            fail(HtmlCheckAttributesUtil.constructErrorMessage(attrs, writer.getWriter().toString()));
        }
    }
    
    public void testHtmlPropertyPassTruNotRendered() throws Exception
    {
        HtmlRenderedAttr[] attrs = HtmlCheckAttributesUtil.generateAttrsNotRenderedForReadOnly();
        
        facesContext.addMessage("test1", new FacesMessage(FacesMessage.SEVERITY_WARN, "warnSumary", "detailWarnSummary"));
        message.setStyle("left: 48px; top: 432px; position: absolute");
        message.setFor("myInputId");
        
        MockResponseWriter writer = (MockResponseWriter)facesContext.getResponseWriter();
        HtmlCheckAttributesUtil.checkRenderedAttributes(
                message, facesContext, writer, attrs);
        if(HtmlCheckAttributesUtil.hasFailedAttrRender(attrs)) {
            fail(HtmlCheckAttributesUtil.constructErrorMessage(attrs, writer.getWriter().toString()));
        }
    }
}