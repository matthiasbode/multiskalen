package org.graph.directed;

import java.util.HashSet;
import org.graph.Path;
import org.graph.directed.DefaultDirectedGraph;

/**
 * A rooted tree is a weakly connected acyclic Graph. In a tree any two vertices
 * are connected by exactly one simple path.
 * Mathematical a tree is defined by a Graph G := (V, E), where |E| = |V|-1
 * <p>
 * To ensure, that the definition of a tree is not hurt, the method
 * <tt>addVertex(E vertex)</tt>, inherited from the <tt>Graph</tt>-class hasn't
 * any effect.
 * The method <tt>addEdge(V first, V second)</tt> calls the
 * <tt>addChild(V child, V parent)</tt> method.
 *
 * @author Nils Rinke
 */
public class RootedTree<V> extends DefaultDirectedGraph<V>{
    V root;


    /**
     * Constructs an empty tree with the specified root.
     */
    public RootedTree(V root) {
        super();
        this.root = root;
        super.addVertex(root);
    }

    
    /**
     * Returns the root of the tree.
     *
     * @return the root of the tree
     */
    public V getRoot() {
        return root;
    }


    /**
     * Returns the childs of <tt>parent</tt>.
     *
     * @param   parent  a vertex in the tree
     * @return  the child of <I>parent</I>
     */
    public HashSet<V> getChildren(V parent) {
        return getSuccessors(parent);
    }


    /**
     * Returns the number of children of <tt>parent</tt>. Returns 0 if the
     * vertex is a leaf.
     *
     * @param  parent a vertex in the tree
     * @return the number of children of the vertex <tt>parent</tt>
     */
    public int getChildCount(V parent) {
        return outDegreeOf(parent);
    }


    /**
     * Returns the parent vertex of the specified child.
     *
     * @param child
     * @return the parent of the specified child.
     */
    public V getParent(V child) {
        if(child.equals(root))
            return root;
        return getPredecessors(child).iterator().next();
    }

    
    /**
     * Returns whether the specified vertex is a leaf vertex.
     *
     * @param vertex the vertex to check
     * @return true if the vertex is a leaf vertex
     *
     */
    public boolean isLeaf(V vertex) {
        return outDegreeOf(vertex)==0;
    }


    /**
     * Returns whether the first vertex is a descendant of the second vertex.
     *
     * @param child the possible child vertex to check
     * @param ancestor the possible ancestor vertex to check
     * @return <code>true</code> if the first vertex is
     *         a descendant of the vertex
     *
     */
    public boolean isDescendant(V child, V ancestor) {
        if(containsVertex(child)) {
            if(child.equals(root))
                return false;
            if(ancestor.equals(root))
                return true;
            V parent = getPredecessors(child).iterator().next();
            while (!parent.equals(root)) {
                if(parent.equals(ancestor))
                    return true;
                parent = getPredecessors(parent).iterator().next();
            }
        }
        return false;
    }


    /**
     * Use of this method hasn't any effect. Adding a vertex to a tree must
     * specify the parent vertex, due to the definition of the tree.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public boolean addVertex(V vertex) {
        throw new UnsupportedOperationException("Adding a single vertex to a " +
                "tree can't garantee the tree definition. Use " +
                "<tt>addChild(E child, E parent)</tt> instead");
    }


    /**
     * This Method calls the <tt>addChild(E child, E parent)</tt> method,
     * to ensure, that the definition of a tree is not hurt.
     */
    @Override
    public boolean addEdge(V first, V second) {
        return addChild(second, first);
    }


    /**
     * Adds the specified child to the specified parent.
     * 
     * @param child the new vertex to be added to the tree
     * @param parent the parent vertex of the child
     * 
     * @throws IllegalArgumentException If the child vertex is the root of the
     * tree or the child is already added to the tree.
     */
    public boolean addChild(V child, V parent) {
        if(containsVertex(child))
            throw new IllegalArgumentException("a vertex in a tree can't"
                    + " have more then one parent");
        if(child.equals(root))
            throw new IllegalArgumentException("a edge to the root in a tree is"
                    + " not allowed");
        super.addVertexWithoutCheckContains(child);
        super.addEdgeWithoutCheckContains(parent, child);
        return true;
    }


    /**
     * Gets the path between the specified child to the specified ancestor.
     *
     * @param child the new vertex to be added to the tree
     * @param ancestor the ancestor vertex of the child
     * @param the Path from the ancestor to the child
     * @throws NullPointerException
     */
    public Path<V> getPath(V ancestor, V child) {
        if(containsVertex(ancestor) && containsVertex(child)) {
            if(isDescendant(child, ancestor)) {
                Path<V> path = new Path<V>(child);
                V parent = getParent(child);
                while (!parent.equals(ancestor)) {
                    path.appendVertexInFront(parent);
                    parent = getParent(parent);
                }
                path.appendVertexInFront(parent);
                return path;
            }
            if(ancestor.equals(child))
                return new Path<V>(ancestor);
            System.out.println(root + " / " + child + " / " + ancestor);
            throw new NullPointerException("The first vertex is not an " +
                    "ancestor of the second vertex.");
        }
        if(!containsVertex(ancestor))
           throw new NullPointerException("The tree doesn't contain ancestor.");
        else
           throw new NullPointerException("The tree doesn't contain child.");
    }
    

    public HashSet<V> getLeafs() {
        HashSet<V> leafs = new HashSet<V>();
        for (V vertex : vertices) {
            if(outDegreeOf(vertex)==0 && !vertex.equals(root))
                leafs.add(vertex);
        }
        return leafs;
    }

    @Override
    public String toString() {
        return edges.toString();
    }
    
    
    public static void main(String[] args) {
        double root = 0.;
        RootedTree<Double> tree = new RootedTree<Double>(root);
        tree.addChild(3., root);
        tree.addChild(4., root);
        tree.addChild(5., 4.);
        tree.addChild(8., 4.);
//        tree.addChild(5., 3.); //throws an exception
        System.out.println(tree.getPath(root, 5.));
    }
}