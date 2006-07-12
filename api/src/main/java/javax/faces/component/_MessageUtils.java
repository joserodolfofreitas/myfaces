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
package javax.faces.component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
class _MessageUtils
{
    private static final String DETAIL_SUFFIX = "_detail";

	static void addErrorMessage(FacesContext facesContext,
								UIComponent component,
								String messageId)
	{
		facesContext.addMessage(component.getClientId(facesContext),
								getMessage(facesContext,
										   facesContext.getViewRoot().getLocale(),
										   FacesMessage.SEVERITY_ERROR,
										   messageId,
										   null));
	}

	static void addErrorMessage(FacesContext facesContext,
								UIComponent component,
								String messageId, Object[] args)
	{
		facesContext.addMessage(component.getClientId(facesContext),
								getMessage(facesContext,
										   facesContext.getViewRoot().getLocale(),
										   FacesMessage.SEVERITY_ERROR,
										   messageId,
										   args));
	}

    static void addErrorMessage(FacesContext facesContext,
            UIComponent component, Throwable cause)
    {
        StringBuffer buf = new StringBuffer();

        while(cause != null && cause.getCause()!=cause)
        {
            if(buf.length()>0)
                buf.append(", ");
            
            buf.append(cause.getLocalizedMessage());

            cause = cause.getCause();
        }

        facesContext.addMessage(component.getClientId(facesContext),
                new FacesMessage(FacesMessage.SEVERITY_ERROR, buf.toString(), buf.toString()));
    }

    
    static FacesMessage getMessage(FacesContext facesContext,
                                   Locale locale,
                                   FacesMessage.Severity severity,
                                   String messageId,
                                   Object args[])
    {
        ResourceBundle appBundle;
        ResourceBundle defBundle;
        String summary;
        String detail;

        appBundle = getApplicationBundle(facesContext, locale);
        summary = getBundleString(appBundle, messageId);
        if (summary != null)
        {
            detail = getBundleString(appBundle, messageId + DETAIL_SUFFIX);
        }
        else
        {
            defBundle = getDefaultBundle(facesContext, locale);
            summary = getBundleString(defBundle, messageId);
            if (summary != null)
            {
                detail = getBundleString(defBundle, messageId + DETAIL_SUFFIX);
            }
            else
            {
                //Try to find detail alone
                detail = getBundleString(appBundle, messageId + DETAIL_SUFFIX);
                if (detail != null)
                {
                    summary = null;
                }
                else
                {
                    detail = getBundleString(defBundle, messageId + DETAIL_SUFFIX);
                    if (detail != null)
                    {
                        summary = null;
                    }
                    else
                    {
                        //Neither detail nor summary found
                        facesContext.getExternalContext().log("No message with id " + messageId + " found in any bundle");
                        return new FacesMessage(severity, messageId, null);
                    }
                }
            }
        }

        if (args != null && args.length > 0)
        {
            MessageFormat format;

            if (summary != null)
            {
                format = new MessageFormat(summary, locale);
                summary = format.format(args);
            }

            if (detail != null)
            {
                format = new MessageFormat(detail, locale);
                detail = format.format(args);
            }
        }

        return new FacesMessage(severity, summary, detail);
    }


    private static String getBundleString(ResourceBundle bundle, String key)
    {
        try
        {
            return bundle == null ? null : bundle.getString(key);
        }
        catch (MissingResourceException e)
        {
            return null;
        }
    }


    private static ResourceBundle getApplicationBundle(FacesContext facesContext, Locale locale)
    {
        String bundleName = facesContext.getApplication().getMessageBundle();
        if (bundleName != null)
        {
            return getBundle(facesContext, locale, bundleName);
        }
        else
        {
            return null;
        }
    }

    private static ResourceBundle getDefaultBundle(FacesContext facesContext,
                                                   Locale locale)
    {
        return getBundle(facesContext, locale, FacesMessage.FACES_MESSAGES);
    }

    private static ResourceBundle getBundle(FacesContext facesContext,
                                            Locale locale,
                                            String bundleName)
    {
        try
        {
            //First we try the JSF implementation class loader
            return ResourceBundle.getBundle(bundleName,
                                            locale,
                                            facesContext.getClass().getClassLoader());
        }
        catch (MissingResourceException ignore1)
        {
            try
            {
                //Next we try the JSF API class loader
                return ResourceBundle.getBundle(bundleName,
                                                locale,
                                                _MessageUtils.class.getClassLoader());
            }
            catch (MissingResourceException ignore2)
            {
                try
                {
                    //Last resort is the context class loader
                    return ResourceBundle.getBundle(bundleName,
                                                    locale,
                                                    Thread.currentThread().getContextClassLoader());
                }
                catch (MissingResourceException damned)
                {
                    facesContext.getExternalContext().log("resource bundle " + bundleName + " could not be found");
                    return null;
                }
            }
        }
    }

}
