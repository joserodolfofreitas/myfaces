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
package net.sourceforge.myfaces.renderkit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * RenderKitFactory implementation as defined in Spec. JSF.7.3
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 * $Log$
 * Revision 1.14  2004/07/05 12:52:41  manolito
 * Apache License
 *
 * Revision 1.13  2004/06/23 15:48:01  manolito
 * Map members now non-static
 *
 * Revision 1.12  2004/05/18 07:13:32  manolito
 * X-checked against specs: no more synchronization needed, allow replacement of renderKit, no excepetion on unknown id
 *
 */
public class RenderKitFactoryImpl
    extends RenderKitFactory
{
    private static final Log log = LogFactory.getLog(RenderKitFactoryImpl.class);

    private Map _renderkits = new HashMap();

    public RenderKitFactoryImpl()
    {
    }


    public void addRenderKit(String renderKitId, RenderKit renderKit)
    {
        if (renderKitId == null) throw new NullPointerException("renderKitId");
        if (renderKit == null) throw new NullPointerException("renderKit");
        if (log.isInfoEnabled())
        {
            if (_renderkits.containsKey(renderKitId))
            {
                log.info("RenderKit with renderKitId '" + renderKitId + "' was replaced.");
            }
        }
        _renderkits.put(renderKitId, renderKit);
    }


    public RenderKit getRenderKit(FacesContext context, String renderKitId)
            throws FacesException
    {
        if (renderKitId == null) throw new NullPointerException("renderKitId");
        RenderKit renderkit = (RenderKit)_renderkits.get(renderKitId);
        if (renderkit == null)
        {
            //throw new IllegalArgumentException("Unknown RenderKit '" + renderKitId + "'.");
            //JSF Spec API Doc says:
            // "If there is no registered RenderKit for the specified identifier, return null"
            // vs "IllegalArgumentException - if no RenderKit instance can be returned for the specified identifier"
            //First sentence is more precise, so we just log a warning
            log.warn("Unknown RenderKit '" + renderKitId + "'.");
        }
        return renderkit;
    }


    public Iterator getRenderKitIds()
    {
        return _renderkits.keySet().iterator();
    }
}
