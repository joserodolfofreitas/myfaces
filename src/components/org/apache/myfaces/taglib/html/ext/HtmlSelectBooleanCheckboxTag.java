/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.myfaces.taglib.html.ext;

import org.apache.myfaces.component.UserRoleAware;
import org.apache.myfaces.component.html.ext.HtmlSelectBooleanCheckbox;
import org.apache.myfaces.taglib.html.HtmlSelectBooleanCheckboxTagBase;

import javax.faces.component.UIComponent;


/**
 * @author Martin Marinschek (latest modification by $Author: mmarinschek $)
 * @version $Revision: 170212 $ $Date: 2005-05-15 12:58:15 +0200 (Sun, 15 May 2005) $
 */
public class HtmlSelectBooleanCheckboxTag
        extends HtmlSelectBooleanCheckboxTagBase
{
    public String getComponentType()
    {
        return HtmlSelectBooleanCheckbox.COMPONENT_TYPE;
    }

    public String getRendererType()
    {
        return "org.apache.myfaces.Checkbox";
    }

    private String _enabledOnUserRole;
    private String _visibleOnUserRole;

    private String _displayValueOnly;
	private String _displayValueOnlyStyle;
	private String _displayValueOnlyStyleClass;

    public void release() {
        super.release();
        _enabledOnUserRole=null;
        _visibleOnUserRole=null;

        _displayValueOnly=null;
        _displayValueOnlyStyle=null;
        _displayValueOnlyStyleClass=null;
   }

    protected void setProperties(UIComponent component)
    {
        super.setProperties(component);
        setStringProperty(component, UserRoleAware.ENABLED_ON_USER_ROLE_ATTR, _enabledOnUserRole);
        setStringProperty(component, UserRoleAware.VISIBLE_ON_USER_ROLE_ATTR, _visibleOnUserRole);

        setBooleanProperty(component, "displayValueOnly", _displayValueOnly);
        setStringProperty(component, "displayValueOnlyStyle", _displayValueOnlyStyle);
        setStringProperty(component, "displayValueOnlyStyleClass", _displayValueOnlyStyleClass);
    }

    public void setEnabledOnUserRole(String enabledOnUserRole)
    {
        _enabledOnUserRole = enabledOnUserRole;
    }

    public void setVisibleOnUserRole(String visibleOnUserRole)
    {
        _visibleOnUserRole = visibleOnUserRole;
    }

    public void setDisplayValueOnly(String displayValueOnly)
    {
        _displayValueOnly = displayValueOnly;
    }

    public void setDisplayValueOnlyStyle(String displayValueOnlyStyle)
    {
        _displayValueOnlyStyle = displayValueOnlyStyle;
    }

    public void setDisplayValueOnlyStyleClass(String displayValueOnlyStyleClass)
    {
        _displayValueOnlyStyleClass = displayValueOnlyStyleClass;
    }
}
