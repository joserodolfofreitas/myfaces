/*
 *  Copyright 2008 werpu.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.apache.myfaces.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;

import org.apache.myfaces.context.servlet.FacesContextImpl;
import org.apache.myfaces.test.base.AbstractJsfTestCase;
import org.apache.myfaces.test.mock.MockResponseWriter;

/**
 * Testcase for the response switching
 *
 * @author Werner Punz(latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class ResponseWrapperSwitchTest extends AbstractJsfTestCase {

    public ResponseWrapperSwitchTest() {
        super("ResponseWrapperSwitchTest");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FactoryFinder.setFactory (FactoryFinder.EXCEPTION_HANDLER_FACTORY,
        "org.apache.myfaces.context.ExceptionHandlerFactoryImpl");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * we define our own response class to be able to test the output suppression!
     */
    class NewStreamingMockResponse extends ServletResponseWrapper {

        PrintWriter _writer;
        ServletOutputStream _strm;
        ServletResponse _response;

        public NewStreamingMockResponse(ServletResponse response, ServletOutputStream strm, PrintWriter writer) {
            super(response);
            _strm = strm;
            _writer = writer;
            _response = response;
        }

        @Override
        public ServletResponse getResponse() {
            return _response;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return _strm;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return _writer;
        }
    }

    /**
     * we need to define our own mockup for the output stream
     * so that we can simulate a servlet one
     */
    class ServletOutputStreamMock extends ServletOutputStream {

        private OutputStream _bos = null;

        ServletOutputStreamMock(OutputStream ostr) {
            _bos = ostr;
        }

        public void write(byte[] arg0) throws IOException {
            _bos.write(arg0);
        }

        public void flush() throws IOException {
            _bos.flush();
        }

        public String toString() {
            return _bos.toString();
        }

        public void close() throws IOException {
            _bos.close();
        }

        /**
         * @return the _bos
         */
        public OutputStream getBos() {
            return _bos;
        }

        /**
         * @param bos the _bos to set
         */
        public void setBos(OutputStream bos) {
            this._bos = bos;
        }

        @Override
        public void write(int arg0) throws IOException {
            _bos.write(arg0);
        }
    }

    /**
     * testing the off switch for the
     * response
     */
    public void testSwitchOnWriter() {

        ByteArrayOutputStream ostr = new ByteArrayOutputStream();
        ByteArrayOutputStream ostrWriter = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(ostrWriter);
        ServletOutputStreamMock sOstr = new ServletOutputStreamMock(ostr);
        NewStreamingMockResponse resp = new NewStreamingMockResponse(response, sOstr, writer);

        FacesContext context = new FacesContextImpl(servletContext, request, resp);


        ResponseWriter responseWriter = context.getResponseWriter();
        if (responseWriter == null) {
            try {
                responseWriter = new MockResponseWriter(((ServletResponse) context.getExternalContext().getResponse()).getWriter(), null, null);
            } catch (IOException ex) {
                super.fail(ex.getMessage());
            }
            context.setResponseWriter(responseWriter);
        }



        assertTrue("responsewriter not null", responseWriter != null);

        try {
            responseWriter.append("hello world");
            responseWriter.flush();
            responseWriter.close();

        } catch (IOException ex) {
            super.fail(ex.getMessage());
        }

        assertTrue(ostrWriter.toString().trim().equals("hello world"));

    }

    /**
     * Test switch off on the writer api
     * 
     * FIXME: enableResponseWriting no longer exists.
     *
    public void testSwitchOffWriter() {
        ByteArrayOutputStream ostr = new ByteArrayOutputStream();
        ByteArrayOutputStream ostrWriter = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(ostrWriter);
        ServletOutputStreamMock sOstr = new ServletOutputStreamMock(ostr);
        NewStreamingMockResponse resp = new NewStreamingMockResponse(response, sOstr, writer);

        FacesContext context = new FacesContextImpl(servletContext, request, resp);


        ResponseWriter responseWriter = context.getResponseWriter();
        if (responseWriter == null) {
            try {
                responseWriter = new MockResponseWriter(((ServletResponse) context.getExternalContext().getResponse()).getWriter(), null, null);
            } catch (IOException ex) {
                super.fail(ex.getMessage());
            }
            context.setResponseWriter(responseWriter);
        }



        assertTrue("responsewriter not null", responseWriter != null);
        context.getPartialViewContext().enableResponseWriting(false);

        try {
            responseWriter.append("hello world");
            responseWriter.flush();
            responseWriter.close();

        } catch (IOException ex) {
            super.fail(ex.getMessage());
        }


        assertTrue(ostrWriter.toString().trim().equals(""));


    }*/

    /**
     * 
     * FIXME: enableResponseWriting no longer exists.
     * 
     * test switch off on the stream api
     * if this works then the stream switch
     * shoud work on the facesContext should work as well!
     * @throws java.io.IOException
     *
    public void testSwitchOffOstr() {
        ByteArrayOutputStream ostr = new ByteArrayOutputStream();
        ByteArrayOutputStream ostrWriter = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(ostrWriter);
        ServletOutputStreamMock sOstr = new ServletOutputStreamMock(ostr);
        NewStreamingMockResponse resp = new NewStreamingMockResponse(response, sOstr, writer);

        FacesContext context = new FacesContextImpl(servletContext, request, resp);
        context.getPartialViewContext().enableResponseWriting(false);
        try {
            OutputStream finalOstr = (OutputStream) ((ServletResponse) context.getExternalContext().getResponse()).getOutputStream();
            PrintWriter finalWriter = new PrintWriter(finalOstr);
           
            finalWriter.write("hello world");
            finalOstr.write('a');
            finalOstr.flush();
            finalOstr.close();

        } catch (IOException ex) {
            super.fail(ex.getMessage());
        }


        assertTrue(ostr.toString().trim().equals(""));


    }*/
}
