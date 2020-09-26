/*******************************************************************************
 * Copyright (c) 2011 Igor Fedorenko
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Igor Fedorenko - initial API and implementation
 *******************************************************************************/

package com.ifedorenko.p2browser.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ifedorenko.p2browser.model.IGroupedInstallableUnits;

public class InstallableUnitContentProvider implements ITreeContentProvider {

    private Map<IInstallableUnit, InstallableUnitNode> nodes;
    private HashMap<Object, Object[]> cachedChildren;
    private HashMap<Object, Long> cachedExpiration;

    public InstallableUnitContentProvider(TreeViewer treeViewer) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        nodes = new HashMap<>();
    }

    public Object[] getChildren(Object parentElement) {
        return getCachedChildren(parentElement, 20000);
    }

    public Object[] getChildrenInternal(Object parentElement) {
        if (parentElement instanceof IGroupedInstallableUnits) {
            IGroupedInstallableUnits metadata = (IGroupedInstallableUnits) parentElement;
            return toViewNodes(metadata, metadata.getRootIncludedInstallableUnits());
        } else if (parentElement instanceof InstallableUnitNode) {
            InstallableUnitNode node = (InstallableUnitNode) parentElement;
            IGroupedInstallableUnits metadata = node.getMetadata();
            return toViewNodes(metadata, metadata.getIncludedInstallableUnits(node.getInstallableUnit(), false));
        }
        return null;
    }

    private Object[] getCachedChildren(Object parentElement, long duration) {
        if (cachedChildren == null) {
            cachedChildren = new HashMap<Object, Object[]>();
        }
        if (cachedExpiration == null) {
            cachedExpiration = new HashMap<Object, Long>();
        }
        if (cachedExpiration.get(parentElement) == null
                || cachedExpiration.get(parentElement).longValue() < System.currentTimeMillis()
                || cachedChildren.get(parentElement) == null) {
            Object[] elements = getChildrenInternal(parentElement);
            cachedChildren.put(parentElement, elements);
            cachedExpiration.put(parentElement, System.currentTimeMillis() + duration);
            return elements;
        }
        return cachedChildren.get(parentElement);
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    protected Object[] toViewNodes(IGroupedInstallableUnits metadata, Collection<IInstallableUnit> units) {
        ArrayList<InstallableUnitNode> nodes = new ArrayList<>();
        for (IInstallableUnit unit : units) {
            InstallableUnitNode node = this.nodes.computeIfAbsent(unit, key -> new InstallableUnitNode(metadata, key));
            nodes.add(node);
        }
        return nodes.toArray();
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public boolean hasChildren(Object element) {
        Object[] ret = getChildren(element);
        return ret != null && ret.length > 0;
    }

}
