package org.graph;

import org.util.Pair;
import java.util.ArrayList;

/**
 * A path in a graph is a sequence of edges such that the second vertex of each
 * edge is the first vertex of the following edge in the sequence.
 *
 * @author Nils Rinke
 */
public class Path<E> {

    /**
     *
     */
    protected ArrayList<Pair<E, E>> pathList;

    /**
     *
     */
    protected E start;

    /**
     * Constructs an empty path.
     */
    public Path() {
        pathList = new ArrayList<Pair<E, E>>();
    }

    /**
     * Constructs a path containing the nodes of the specified array.
     *
     * @param vertices the array whose elements represent the path.
     * @throws NullPointerException if the specified array is null
     */
    public Path(E... vertices) {
        this();
        start = vertices[0];
        for (int i = 1; i < vertices.length; i++) {
            appendEdge(new Pair<E, E>(vertices[i - 1], vertices[i]));
        }
    }

    /**
     * Constructs a path containing the edges of the specified list.
     *
     * @param pathEdges the list whose edges represent the path.
     * @throws NullPointerException if the specified list is null
     */
    public Path(Path<E> path) {
        this();
        start = path.start;
        for (Pair<E, E> edge : path.pathList) {
            appendEdge(edge);
        }
    }

    /**
     * Adds the specified element to the end of this path.
     *
     * @param vertex The vertex to be appended to this path
     */
    public void appendVertex(E vertex) {
        if (start == null) {
            start = vertex;
        } else {
            this.appendEdge(new Pair<E, E>(getEndVertex(), vertex));
        }
    }

    public void appendPath(Path<E> path) {
        for (Pair<E, E> edge : path.getPathEdges()) {
            appendEdge(edge);
        }
    }

    /**
     * Adds the specified element to the beginning of this path.
     *
     * @param vertex vertex to be appended to this path
     */
    public void appendVertexInFront(E vertex) {
        if (start == null) {
            start = vertex;
        } else {
            pathList.add(0, new Pair<E, E>(vertex, start));
            start = vertex;
        }
    }

    /**
     * Adds the specified <tt>Edge</tt> to the end of this path.
     *
     * @param edge edge to be appended to this path
     * @throws IllegalArgumentException if the first vertex of <tt>edge</tt> is
     * not equal to the current last vertex of the path.
     */
    public boolean appendEdge(Pair<E, E> edge) {
        if (pathList.isEmpty()) {
            start = edge.getFirst();
        }
        if (getEndVertex() == null) {
            System.out.println("letzetr Knoten null");
            System.out.println(pathList.size());
        }
        if (edge.getFirst() == null) {
            System.out.println("neuer startknoten null");
            System.out.println(pathList.size());
        }
        if (!getEndVertex().equals(edge.getFirst())) {
            throw new IllegalArgumentException("First vertex of the edge "
                    + "has to be equal to the current last vertex in the path.");
        }
        return pathList.add(edge);
    }

    /**
     * Adds the specified element to the beginning of this path.
     *
     * @param vertex vertex to be appended to this path
     * @throws IllegalArgumentException if the second vertex of <tt>edge</tt> is
     * not equal to the current start vertex of the path.
     */
    public void appendEdgeInFront(Pair<E, E> edge) {
        if (!edge.getSecond().equals(start)) {
            if (pathList.isEmpty()) {
                pathList.add(edge);
            } else {
                throw new IllegalArgumentException("Second vertex of the edge "
                        + "has to be equal to the current first vertex in the path.");
            }
        } else {
            pathList.add(0, edge);
        }
        start = edge.getFirst();
    }

    /**
     * Returns the start vertex of this path
     *
     * @return the start vertex of this path
     */
    public E getStartVertex() {
        return start;
    }

    /**
     * Returns the end vertex of this path
     *
     * @return the end vertex of this path
     */
    public E getEndVertex() {
        if (pathList.isEmpty()) {
            return start;
        }
        return pathList.get(pathList.size() - 1).getSecond();
    }

    /**
     * Returns the <code>Edge</code> at the specified position of this path.
     *
     * @return the vertex at the specified position of this path
     */
    public Pair<E, E> get(int i) {
        return pathList.get(i);
    }

    /**
     * Returns the element at the specified position of this path.
     *
     * @return the element at the specified position of this path
     */
    public E getVertexAt(int i) {
        if (i == 0) {
            return this.getStartVertex();
        }
        if (i > pathList.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (i == pathList.size()) {
            return pathList.get(i - 1).getSecond();
        }
        return pathList.get(i).getFirst();
    }

    public E getLastVertex() {
        if (pathList.size() == 0) {
            return start;
        }
        return pathList.get(pathList.size() - 1).getSecond();
    }

    /**
     * Converts this list of path edges into a list of the path nodes.
     *
     * @return A list containing all path nodes in the specified order
     */
    public ArrayList<E> getPathVertices() {
        ArrayList<E> vertexList = new ArrayList<E>();
        if (start == null) {
            start = pathList.get(0).getFirst();
        }
        vertexList.add(start);
        for (Pair<E, E> edge : pathList) {
            vertexList.add(edge.getSecond());
        }
        return vertexList;
    }

    /**
     *
     * @return
     */
    public ArrayList<Pair<E, E>> getPathEdges() {
        return pathList;
    }

    /**
     *
     * @param path
     * @return
     */
    public Path<E> product(Path<E> path) {
        if (pathList.isEmpty()) {
            return new Path<E>(path);
        }
        if (this.getEndVertex().equals(path.getStartVertex())) {
            Path<E> res = new Path<E>(this);
            res.start = start;
            for (Pair<E, E> edge : path.pathList) {
                res.pathList.add(edge);
            }
            return res;
        }
        throw new IllegalArgumentException("Last vertex of this path has to be"
                + " first vertex of the second path!");
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Path<E> other = (Path<E>) obj;
        if (this.pathList != other.pathList && (this.pathList == null || !this.pathList.equals(other.pathList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.pathList != null ? this.pathList.hashCode() : 0);
        return hash;
    }

    /**
     *
     * @return
     */
    public int getNumberOfVertices() {
        if (pathList.isEmpty()) {
            if (start != null) {
                return 1;
            }
            return 0;
        }
        return pathList.size() + 1;
    }

    /**
     *
     * @return
     */
    public int getNumberOfEdges() {
        return pathList.size();
    }

    /**
     *
     * @return
     */
    public Path<E> invertPath() {
        Path<E> ret = new Path<E>(start);
        for (Pair<E, E> edge : pathList) {
            ret.appendEdgeInFront(edge.transposition());
        }
        return ret;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        String message = "";
        if (getNumberOfVertices() == 1) {
            message += start;
        } else {
            for (int i = 0; i < this.getPathVertices().size(); i++) {
                E e = this.getPathVertices().get(i);
                message += e.toString();
                if (i < this.getPathVertices().size() - 1) {
                    message += ",";
                }
            }
        }

        return "<" + message + ">";
    }

    /**
     *
     */
    public void clear() {
        pathList.clear();
        start = null;
    }

    /**
     *
     * @param edge
     * @return
     */
    public boolean containsEdge(Pair<E, E> edge) {
        if (pathList.contains(edge)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param vertex
     * @return
     */
    public boolean containsVertex(E vertex) {
        if (start.equals(vertex)) {
            return true;
        }
        for (Pair<E, E> edge : pathList) {
            if (edge.getSecond().equals(vertex)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public Path<E> getSubPath(E from, E to) {
        int containsFrom = containsVertexWithPosition(from);
        int containsTo = containsVertexWithPosition(to);
        if (containsFrom != -1 && containsFrom <= containsTo) {
            Path<E> subPath = new Path<E>(from);
            for (int i = containsFrom; i < containsTo; i++) {
                subPath.appendEdge(pathList.get(i));
            }
            return subPath;
        }
        throw new IllegalArgumentException();
    }

    public boolean containsSubPath(Path<E> subPath) {
        int containsFrom = containsVertexWithPosition(subPath.getStartVertex());
        int containsTo = containsVertexWithPosition(subPath.getEndVertex());
        if (containsFrom != -1 && containsFrom <= containsTo) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param vertex
     * @return
     */
    private int containsVertexWithPosition(E vertex) {
        if (start.equals(vertex)) {
            return 0;
        }
        int counter = 1;
        for (Pair<E, E> edge : pathList) {
            if (edge.getSecond().equals(vertex)) {
                return counter;
            }
            counter++;
        }
        return -1;
    }

}
