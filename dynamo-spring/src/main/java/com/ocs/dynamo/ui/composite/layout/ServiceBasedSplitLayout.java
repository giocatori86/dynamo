/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.ocs.dynamo.ui.composite.layout;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.util.StringUtils;

import com.ocs.dynamo.dao.query.FetchJoinInformation;
import com.ocs.dynamo.domain.AbstractEntity;
import com.ocs.dynamo.domain.model.EntityModel;
import com.ocs.dynamo.service.BaseService;
import com.ocs.dynamo.ui.composite.form.FormOptions;
import com.ocs.dynamo.ui.composite.table.BaseTableWrapper;
import com.ocs.dynamo.ui.composite.table.ServiceResultsTableWrapper;
import com.ocs.dynamo.ui.container.QueryType;
import com.ocs.dynamo.ui.container.ServiceContainer;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.filter.And;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.TextField;

/**
 * A split layout - contains both a table and a details view - that uses a service to fetch data
 * 
 * @author bas.rutten
 * @param <ID>
 *            type of the primary key
 * @param <T>
 *            type of the entity
 */
@SuppressWarnings("serial")
public abstract class ServiceBasedSplitLayout<ID extends Serializable, T extends AbstractEntity<ID>>
        extends BaseSplitLayout<ID, T> {

    private static final long serialVersionUID = 1068860513192819804L;

    private Filter filter;

    private QueryType queryType = QueryType.ID_BASED;

    /**
     * Constructor
     * 
     * @param service
     * @param entityModel
     * @param formOptions
     * @param fieldFilters
     * @param sortOrder
     * @param joins
     */
    public ServiceBasedSplitLayout(BaseService<ID, T> service, EntityModel<T> entityModel,
            FormOptions formOptions, SortOrder sortOrder, FetchJoinInformation... joins) {
        super(service, entityModel, formOptions, sortOrder, joins);
    }

    @Override
    protected void afterReload(T t) {
        // in a lazy query container, the entity ID is used as the key
        getTableWrapper().getTable().select(t == null ? null : t.getId());
    }

    /**
     * Constructs the extra search filter - override in subclass if your panel contains a quick
     * search field
     * 
     * @param value
     * @return
     */
    protected Filter constructQuickSearchFilter(String value) {
        return null;
    }

    /**
     * Constructs a quick search field
     */
    @Override
    protected TextField constructSearchField() {
        if (getFormOptions().isShowQuickSearchField()) {
            TextField searchField = new TextField(message("ocs.search"));

            // respond to the user entering a search term
            searchField.addTextChangeListener(new TextChangeListener() {

                @Override
                public void textChange(TextChangeEvent event) {
                    String text = event.getText();
                    if (!StringUtils.isEmpty(text)) {
                        Filter extra = constructQuickSearchFilter(text);

                        Filter f = extra;
                        if (getFilter() != null) {
                            f = new And(extra, getFilter());
                        }
                        getContainer().search(f);
                    } else {
                        getContainer().search(filter);
                    }
                }
            });
            return searchField;
        }
        return null;
    }

    @Override
    protected BaseTableWrapper<ID, T> constructTableWrapper() {
        ServiceResultsTableWrapper<ID, T> tw = new ServiceResultsTableWrapper<ID, T>(getService(),
                getEntityModel(), getQueryType(), filter, getSortOrders(), getJoins()) {

            @Override
            protected void onSelect(Object selected) {
                setSelectedItems(selected);
                checkButtonState(getSelectedItem());
                if (getSelectedItem() != null) {
                    detailsMode(getSelectedItem());
                }
            }
        };
        tw.build();
        return tw;
    }

    /**
     * Creates the main search filter - overwrite in subclass if you need to actually filter the
     * data
     * 
     * @return
     */
    protected Filter constructFilter() {
        return null;
    }

    @SuppressWarnings("unchecked")
    protected ServiceContainer<ID, T> getContainer() {
        return (ServiceContainer<ID, T>) getTableWrapper().getContainer();
    }

    public Filter getFilter() {
        return filter;
    }

    @Override
    protected void init() {
        filter = constructFilter();
    }

    @Override
    public void reload() {
        super.reload();
        getTableWrapper().reloadContainer();
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @SuppressWarnings("unchecked")
    public void setSelectedItems(Object selectedItems) {
        if (selectedItems != null) {
            if (selectedItems instanceof Collection<?>) {
                // the lazy query container returns an array of IDs of the
                // selected items
                Collection<?> col = (Collection<?>) selectedItems;
                ID id = (ID) col.iterator().next();
                setSelectedItem(getService().fetchById(id));
            } else {
                ID id = (ID) selectedItems;
                setSelectedItem(getService().fetchById(id));
            }
        } else {
            setSelectedItem(null);
            emptyDetailView();
        }
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

}
