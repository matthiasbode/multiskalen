/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.ga.localsearch;

import applications.mmrcsp.model.operations.Operation;
import org.graph.Path;

/**
 *
 * @author bode
 */
public class CriticalPath<O extends Operation> extends Path<O> {

    private O pre;
    private O suc;
    private CriticalPath affectedBy;

    public CriticalPath() {
    }

    public CriticalPath(O... vertices) {
        super(vertices);
    }

    public CriticalPath(Path<O> path) {
        super(path);
    }

    public CriticalPath getAffectedBy() {
        return affectedBy;
    }

    public void setAffectedBy(CriticalPath affectedBy) {
        this.affectedBy = affectedBy;
    }

    public O getPre() {
        return pre;
    }

    public void setPre(O pre) {
        this.pre = pre;
    }

    public O getSuc() {
        return suc;
    }

    public void setSuc(O suc) {
        this.suc = suc;
    }

}
