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
package net.sourceforge.myfaces.util.bundle;

import net.sourceforge.myfaces.util.logging.LogUtil;

import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * DOCUMENT ME!
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class BundleUtils
{
    private BundleUtils() {}

    public static ResourceBundle findResourceBundle(String baseName, Locale inLocale)
    {
        try
        {
            return ResourceBundle.getBundle(baseName, inLocale);
        }
        catch (MissingResourceException e)
        {
            LogUtil.getLogger().severe("Resource bundle '" + baseName + "' could not be found.");
            return null;
        }
    }

    public static String getString(String bundleName, String key, Locale inLocale)
    {
        ResourceBundle bundle = findResourceBundle(bundleName, inLocale);
        if (bundle != null)
        {
            try
            {
                return bundle.getString(key);
            }
            catch (MissingResourceException e)
            {
                LogUtil.getLogger().warning("Resource string '" + key + "' in bundle '" + bundleName + "' could not be found.");
                return key;
            }
        }
        else
        {
            return key;
        }
    }

}
