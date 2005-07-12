/*
 * Created on 12.06.2005
 *
 */
package freemind.view.mindmapview;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import freemind.modes.attributes.AttributeTableModel;
import freemind.modes.attributes.PersistentAttributeTableModel;
import freemind.modes.attributes.ExtendedAttributeTableModel;
import freemind.modes.attributes.FilteredAttributeTableModel;

/**
 * @author dimitri
 * 12.06.2005
 */
public class AttributeTable extends JTable {
    static private AttributeTable selectedTable = null;
    static private class MyFocusListener implements FocusListener{
        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
         */
        public void focusGained(FocusEvent event) { 
            AttributeTable table = (AttributeTable)event.getSource();
            NodeView nodeView = table.node;
            nodeView.getMap().selectAsTheOnlyOneSelected(nodeView);
            selectedTable = table;
        }
        
        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
         */
        public void focusLost(FocusEvent event) {
        }
    }
    
    static private class MyComponentListener implements ComponentListener{

        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
         */
        public void componentResized(ComponentEvent e) {
            AttributeTable table = (AttributeTable)e.getSource();
            JComponent map = (JComponent)table.getParent().getParent().getParent().getParent();
            map.revalidate();
        }

        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
         */
        public void componentMoved(ComponentEvent e) {
        }

        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
         */
        public void componentShown(ComponentEvent e) {
        }

        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
         */
        public void componentHidden(ComponentEvent e) {
         }
        
    }

    static private MyFocusListener focusListener = new MyFocusListener();
    static private MyComponentListener componentListener = new MyComponentListener();
    private AttributeTableModel currentModel;
    static private JComboBox comboBox = null; 
    static private ComboBoxModel defaultModel = null; 
    static private DefaultCellEditor dce = null;
    private NodeView node;
    public AttributeTable(NodeView node) {
        super();
        this.node = node;
        addFocusListener(focusListener);
        addComponentListener(componentListener);
        currentModel = node.getCurrentAttributeTableModel();
        setModel(currentModel);
        setDefaultEditor(Object.class, getDCE());
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(AUTO_RESIZE_OFF);
    }

    public Component prepareEditor(TableCellEditor tce, int row, int col) {
        ComboBoxModel model;
        switch (col){
        case 0:        
            model = currentModel.getNode().getMap().getRegistry().getAttributes().getComboBoxModel();
            break;
        case 1:
            String attrName = currentModel.getValueAt(row, 0).toString();
            model = currentModel.getNode().getMap().getRegistry().getAttributes().getDefaultComboBoxModel(attrName);
            break;
        default:
            model = getDefaultModel();
        }
        model.setSelectedItem("");
        if(selectedTable != this)
        {
            node.getMap().selectAsTheOnlyOneSelected(node);
            selectedTable = this;
        }
        comboBox.setModel(model);
        return super.prepareEditor(tce, row, col);
    }

    public Dimension getPreferredScrollableViewportSize() {
        if(! isValid())
            validate();
        Dimension dimension = super.getPreferredSize();
        dimension.width = Math.min(600, dimension.width);
        dimension.height = Math.min(300, dimension.height);
        return dimension;
    }
    public boolean getScrollableTracksViewportWidth() {
        return false; // getWidth() < 300;
    }
    private static DefaultCellEditor getDCE() {
        if (dce == null)
        {
            comboBox = new JComboBox();
            comboBox.setEditable(true);
            dce = new DefaultCellEditor(comboBox);
        }
        return dce;
    }
    public static ComboBoxModel getDefaultModel() {
        if (defaultModel == null)
        {
            defaultModel = new DefaultComboBoxModel();
         }
        return defaultModel;
    }
    static void clearOldSelection(AttributeTable table){
        if(selectedTable != null && selectedTable != table){
            if (selectedTable.isEditing()){                
                selectedTable.getCellEditor().stopCellEditing();
            }
            selectedTable.clearSelection(); 
            selectedTable = null;
        }
    }
}
