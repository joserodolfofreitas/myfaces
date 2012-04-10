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
package javax.faces.component.html;

import javax.faces.component.UIForm;

/**
 * Renders an HTML form element.
 * 
 * See Javadoc of <a href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/api/index.html">JSF Specification</a>
 *
 * @JSFComponent
 *   name = "h:form"
 *   class = "javax.faces.component.html.HtmlForm"
 *   tagClass = "org.apache.myfaces.taglib.html.HtmlFormTag"
 *   template = "true"
 *   desc = "h:form"
 *   
 * @author Thomas Spiegl (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
abstract class _HtmlForm extends UIForm implements _EventProperties,
    _UniversalProperties, _StyleProperties
{

    public static final String COMPONENT_TYPE = "javax.faces.HtmlForm";
    private static final String DEFAULT_RENDERER_TYPE = "javax.faces.Form";

    /**
     * HTML: Provides a comma-separated list of content types that the 
     * server processing this form can handle.
     * 
     * @JSFProperty
     */
    public abstract String getAccept();

    /**
     * HTML: The list of character encodings accepted by the server for this
     * form.
     * 
     * @JSFProperty
     */
    public abstract String getAcceptcharset();

    /**
     * HTML: The content type used to submit this form to the server.
     * 
     * @JSFProperty
     * defaultValue = "application/x-www-form-urlencoded"
     */
    public abstract String getEnctype();

    /**
     * HTML: Script to be invoked when this form is reset.
     * 
     * @JSFProperty
     */
    public abstract String getOnreset();

    /**
     * HTML: Script to be invoked when this form is submitted.
     * 
     * @JSFProperty
     */
    public abstract String getOnsubmit();
    
    /**
     * HTML: Names the frame that should display content generated by invoking this action. 
     * 
     * @JSFProperty
     */
    public abstract String getTarget();
    
}
