/**
 * Copyright 2004 by Irian Marinschek & Spiegl Software OEG
 */
package org.apache.myfaces.config.impl.digester.elements;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

/**
 * @author Martin Marinschek
 * @version $Revision$ $Date$
 * 
     The "attribute" element represents a named, typed, value associated with
     the parent UIComponent via the generic attributes mechanism.

     Attribute names must be unique within the scope of the parent (or related)
     component.

     <!ELEMENT attribute       (description*, display-name*, icon*, attribute-name, attribute-class, default-value?, suggested-value?, attribute-extension*)>
 
 *          <p/>
 *          $Log$
 *          Revision 1.1  2005/03/04 00:28:45  mmarinschek
 *          Changes in configuration due to missing Attribute/Property classes for the converter; not building in the functionality yet except for part of the converter properties
 *
 * 
 */
public class Attribute
{
    private List _description;
    private List _displayName;
    private List _icon;
    private String _attributeName;
    private String _attributeClass;
    private String _defaultValue;
    private String _suggestedValue;
    private List _attributeExtension;


    public void addDescription(String value)
    {
        if(_description == null)
            _description = new ArrayList();

        _description.add(value);
    }

    public Iterator getDescriptions()
    {
        if(_description==null)
            return Collections.EMPTY_LIST.iterator();

        return _description.iterator();
    }

    public void addDisplayName(String value)
    {
        if(_displayName == null)
            _displayName = new ArrayList();

        _displayName.add(value);
    }

    public Iterator getDisplayNames()
    {
        if(_displayName==null)
            return Collections.EMPTY_LIST.iterator();

        return _displayName.iterator();
    }

    public void addIcon(String value)
    {
        if(_icon == null)
            _icon = new ArrayList();

        _icon.add(value);
    }

    public Iterator getIcons()
    {
        if(_icon==null)
            return Collections.EMPTY_LIST.iterator();

        return _icon.iterator();
    }

    public void setAttributeName(String attributeName)
    {
        _attributeName = attributeName;
    }

    public String getAttributeName()
    {
        return _attributeName;
    }

    public void setAttributeClass(String attributeClass)
    {
        _attributeClass = attributeClass;
    }

    public String getAttributeClass()
    {
        return _attributeClass;
    }

    public void setDefaultValue(String defaultValue)
    {
        _defaultValue = defaultValue;
    }

    public String getDefaultValue()
    {
        return _defaultValue;
    }

    public void setSuggestedValue(String suggestedValue)
    {
        _suggestedValue = suggestedValue;
    }

    public String getSuggestedValue()
    {
        return _suggestedValue;
    }

    public void addAttributeExtension(String attributeExtension)
    {
        if(_attributeExtension == null)
            _attributeExtension = new ArrayList();

        _attributeExtension.add(attributeExtension);
    }

    public Iterator getAttributeExtensions()
    {
        if(_attributeExtension==null)
            return Collections.EMPTY_LIST.iterator();

        return _attributeExtension.iterator();
    }
}
