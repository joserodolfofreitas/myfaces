/**
 * MyFaces - the free JSF implementation
 * Copyright (C) 2003  The MyFaces Team (http://myfaces.sourceforge.net)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package net.sourceforge.myfaces.config;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * DOCUMENT ME!
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class NavigationRuleConfig
    implements Config
{
    private String _fromTreeId = null;
    private List _navigationCaseConfigList = null;

    public String getFromTreeId()
    {
        return _fromTreeId;
    }

    public void setFromTreeId(String fromTreeId)
    {
        _fromTreeId = fromTreeId;
    }

    public void addNavigationCaseConfig(NavigationCaseConfig navigationCaseConfig)
    {
        if (_navigationCaseConfigList == null)
        {
            _navigationCaseConfigList = new ArrayList();
        }
        _navigationCaseConfigList.add(navigationCaseConfig);
    }

    public List getNavigationCaseConfigList()
    {
        return _navigationCaseConfigList == null
                ? Collections.EMPTY_LIST
                : _navigationCaseConfigList;
    }
}
