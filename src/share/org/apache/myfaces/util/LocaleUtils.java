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
package org.apache.myfaces.util;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Anton Koinov (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class LocaleUtils
{
    private static final Log log = LogFactory.getLog(LocaleUtils.class);

    /** Utility class, do not instatiate */
    private LocaleUtils()
    {
        // utility class, do not instantiate
    }

    /**
     * Converts a locale string to <code>Locale</code> class. Accepts both
     * '_' and '-' as separators for locale components.
     *
     * @param localeString string representation of a locale
     * @return Locale instance, compatible with the string representation
     */
    public static Locale toLocale(String localeString)
    {
        if ((localeString == null) || (localeString.length() == 0))
        {
            log.error("Locale name null or empty, ignoring");
            return Locale.getDefault();
        }

        int separatorCountry = localeString.indexOf('_');
        char separator;
        if (separatorCountry >= 0) {
            separator = '_';
        }
        else
        {
            separatorCountry = localeString.indexOf('-');
            separator = '-';
        }

        String language, country, variant;
        if (separatorCountry < 0)
        {
            language = localeString;
            country = variant = "";
        }
        else
        {
            language = localeString.substring(0, separatorCountry);

            int separatorVariant = localeString.indexOf(separator, separatorCountry + 1);
            if (separatorVariant < 0)
            {
                country = localeString.substring(separatorCountry + 1);
                variant = "";
            }
            else
            {
                country = localeString.substring(separatorCountry + 1, separatorVariant);
                variant = localeString.substring(separatorVariant + 1);
            }
        }

        return new Locale(language, country, variant);
    }
}
