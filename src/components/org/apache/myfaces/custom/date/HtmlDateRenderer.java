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
package org.apache.myfaces.custom.date;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.apache.myfaces.component.UserRoleUtils;
import org.apache.myfaces.custom.date.HtmlInputDate.UserData;
import org.apache.myfaces.renderkit.RendererUtils;
import org.apache.myfaces.renderkit.html.HTML;
import org.apache.myfaces.renderkit.html.HtmlRenderer;
import org.apache.myfaces.renderkit.html.HtmlRendererUtils;
import org.apache.myfaces.util.MessageUtils;

/**
 * $Log$
 * Revision 1.10  2004/12/09 05:13:02  svieujot
 * Mark potential bugs where we use the backing bean's value, and do not check for submitted value
 *
 * Revision 1.9  2004/10/13 11:50:57  matze
 * renamed packages to org.apache
 *
 * Revision 1.8  2004/10/04 17:47:55  svieujot
 * Bugfix for bug 1039797 (Missing setter in HtmlInputDateTag), and add User Role support to InputDate.
 *
 * Revision 1.7  2004/07/30 13:09:04  svieujot
 * Render numbers as 2 digits
 *
 * Revision 1.6  2004/07/30 02:59:00  svieujot
 * Enable disabled attribute
 *
 * Revision 1.5  2004/07/26 02:00:05  svieujot
 * Change structure to keep the data entered by the user even if they can't be converted
 *
 * Revision 1.4  2004/07/21 20:34:13  svieujot
 * Add error handling
 *
 * Revision 1.3  2004/07/18 03:08:23  svieujot
 * inputDate : add a type="date|time|both" similar as f:convertDateTime
 *
 * Revision 1.2  2004/07/17 21:03:05  svieujot
 * Clean code
 *
 * Revision 1.1  2004/07/17 20:52:53  svieujot
 * First version of an x:inputDate component
 *
 * 
 * @author Sylvain Vieujot (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class HtmlDateRenderer extends HtmlRenderer {
	/**
	 * <p>The message identifier of the {@link FacesMessage} to be created if
	 * the creditcard check fails.</p>
	 */
	public static final String DATE_MESSAGE_ID = "org.apache.myfaces.Date.INVALID";	
	
    private static final String ID_DAY_POSTFIX = ".day";
    private static final String ID_MONTH_POSTFIX = ".month";
    private static final String ID_YEAR_POSTFIX = ".year";
    private static final String ID_HOURS_POSTFIX = ".hours";
    private static final String ID_MINUTES_POSTFIX = ".minutes";
    private static final String ID_SECONDS_POSTFIX = ".seconds";
    
    protected boolean isDisabled(FacesContext facesContext, UIComponent uiComponent) {
        if( !UserRoleUtils.isEnabledOnUserRole(uiComponent) ){
            return false;
        }else{
            if( uiComponent instanceof HtmlInputDate ){
                return ((HtmlInputDate)uiComponent).isDisabled();
            }else{
                return RendererUtils.getBooleanAttribute(uiComponent, HTML.DISABLED_ATTR, false);
            }
        }
    }
    
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        RendererUtils.checkParamValidity(facesContext, uiComponent, HtmlInputDate.class);

        HtmlInputDate inputDate = (HtmlInputDate) uiComponent;
        Locale currentLocale = facesContext.getViewRoot().getLocale();
        UserData userData = inputDate.getUserData(currentLocale); // TODO : Check if restores the submitted value
        String type = inputDate.getType();
        String clientId = uiComponent.getClientId(facesContext);

        boolean disabled = isDisabled(facesContext, uiComponent);

        ResponseWriter writer = facesContext.getResponseWriter();

        HtmlRendererUtils.writePrettyLineSeparator(facesContext);

        if( ! type.equals("time")){
	        encodeInputDay(uiComponent, writer, clientId, userData, disabled);
	        encodeInputMonth(uiComponent, writer, clientId, userData, currentLocale, disabled);
	        encodeInputYear(uiComponent, writer, clientId, userData, disabled);
        }
        if( type.equals("both") ){
            writer.write(" ");
        }
        if( ! type.equals("date")){
	        encodeInputHours(uiComponent, writer, clientId, userData, disabled);
	        writer.write(":");
	        encodeInputMinutes(uiComponent, writer, clientId, userData, disabled);
	        writer.write(":");
	        encodeInputSeconds(uiComponent, writer, clientId, userData, disabled);
        }
    }
    
    private static void encodeInputField(UIComponent uiComponent, ResponseWriter writer, String id, String value, int size, boolean disabled)  throws IOException {
        writer.startElement(HTML.INPUT_ELEM, uiComponent);
        HtmlRendererUtils.renderHTMLAttributes(writer, uiComponent, HTML.UNIVERSAL_ATTRIBUTES);
        HtmlRendererUtils.renderHTMLAttributes(writer, uiComponent, HTML.EVENT_HANDLER_ATTRIBUTES);
        HtmlRendererUtils.renderHTMLAttributes(writer, uiComponent, HTML.INPUT_ATTRIBUTES);

		if (disabled) {
		    writer.writeAttribute(HTML.DISABLED_ATTR, Boolean.TRUE, null);
		}

		writer.writeAttribute(HTML.ID_ATTR, id, null);
		writer.writeAttribute(HTML.NAME_ATTR, id, null);
		writer.writeAttribute(HTML.SIZE_ATTR, Integer.toString(size), null);
		writer.writeAttribute(HTML.MAXLENGTH_ATTR, Integer.toString(size), null);
		if (value != null) {
		    writer.writeAttribute(HTML.VALUE_ATTR, value, null);
		}
		writer.endElement(HTML.INPUT_ELEM);
    }
    
    private static void encodeInputDay(UIComponent uiComponent, ResponseWriter writer, String clientId, UserData userData, boolean disabled)
            throws IOException {
        encodeInputField(uiComponent, writer, clientId + ID_DAY_POSTFIX, userData.getDay(), 2, disabled);
    }

    private static void encodeInputMonth(UIComponent uiComponent, ResponseWriter writer, String clientId, UserData userData, Locale currentLocale,
            boolean disabled) throws IOException {
        writer.startElement(HTML.SELECT_ELEM, uiComponent);
        writer.writeAttribute(HTML.NAME_ATTR, clientId + ID_MONTH_POSTFIX, null);
        writer.writeAttribute(HTML.SIZE_ATTR, "1", null);
        HtmlRendererUtils.renderHTMLAttributes(writer, uiComponent, HTML.UNIVERSAL_ATTRIBUTES);
        HtmlRendererUtils.renderHTMLAttributes(writer, uiComponent, HTML.EVENT_HANDLER_ATTRIBUTES);

        if (disabled) {
            writer.writeAttribute(HTML.DISABLED_ATTR, Boolean.TRUE, null);
        }

        int selectedMonth = userData.getMonth() == null ? -1 : Integer.parseInt(userData.getMonth())-1;

        String[] months = mapMonths(new DateFormatSymbols(currentLocale));
        for (int i = 0; i < months.length; i++) {
            String monthName = months[i];
            String monthNumber = Integer.toString(i+1);

            writer.write("\t\t");
            writer.startElement(HTML.OPTION_ELEM, null);
            writer.writeAttribute(HTML.VALUE_ATTR, monthNumber, null);

            if (i == selectedMonth)
                writer.writeAttribute(HTML.SELECTED_ATTR, HTML.SELECTED_ATTR, null);

            writer.writeText(monthName, null);

            writer.endElement(HTML.OPTION_ELEM);
        }

        // bug #970747: force separate end tag
        writer.writeText("", null);
        writer.endElement(HTML.SELECT_ELEM);
    }

    private static void encodeInputYear(UIComponent uiComponent, ResponseWriter writer, String clientId, UserData userData, boolean disabled) throws IOException {
        encodeInputField(uiComponent, writer, clientId + ID_YEAR_POSTFIX, userData.getYear(), 4, disabled);
    }
    
    private static void encodeInputHours(UIComponent uiComponent, ResponseWriter writer, String clientId, UserData userData, boolean disabled) throws IOException {
        encodeInputField(uiComponent, writer, clientId + ID_HOURS_POSTFIX, userData.getHours(), 2, disabled);
    }
    
    private static void encodeInputMinutes(UIComponent uiComponent, ResponseWriter writer, String clientId, UserData userData, boolean disabled) throws IOException {
        encodeInputField(uiComponent, writer, clientId + ID_MINUTES_POSTFIX, userData.getMinutes(), 2, disabled);
    }
    
    private static void encodeInputSeconds(UIComponent uiComponent, ResponseWriter writer, String clientId, UserData userData, boolean disabled) throws IOException {
        encodeInputField(uiComponent, writer, clientId + ID_SECONDS_POSTFIX, userData.getSeconds(), 2, disabled);
    }
    
    private static String[] mapMonths(DateFormatSymbols symbols) {
        String[] months = new String[12];

        String[] localeMonths = symbols.getMonths();

        months[0] = localeMonths[Calendar.JANUARY];
        months[1] = localeMonths[Calendar.FEBRUARY];
        months[2] = localeMonths[Calendar.MARCH];
        months[3] = localeMonths[Calendar.APRIL];
        months[4] = localeMonths[Calendar.MAY];
        months[5] = localeMonths[Calendar.JUNE];
        months[6] = localeMonths[Calendar.JULY];
        months[7] = localeMonths[Calendar.AUGUST];
        months[8] = localeMonths[Calendar.SEPTEMBER];
        months[9] = localeMonths[Calendar.OCTOBER];
        months[10] = localeMonths[Calendar.NOVEMBER];
        months[11] = localeMonths[Calendar.DECEMBER];

        return months;
    }

    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        RendererUtils.checkParamValidity(facesContext, uiComponent, HtmlInputDate.class);
        
        if( isDisabled(facesContext, uiComponent) ) // For safety, do not set the submited value if the component is disabled.
            return;

        HtmlInputDate inputDate = (HtmlInputDate) uiComponent;
        Locale currentLocale = facesContext.getViewRoot().getLocale();
        UserData userData = inputDate.getUserData(currentLocale);
        String clientId = inputDate.getClientId(facesContext);
        String type = inputDate.getType();
        Map requestMap = facesContext.getExternalContext().getRequestParameterMap();

        if( ! type.equals( "time" ) ){
            userData.setDay( (String) requestMap.get(clientId + ID_DAY_POSTFIX) );
            userData.setMonth( (String) requestMap.get(clientId + ID_MONTH_POSTFIX) );
            userData.setYear( (String) requestMap.get(clientId + ID_YEAR_POSTFIX) );
        }
        
        if( ! type.equals( "date" ) ){
            userData.setHours( (String) requestMap.get(clientId + ID_HOURS_POSTFIX) );
            userData.setMinutes( (String) requestMap.get(clientId + ID_MINUTES_POSTFIX) );
            userData.setSeconds( (String) requestMap.get(clientId + ID_SECONDS_POSTFIX) );
        }

        inputDate.setSubmittedValue( userData );
    }
    
    public Object getConvertedValue(FacesContext context, UIComponent uiComponent, Object submittedValue) throws ConverterException {
        UserData userData = (UserData) submittedValue;
        try {
            return userData.parse();
        } catch (ParseException e) {
            Object[] args = {uiComponent.getId()};
            throw new ConverterException(MessageUtils.getMessage(FacesMessage.SEVERITY_ERROR, DATE_MESSAGE_ID, args));
        }
    }
}