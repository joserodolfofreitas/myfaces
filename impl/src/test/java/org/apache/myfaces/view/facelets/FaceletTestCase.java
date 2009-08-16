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

package org.apache.myfaces.view.facelets;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;

import junit.framework.TestCase;

import org.apache.myfaces.application.ApplicationFactoryImpl;
import org.apache.myfaces.application.ApplicationImpl;
import org.apache.myfaces.config.FacesConfigDispenser;
import org.apache.myfaces.config.FacesConfigUnmarshaller;
import org.apache.myfaces.config.RuntimeConfig;
import org.apache.myfaces.config.element.Renderer;
import org.apache.myfaces.config.impl.digester.DigesterFacesConfigDispenserImpl;
import org.apache.myfaces.config.impl.digester.DigesterFacesConfigUnmarshallerImpl;
import org.apache.myfaces.config.impl.digester.elements.FacesConfig;
import org.apache.myfaces.context.PartialViewContextFactoryImpl;
import org.apache.myfaces.renderkit.html.HtmlResponseStateManager;
import org.apache.myfaces.shared_impl.util.ClassUtils;
import org.apache.myfaces.shared_impl.util.StateUtils;
import org.apache.myfaces.shared_impl.util.serial.DefaultSerialFactory;
import org.apache.myfaces.view.facelets.compiler.Compiler;
import org.apache.myfaces.view.facelets.compiler.SAXCompiler;
import org.apache.myfaces.view.facelets.impl.DefaultFaceletFactory;
import org.apache.myfaces.view.facelets.impl.ResourceResolver;
import org.apache.myfaces.view.facelets.mock.MockHttpServletRequest;
import org.apache.myfaces.view.facelets.mock.MockHttpServletResponse;
import org.apache.myfaces.view.facelets.mock.MockServletContext;
import org.apache.myfaces.view.facelets.mock.MockViewDeclarationLanguageFactory;
import org.apache.myfaces.view.facelets.tag.jsf.TagHandlerDelegateFactoryImpl;
import org.apache.shale.test.el.MockExpressionFactory;
import org.apache.shale.test.mock.MockExternalContext;
import org.apache.shale.test.mock.MockFacesContext;
import org.apache.shale.test.mock.MockFacesContextFactory;
import org.apache.shale.test.mock.MockPropertyResolver;
import org.apache.shale.test.mock.MockRenderKit;
import org.apache.shale.test.mock.MockVariableResolver;
import org.apache.shale.test.mock.lifecycle.MockLifecycle;
import org.apache.shale.test.mock.lifecycle.MockLifecycleFactory;

public abstract class FaceletTestCase extends TestCase implements
        ResourceResolver
{
    private final String filePath = this.getDirectory();    
    protected MockServletContext servletContext;
    protected MockHttpServletRequest servletRequest;
    protected MockHttpServletResponse servletResponse;
    protected MockExternalContext externalContext;
    protected MockFacesContext facesContext;
    protected MockFacesContextFactory facesContextFactory;
    protected MockLifecycle lifecycle;
    protected MockLifecycleFactory lifecycleFactory;
    protected ApplicationImpl application;
    protected MockRenderKit renderKit;
    protected MockFaceletViewDeclarationLanguage vdl;
    
    protected FacesConfigDispenser<FacesConfig> dispenser = null;

    protected URI getContext()
    {
        try
        {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL url = cl.getResource(this.filePath);
            if (url == null)
            {
                throw new FileNotFoundException(cl.getResource("").getFile()
                        + this.filePath + " was not found");
            }
            else
            {
                return new URI(url.toString());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Initializing Context", e);
        }
    }

    protected URL getLocalFile(String name) throws FileNotFoundException
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(this.filePath + "/" + name);
        if (url == null)
        {
            throw new FileNotFoundException(cl.getResource("").getFile() + name
                    + " was not found");
        }
        return url;
    }

    private String getDirectory()
    {
        return this.getClass().getName().substring(0,
                this.getClass().getName().lastIndexOf('.')).replace('.', '/')
                + "/";
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        URI context = this.getContext();

        this.servletContext = new MockServletContext(context);
        this.servletRequest = new MockHttpServletRequest(this.servletContext,
                context);
        this.servletResponse = new MockHttpServletResponse();

        externalContext = new MockExternalContext(servletContext,
                servletRequest, servletResponse);

        // Set up JSF API Objects
        FactoryFinder.releaseFactories();
        
        setupRuntimeConfigAndFactories();

        lifecycleFactory = (MockLifecycleFactory) FactoryFinder
                .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        lifecycle = (MockLifecycle) lifecycleFactory
                .getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
        facesContextFactory = (MockFacesContextFactory) FactoryFinder
                .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        facesContext = (MockFacesContext) facesContextFactory.getFacesContext(
                servletContext, servletRequest, servletResponse, lifecycle);
        externalContext = (MockExternalContext) facesContext
                .getExternalContext();
        ApplicationFactory applicationFactory = (ApplicationFactory) FactoryFinder
                .getFactory(FactoryFinder.APPLICATION_FACTORY);
        application = (ApplicationImpl) applicationFactory.getApplication();
        facesContext.setApplication(application);
        StateUtils.initSecret(servletContext);
        externalContext.getApplicationMap().put(StateUtils.SERIAL_FACTORY,
                new DefaultSerialFactory());

        RenderKitFactory renderKitFactory = (RenderKitFactory) FactoryFinder
                .getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        renderKit = new MockRenderKit()
        {
            ResponseStateManager rsm = new HtmlResponseStateManager();

            @Override
            public ResponseStateManager getResponseStateManager()
            {
                return rsm;
            }
        };
        renderKitFactory.addRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT,
                renderKit);

        setupComponents();
        setupConvertersAndValidators();
        setupRenderers();

        //Compiler c = new SAXCompiler();
        //c.setTrimmingWhitespace(true);
        //FaceletFactory factory = new DefaultFaceletFactory(c, this);
        //FaceletFactory.setInstance(factory);
        
        facesContext.setViewRoot(facesContext.getApplication().getViewHandler()
                .createView(facesContext, "/test"));
        
        vdl = (MockFaceletViewDeclarationLanguage) application.getViewHandler().
            getViewDeclarationLanguage(facesContext,"/test");

        ResponseWriter rw = facesContext.getRenderKit().createResponseWriter(
                new StringWriter(), null, null);
        facesContext.setResponseWriter(rw);
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        this.servletContext = null;
        servletRequest = null;
        servletResponse = null;
        externalContext = null;
        facesContext = null;
        facesContextFactory = null;
        lifecycle = null;
        lifecycleFactory = null;
        application = null;
        renderKit = null;
        vdl = null;
    }
    
    protected void setupRuntimeConfigAndFactories()
    {
        RuntimeConfig.getCurrentInstance(externalContext).setPropertyResolver(
                new MockPropertyResolver());
        RuntimeConfig.getCurrentInstance(externalContext).setVariableResolver(
                new MockVariableResolver());
        RuntimeConfig.getCurrentInstance(externalContext).setExpressionFactory(
                new MockExpressionFactory());
        //To make work ValueExpressions

        FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY,
                ApplicationFactoryImpl.class.getName());
        FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY,
                "org.apache.shale.test.mock.MockFacesContextFactory");
        FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY,
                "org.apache.shale.test.mock.lifecycle.MockLifecycleFactory");
        FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY,
                "org.apache.shale.test.mock.MockRenderKitFactory");
        FactoryFinder.setFactory(
                FactoryFinder.VIEW_DECLARATION_LANGUAGE_FACTORY,
                MockViewDeclarationLanguageFactory.class.getName());
        FactoryFinder.setFactory(FactoryFinder.TAG_HANDLER_DELEGATE_FACTORY,
                TagHandlerDelegateFactoryImpl.class.getName());
        FactoryFinder.setFactory(FactoryFinder.PARTIAL_VIEW_CONTEXT_FACTORY,
                PartialViewContextFactoryImpl.class.getName());
    }
    
    protected void loadStandardFacesConfig() throws Exception
    {
        if (dispenser == null)
        {
            InputStream stream = ClassUtils
            .getResourceAsStream("META-INF/standard-faces-config.xml");
            FacesConfigUnmarshaller<? extends FacesConfig> unmarshaller = new DigesterFacesConfigUnmarshallerImpl(
                    externalContext);
            dispenser = new DigesterFacesConfigDispenserImpl();
            dispenser.feed(unmarshaller.getFacesConfig(stream,
                    "META-INF/standard-faces-config.xml"));
        }
    }
    
    /**
     * Override this methods and add just what it is necessary
     * reduce execution time.
     */
    protected void setupComponents() throws Exception
    {
        loadStandardFacesConfig();
        for (String componentType : dispenser.getComponentTypes())
        {
            application.addComponent(componentType, dispenser
                    .getComponentClass(componentType));
        }
    }
    
    /**
     * Override this methods and add just what it is necessary
     * reduce execution time.
     */
    protected void setupRenderers() throws Exception
    {
        loadStandardFacesConfig();
        for (Renderer element : dispenser
                .getRenderers(RenderKitFactory.HTML_BASIC_RENDER_KIT))
        {
            javax.faces.render.Renderer renderer;
            try
            {
                renderer = (javax.faces.render.Renderer) ClassUtils
                        .newInstance(element.getRendererClass());
            }
            catch (Throwable e)
            {
                // ignore the failure so that the render kit is configured
                continue;
            }

            renderKit.addRenderer(element.getComponentFamily(), element
                    .getRendererType(), renderer);
        }        
    }
    
    /**
     * Override this methods and add just what it is necessary
     * reduce execution time.
     */
    protected void setupConvertersAndValidators() throws Exception
    {
        loadStandardFacesConfig();
        for (String validatorId : dispenser.getValidatorIds())
        {
            application.addValidator(validatorId, dispenser
                    .getValidatorClass(validatorId));
        }
        for (String converterId : dispenser.getConverterIds())
        {
            application.addConverter(converterId, dispenser
                    .getConverterClassById(converterId));
        }
        for (String validatorId : dispenser.getValidatorIds())
        {
            application.addValidator(validatorId, dispenser
                    .getValidatorClass(validatorId));
        }
    }

    public URL resolveUrl(String path)
    {
        try
        {
            return new URL(this.getContext().toURL(), path.substring(1));
        }
        catch (Exception e)
        {
            throw new FacesException(e);
        }
    }

}