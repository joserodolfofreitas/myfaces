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
package net.sourceforge.myfaces.context;

import net.sourceforge.myfaces.context.maphelp.*;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * JSF 1.0 PRD2, 6.1.1
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class ExternalContextImpl
    extends ExternalContext
{
    private ServletContext _servletContext;
    private ServletRequest _servletRequest;
    private ServletResponse _servletResponse;
    private Map _applicationMap;
    private Map _sessionMap;
    private Map _requestMap;
    private Map _requestParameterMap;
    private Map _requestParameterValuesMap;
    private Map _requestHeaderMap;
    private Map _requestHeaderValuesMap;
    private Map _requestCookieMap;
    private Map _initParameterMap;

    public ExternalContextImpl(ServletContext servletContext,
                               ServletRequest servletRequest,
                               ServletResponse servletResponse)
    {
        _servletContext = servletContext;
        _servletRequest = servletRequest;
        _servletResponse = servletResponse;
        _applicationMap = null;
        _sessionMap = null;
        _requestMap = null;
        _requestParameterMap = null;
        _requestParameterValuesMap = null;
        _requestHeaderMap = null;
        _requestHeaderValuesMap = null;
        _requestCookieMap = null;
        _initParameterMap = null;
    }

    public void release()
    {
        _servletContext = null;
        _servletRequest = null;
        _servletResponse = null;
        _applicationMap = null;
        _sessionMap = null;
        _requestMap = null;
        _requestParameterMap = null;
        _requestParameterValuesMap = null;
        _requestHeaderMap = null;
        _requestHeaderValuesMap = null;
        _requestCookieMap = null;
        _initParameterMap = null;
    }



    public Object getSession(boolean create)
    {
        if (_servletRequest instanceof HttpServletRequest)
        {
            return ((HttpServletRequest)_servletRequest).getSession(create);
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public Object getContext()
    {
        return _servletContext;
    }

    public Object getRequest()
    {
        return _servletRequest;
    }

    public Object getResponse()
    {
        return _servletResponse;
    }

    public Map getApplicationMap()
    {
        if (_applicationMap == null)
        {
            _applicationMap = new ApplicationMap(_servletContext);
        }
        return _applicationMap;
    }

    public Map getSessionMap()
    {
        if (_sessionMap == null)
        {
            HttpSession session = ((HttpSession)getSession(false));
            if (session != null)
            {
                _sessionMap = new SessionMap(session);
            }
        }
        return _sessionMap;
    }

    public Map getRequestMap()
    {
        if (_requestMap == null)
        {
            _requestMap = new RequestMap(_servletRequest);
        }
        return _requestMap;
    }

    public Map getRequestParameterMap()
    {
        if (_requestParameterMap == null)
        {
            _requestParameterMap = new RequestParameterMap(_servletRequest);
        }
        return _requestParameterMap;
    }

    public Map getRequestParameterValuesMap()
    {
        if (_requestParameterValuesMap == null)
        {
            _requestParameterValuesMap = new RequestParameterValuesMap(_servletRequest);
        }
        return _requestParameterValuesMap;
    }

    public Iterator getRequestParameterNames()
    {
        final Enumeration enum = _servletRequest.getParameterNames();
        Iterator it = new Iterator()
        {
            public boolean hasNext() {
                return enum.hasMoreElements();
            }

            public Object next() {
                return enum.nextElement();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

    public Map getRequestHeaderMap()
    {
        if (_requestHeaderMap == null)
        {
            _requestHeaderMap = new RequestHeaderMap((HttpServletRequest)_servletRequest);
        }
        return _requestHeaderMap;
    }

    public Map getRequestHeaderValuesMap()
    {
        if (_requestHeaderValuesMap == null)
        {
            _requestHeaderValuesMap = new RequestHeaderValuesMap((HttpServletRequest)_servletRequest);
        }
        return _requestHeaderValuesMap;
    }

    public Map getRequestCookieMap()
    {
        if (_requestCookieMap == null)
        {
            Cookie[] cookies = ((HttpServletRequest)_servletRequest).getCookies();
            _requestCookieMap = new HashMap(cookies.length);
            for (int i = 0; i < cookies.length; i++)
            {
                _requestCookieMap.put(cookies[i].getName(), cookies[i]);
            }
        }
        return _requestCookieMap;
    }

    public Locale getRequestLocale()
    {
        return _servletRequest.getLocale();
    }

    public String getRequestPathInfo()
    {
        return ((HttpServletRequest)_servletRequest).getPathInfo();
    }

    public String getRequestContextPath()
    {
        return ((HttpServletRequest)_servletRequest).getContextPath();
    }

    public Cookie[] getRequestCookies()
    {
        return ((HttpServletRequest)_servletRequest).getCookies();
    }

    public String getInitParameter(String s)
    {
        return _servletContext.getInitParameter(s);
    }

    public Map getInitParameterMap()
    {
        if (_initParameterMap == null)
         {
             _initParameterMap = new InitParameterMap(_servletContext);
         }
         return _initParameterMap;
    }

    public Set getResourcePaths(String s)
    {
        return _servletContext.getResourcePaths(s);
    }

    public InputStream getResourceAsStream(String s)
    {
        return _servletContext.getResourceAsStream(s);
    }

    public String encodeActionURL(String s)
    {
        return ((HttpServletResponse)_servletResponse).encodeURL(s);
    }

    public String encodeResourceURL(String s)
    {
        return ((HttpServletResponse)_servletResponse).encodeURL(s);
    }

    public String encodeURL(String s)
    {
        return ((HttpServletResponse)_servletResponse).encodeURL(s);
    }

    public String encodeNamespace(String s)
    {
        return s;
    }

    public void dispatchMessage(String requestURI) throws IOException, FacesException
    {
        RequestDispatcher requestDispatcher
            = _servletRequest.getRequestDispatcher(requestURI);
        try
        {
            requestDispatcher.forward(_servletRequest, _servletResponse);
        }
        catch (ServletException e)
        {
            throw new FacesException(e);
        }
    }
}
