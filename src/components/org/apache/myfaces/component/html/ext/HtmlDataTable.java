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
package net.sourceforge.myfaces.component.html.ext;

import net.sourceforge.myfaces.component.UserRoleAware;
import net.sourceforge.myfaces.component.UserRoleUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.model.DataModel;
import javax.faces.render.Renderer;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Spiegl (latest modification by $Author$)
 * @author Manfred Geiler
 * @version $Revision$ $Date$
 * $Log$
 * Revision 1.13  2004/09/10 14:15:00  manolito
 * new previousRowDataVar attribute in extended HtmlDataTable
 *
 * Revision 1.12  2004/08/20 07:14:39  manolito
 * HtmlDataTable now also supports rowIndexVar and rowCountVar
 *
 * Revision 1.11  2004/08/10 13:29:59  manolito
 * full revision of extended HtmlDataTable so that there is no more cache problem
 *
 * Revision 1.10  2004/07/01 21:53:05  mwessendorf
 * ASF switch
 *
 * Revision 1.9  2004/06/22 14:33:29  royalts
 * no message
 *
 * Revision 1.8  2004/06/21 16:01:57  royalts
 * setSortAscending(...) and setSortColumn have to update model their own, because processUdates won't be executed.
 *
 * Revision 1.7  2004/06/21 14:43:20  manolito
 * no more calls to getRowCount to determine if list is empty before encodeBegin was called
 *
 * Revision 1.6  2004/06/21 12:15:29  manolito
 * encodeBegin in UIData examines descendants valid flag recursivly now before refreshing DataModel
 *
 * Revision 1.5  2004/05/21 10:39:26  manolito
 * new renderedIfEmpty attribute in ext. HtmlDataTable component
 *
 * Revision 1.4  2004/05/18 14:31:36  manolito
 * user role support completely moved to components source tree
 *
 * Revision 1.3  2004/05/18 11:22:44  manolito
 * optimized local value handling, so that getValue of UIData is only called when needed
 *
 */
public class HtmlDataTable
        extends HtmlDataTableHack
        implements UserRoleAware
{
    private static final Log log = LogFactory.getLog(HtmlDataTable.class);

    private static final boolean DEFAULT_SORTASCENDING = true;
    private static final Class OBJECT_ARRAY_CLASS = (new Object[0]).getClass();

    private transient boolean _isDataModelRestored = false;
    //private transient DataModel _dataModel;
    //transient private _SerializableDataModel _restoredDataModel = null;
    //transient private Object _cachedValue = null;

    //Flag to detect if component is rendered for the first time (restoreState sets it to false)
    transient private boolean _firstTimeRendered = true;

    private String _sortColumn = null;
    private Boolean _sortAscending = null;


    public void setValue(Object value)
    {
        _dataModel = null;
        _isDataModelRestored = false;
        super.setValue(value);
    }


    public void setRowIndex(int rowIndex)
    {
        String rowIndexVar = getRowIndexVar();
        String rowCountVar = getRowCountVar();
        String previousRowDataVar = getPreviousRowDataVar();
        if (rowIndexVar != null || rowCountVar != null || previousRowDataVar != null)
        {
            Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

            if (previousRowDataVar != null && rowIndex >= 0) //we only need to provide the previousRowDataVar for a valid rowIndex
            {
                if (isRowAvailable())
                {
                    //previous row is available
                    requestMap.put(previousRowDataVar, getRowData());
                }
                else
                {
                    //no previous row available
                    requestMap.put(previousRowDataVar, null);
                }
            }

            super.setRowIndex(rowIndex);

            if (rowIndex >= 0)
            {
                //regular row index, update request scope variables
                if (rowIndexVar != null)
                {
                    requestMap.put(rowIndexVar, new Integer(rowIndex));
                }

                if (rowCountVar != null)
                {
                    requestMap.put(rowCountVar, new Integer(getRowCount()));
                }
            }
            else
            {
                //rowIndex == -1 means end of loop --> remove request scope variables
                if (rowIndexVar != null)
                {
                    requestMap.remove(rowIndexVar);
                }

                if (rowCountVar != null)
                {
                    requestMap.remove(rowCountVar);
                }

                if (previousRowDataVar != null)
                {
                    requestMap.remove(previousRowDataVar);
                }
            }
        }
        else
        {
            // no extended var attributes defined, no special treatment
            super.setRowIndex(rowIndex);
        }
    }


    public void processRestoreState(FacesContext context, Object state)
    {
        super.processRestoreState(context, state);
    }

    public void processDecodes(FacesContext context)
    {
        super.processDecodes(context);
    }

    public void processValidators(FacesContext context)
    {
        super.processValidators(context);
    }

    public Object processSaveState(FacesContext context)
    {
        return super.processSaveState(context);
    }

    public void broadcast(FacesEvent event) throws AbortProcessingException
    {
        super.broadcast(event);
    }

    public void processUpdates(FacesContext context)
    {
        super.processUpdates(context);

        if (_isDataModelRestored)
        {
            updateModelFromPreservedDataModel(context);
        }

        if (isPreserveSort())
        {
            if (_sortColumn != null)
            {
                ValueBinding vb = getValueBinding("sortColumn");
                if (vb != null)
                {
                    vb.setValue(context, _sortColumn);
                    _sortColumn = null;
                }
            }

            if (_sortAscending != null)
            {
                ValueBinding vb = getValueBinding("sortAscending");
                if (vb != null)
                {
                    vb.setValue(context, _sortAscending);
                    _sortAscending = null;
                }
            }
        }
    }


    private void updateModelFromPreservedDataModel(FacesContext context)
    {
        ValueBinding vb = getValueBinding("value");
        if (vb != null && !vb.isReadOnly(context))
        {
            _SerializableDataModel dm = (_SerializableDataModel)_dataModel;
            Class type = vb.getType(context);
            if (DataModel.class.isAssignableFrom(type))
            {
                vb.setValue(context, dm);
            }
            else if (List.class.isAssignableFrom(type))
            {
                vb.setValue(context, (List)dm.getWrappedData());
            }
            else if (OBJECT_ARRAY_CLASS.isAssignableFrom(type))
            {
                List lst = (List)dm.getWrappedData();
                vb.setValue(context, lst.toArray(new Object[lst.size()]));
            }
            else if (ResultSet.class.isAssignableFrom(type))
            {
                throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
            }
            else
            {
                //Assume scalar data model
                List lst = (List)dm.getWrappedData();
                if (lst.size() > 0)
                {
                    vb.setValue(context, lst.get(0));
                }
                else
                {
                    vb.setValue(context, null);
                }
            }
        }
    }


    /**
     * TODO: We could perhaps optimize this if we know we are derived from MyFaces UIData implementation
     */
    private boolean isAllChildrenAndFacetsValid()
    {
        int first = getFirst();
        int rows = getRows();
        int last;
        if (rows == 0)
        {
            last = getRowCount();
        }
        else
        {
            last = first + rows;
        }
        try
        {
            for (int rowIndex = first; rowIndex < last; rowIndex++)
            {
                setRowIndex(rowIndex);
                if (isRowAvailable())
                {
                    if (!isAllEditableValueHoldersValidRecursive(getFacetsAndChildren()))
                    {
                        return false;
                    }
                }
            }
        }
        finally
        {
            setRowIndex(-1);
        }
        return true;
    }


    private boolean isAllEditableValueHoldersValidRecursive(Iterator facetsAndChildrenIterator)
    {
        while (facetsAndChildrenIterator.hasNext())
        {
            UIComponent c = (UIComponent)facetsAndChildrenIterator.next();
            if (c instanceof EditableValueHolder &&
                !((EditableValueHolder)c).isValid())
            {
                return false;
            }
            if (!isAllEditableValueHoldersValidRecursive(c.getFacetsAndChildren()))
            {
                return false;
            }
        }
        return true;
    }


    protected void refresh(FacesContext context)
    {
        if (log.isDebugEnabled()) log.debug("Refresh for HtmlDataTable " + getClientId(context) + " was called");
        
        if (_firstTimeRendered || isAllChildrenAndFacetsValid())
        {
            // No invalid children
            // --> clear data model
            _dataModel = null;
            _isDataModelRestored = false;

            _saveDescendantStates = false; // no need to save children states
        }
        else
        {
            _saveDescendantStates = true; // save children states (valid flag, submittedValues, etc.)
        }
    }

    
    public void encodeBegin(FacesContext context) throws IOException
    {
        //refresh of _dataModel already done via refresh (called by HtmlDataTablePhaseListener)

        if (isRenderedIfEmpty() || getRowCount() > 0)
        {
            if (context == null) throw new NullPointerException("context");
            if (!isRendered()) return;
            Renderer renderer = getRenderer(context);
            if (renderer != null)
            {
                renderer.encodeBegin(context, this);
            }
        }
    }

    public void encodeChildren(FacesContext context) throws IOException
    {
        if (isRenderedIfEmpty() || getRowCount() > 0)
        {
            super.encodeChildren(context);
        }
    }

    public void encodeEnd(FacesContext context) throws IOException
    {
        if (isRenderedIfEmpty() || getRowCount() > 0)
        {
            super.encodeEnd(context);
        }
    }

    public int getFirst()
    {
        if (_isDataModelRestored)
        {
            //Rather get the currently restored DataModel attribute
            return ((_SerializableDataModel)_dataModel).getFirst();
        }
        else
        {
            return super.getFirst();
        }
    }

    public void setFirst(int first)
    {
        if (_isDataModelRestored)
        {
            //Also change the currently restored DataModel attribute
            ((_SerializableDataModel)_dataModel).setFirst(first);
        }
        super.setFirst(first);
    }

    public int getRows()
    {
        if (_isDataModelRestored)
        {
            //Rather get the currently restored DataModel attribute
            return ((_SerializableDataModel)_dataModel).getRows();
        }
        else
        {
            return super.getRows();
        }
    }

    public void setRows(int rows)
    {
        if (_isDataModelRestored)
        {
            //Also change the currently restored DataModel attribute
            ((_SerializableDataModel)_dataModel).setRows(rows);
        }
        super.setRows(rows);
    }


    public Object saveState(FacesContext context)
    {
        boolean preserveSort = isPreserveSort();
        Object values[] = new Object[9];
        values[0] = super.saveState(context);
        values[1] = _preserveDataModel;
        if (isPreserveDataModel())
        {
            values[2] = saveAttachedState(context, getSerializableDataModel());
        }
        else
        {
            values[2] = null;
        }
        values[3] = _preserveSort;
        values[4] = preserveSort ? getSortColumn() : _sortColumn;
        values[5] = preserveSort ? Boolean.valueOf(isSortAscending()) : _sortAscending;
        values[6] = _renderedIfEmpty;
        values[7] = _rowCountVar;
        values[8] = _rowIndexVar;
        return ((Object) (values));
    }


    public void restoreState(FacesContext context, Object state)
    {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        _preserveDataModel = (Boolean)values[1];
        if (isPreserveDataModel())
        {
            _dataModel = (_SerializableDataModel)restoreAttachedState(context, values[2]);
            _isDataModelRestored = true;
        }
        else
        {
            _dataModel = null;
            _isDataModelRestored = false;
        }
        _preserveSort = (Boolean)values[3];
        _sortColumn = (String)values[4];
        _sortAscending = (Boolean)values[5];
        _renderedIfEmpty = (Boolean)values[6];
        _rowCountVar = (String)values[7];
        _rowIndexVar = (String)values[8];

        // restore state means component was already rendered at least once:
        _firstTimeRendered = false;
    }


    public _SerializableDataModel getSerializableDataModel()
    {
        if (_dataModel != null)
        {
            if (_dataModel instanceof _SerializableDataModel)
            {
                return (_SerializableDataModel)_dataModel;
            }
            else
            {
                return new _SerializableDataModel(getFirst(), getRows(), _dataModel);
            }
        }

        Object value = getValue();
        if (value == null)
        {
            return null;
        }
        else if (value instanceof DataModel)
        {
            return new _SerializableDataModel(getFirst(), getRows(), (DataModel)value);
        }
        else if (value instanceof List)
        {
            return new _SerializableListDataModel(getFirst(), getRows(), (List)value);
        }
        else if (OBJECT_ARRAY_CLASS.isAssignableFrom(value.getClass()))
        {
            return new _SerializableArrayDataModel(getFirst(), getRows(), (Object[])value);
        }
        else if (value instanceof ResultSet)
        {
            return new _SerializableResultSetDataModel(getFirst(), getRows(), (ResultSet)value);
        }
        else if (value instanceof javax.servlet.jsp.jstl.sql.Result)
        {
            return new _SerializableResultDataModel(getFirst(), getRows(), (javax.servlet.jsp.jstl.sql.Result)value);
        }
        else
        {
            return new _SerializableScalarDataModel(getFirst(), getRows(), (Object)value);
        }
    }

    public boolean isRendered()
    {
        if (!UserRoleUtils.isVisibleOnUserRole(this)) return false;
        return super.isRendered();
    }

    public void setSortColumn(String sortColumn)
    {
        _sortColumn = sortColumn;
        // update model is necessary here, because processUpdates is never called
        // reason: HtmlCommandSortHeader.isImmediate() == true
        ValueBinding vb = getValueBinding("sortColumn");
        if (vb != null)
        {
            vb.setValue(getFacesContext(), _sortColumn);
            _sortColumn = null;
        }
    }

    public String getSortColumn()
    {
        if (_sortColumn != null) return _sortColumn;
        ValueBinding vb = getValueBinding("sortColumn");
        return vb != null ? (String)vb.getValue(getFacesContext()) : null;
    }

    public void setSortAscending(boolean sortAscending)
    {
        _sortAscending = Boolean.valueOf(sortAscending);
        // update model is necessary here, because processUpdates is never called
        // reason: HtmlCommandSortHeader.isImmediate() == true
        ValueBinding vb = getValueBinding("sortAscending");
        if (vb != null)
        {
            vb.setValue(getFacesContext(), _sortAscending);
            _sortAscending = null;
        }
    }

    public boolean isSortAscending()
    {
        if (_sortAscending != null) return _sortAscending.booleanValue();
        ValueBinding vb = getValueBinding("sortAscending");
        Boolean v = vb != null ? (Boolean)vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : DEFAULT_SORTASCENDING;
    }





    //------------------ GENERATED CODE BEGIN (do not modify!) --------------------

    public static final String COMPONENT_TYPE = "net.sourceforge.myfaces.HtmlDataTable";
    private static final boolean DEFAULT_PRESERVEDATAMODEL = false;
    private static final boolean DEFAULT_PRESERVESORT = false;
    private static final boolean DEFAULT_RENDEREDIFEMPTY = true;

    private Boolean _preserveDataModel = null;
    private Boolean _preserveSort = null;
    private String _enabledOnUserRole = null;
    private String _visibleOnUserRole = null;
    private Boolean _renderedIfEmpty = null;
    private String _rowIndexVar = null;
    private String _rowCountVar = null;
    private String _previousRowDataVar = null;

    public HtmlDataTable()
    {
    }


    public void setPreserveDataModel(boolean preserveDataModel)
    {
        _preserveDataModel = Boolean.valueOf(preserveDataModel);
    }

    public boolean isPreserveDataModel()
    {
        if (_preserveDataModel != null) return _preserveDataModel.booleanValue();
        ValueBinding vb = getValueBinding("preserveDataModel");
        Boolean v = vb != null ? (Boolean)vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : DEFAULT_PRESERVEDATAMODEL;
    }

    public void setPreserveSort(boolean preserveSort)
    {
        _preserveSort = Boolean.valueOf(preserveSort);
    }

    public boolean isPreserveSort()
    {
        if (_preserveSort != null) return _preserveSort.booleanValue();
        ValueBinding vb = getValueBinding("preserveSort");
        Boolean v = vb != null ? (Boolean)vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : DEFAULT_PRESERVESORT;
    }

    public void setEnabledOnUserRole(String enabledOnUserRole)
    {
        _enabledOnUserRole = enabledOnUserRole;
    }

    public String getEnabledOnUserRole()
    {
        if (_enabledOnUserRole != null) return _enabledOnUserRole;
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String)vb.getValue(getFacesContext()) : null;
    }

    public void setVisibleOnUserRole(String visibleOnUserRole)
    {
        _visibleOnUserRole = visibleOnUserRole;
    }

    public String getVisibleOnUserRole()
    {
        if (_visibleOnUserRole != null) return _visibleOnUserRole;
        ValueBinding vb = getValueBinding("visibleOnUserRole");
        return vb != null ? (String)vb.getValue(getFacesContext()) : null;
    }

    public void setRenderedIfEmpty(boolean renderedIfEmpty)
    {
        _renderedIfEmpty = Boolean.valueOf(renderedIfEmpty);
    }

    public boolean isRenderedIfEmpty()
    {
        if (_renderedIfEmpty != null) return _renderedIfEmpty.booleanValue();
        ValueBinding vb = getValueBinding("renderedIfEmpty");
        Boolean v = vb != null ? (Boolean)vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : DEFAULT_RENDEREDIFEMPTY;
    }

    public void setRowIndexVar(String rowIndexVar)
    {
        _rowIndexVar = rowIndexVar;
    }

    public String getRowIndexVar()
    {
        if (_rowIndexVar != null) return _rowIndexVar;
        ValueBinding vb = getValueBinding("rowIndexVar");
        return vb != null ? (String)vb.getValue(getFacesContext()) : null;
    }

    public void setRowCountVar(String rowCountVar)
    {
        _rowCountVar = rowCountVar;
    }

    public String getRowCountVar()
    {
        if (_rowCountVar != null) return _rowCountVar;
        ValueBinding vb = getValueBinding("rowCountVar");
        return vb != null ? (String)vb.getValue(getFacesContext()) : null;
    }

    public void setPreviousRowDataVar(String previousRowDataVar)
    {
        _previousRowDataVar = previousRowDataVar;
    }

    public String getPreviousRowDataVar()
    {
        if (_previousRowDataVar != null) return _previousRowDataVar;
        ValueBinding vb = getValueBinding("previousRowDataVar");
        return vb != null ? (String)vb.getValue(getFacesContext()) : null;
    }



    //------------------ GENERATED CODE END ---------------------------------------
}
