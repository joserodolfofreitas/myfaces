/**
 * MyFaces - the free JSF implementation
 * Copyright (C) 2003, 2004  The MyFaces Team (http://myfaces.sourceforge.net)
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
package javax.faces.convert;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * $Log$
 * Revision 1.6  2004/06/07 12:17:36  mwessendorf
 * throws now ConverterException
 *
 * Revision 1.5  2004/03/26 12:08:41  manolito
 * Exceptions in getAsString now catched and
 * more relaxed Number casting in all number converters
 *
 * @author Thomas Spiegl (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface Converter
{
    Object getAsObject(FacesContext context,
                       UIComponent component,
                       String value) throws ConverterException;

    String getAsString(FacesContext context,
                       UIComponent component,
                       Object value) throws ConverterException;
}
