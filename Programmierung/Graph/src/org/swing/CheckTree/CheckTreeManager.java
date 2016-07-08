package org.swing.CheckTree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class CheckTreeManager extends MouseAdapter {
    private CheckTreeSelectionModel selectionModel;
    private JTree tree = new JTree();
    int hotspot = new JCheckBox().getPreferredSize().width;
    DefaultTreeModel model;

    private TreeSelectionListener treeSelectionListener;

    public CheckTreeManager(JTree tree){
        this.tree = tree;
        selectionModel = new CheckTreeSelectionModel(tree.getModel());
        tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel));
        tree.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent me){
        TreePath path = tree.getPathForLocation(me.getX(), me.getY());
        if(path==null)
            return;
        if(me.getX()>tree.getPathBounds(path).x+hotspot)
            return;

        boolean selected = selectionModel.isPathSelected(path, true);
        
        try{
            if(selected)
                selectionModel.removeSelectionPath(path);
            else
                selectionModel.addSelectionPaths(new TreePath[]{path});
        } finally{ 
            tree.treeDidChange();
        }
    }

    public CheckTreeSelectionModel getSelectionModel(){
        return selectionModel;
    }

    public void setSelectionListener(TreeSelectionListener treeSelectionListener) {
        this.treeSelectionListener = treeSelectionListener;
        selectionModel.addTreeSelectionListener(this.treeSelectionListener);
    }
}