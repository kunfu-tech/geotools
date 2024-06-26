/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2019, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */

package org.geotools.brewer.styling.filter;

import java.util.ArrayList;
import java.util.List;
import org.geotools.api.filter.And;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.filter.identity.Identifier;
import org.geotools.brewer.styling.builder.Builder;
import org.geotools.factory.CommonFactoryFinder;

/** FilterBuilder acting as a simple wrapper around an Expression. */
public class AndBuilder<P> implements Builder<And> {
    protected FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
    protected P parent;
    protected List<FilterBuilder> list;

    private List<Identifier> ids = new ArrayList<>();

    public AndBuilder() {
        reset();
    }

    public AndBuilder(P parent) {
        this.parent = parent;
        reset();
    }

    /** Build an And filter */
    @Override
    public And build() {
        if (list == null) {
            return null;
        }
        List<Filter> filters = new ArrayList<>(list.size());
        for (FilterBuilder build : list) {
            Filter filter = build.build();
            if (filter != null) {
                filters.add(filter);
            }
        }
        if (parent == null) {
            list.clear();
        }
        return ff.and(filters);
    }

    public AndBuilder<P> fid(String fid) {
        ids.add(ff.featureId(fid));
        return this;
    }

    public AndBuilder<P> and(Filter filter) {
        list.add(new FilterBuilder().reset(filter));
        return this;
    }

    public AndBuilder<P> fid(List<String> fids) {
        for (String fid : fids) {
            ids.add(ff.featureId(fid));
        }
        return this;
    }

    public P end() {
        return parent;
    }

    @Override
    public AndBuilder<P> reset() {
        this.list = new ArrayList<>();
        return this;
    }

    @Override
    public AndBuilder<P> reset(And filter) {
        if (filter == null) {
            return unset();
        }
        this.list = new ArrayList<>();
        if (filter.getChildren() != null) {
            for (Filter child : filter.getChildren()) {
                list.add(new FilterBuilder().reset(child));
            }
        }
        return this;
    }

    @Override
    public AndBuilder<P> unset() {
        this.list = null;
        return this;
    }
}
