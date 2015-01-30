package de.hpi.isg.metadata_store.domain.targets.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.apache.commons.lang3.Validate;

import de.hpi.isg.metadata_store.domain.Location;
import de.hpi.isg.metadata_store.domain.MetadataStore;
import de.hpi.isg.metadata_store.domain.common.Observer;
import de.hpi.isg.metadata_store.domain.common.impl.ExcludeHashCodeEquals;
import de.hpi.isg.metadata_store.domain.impl.AbstractTarget;
import de.hpi.isg.metadata_store.domain.location.impl.DefaultLocation;
import de.hpi.isg.metadata_store.domain.targets.Column;
import de.hpi.isg.metadata_store.domain.targets.Schema;
import de.hpi.isg.metadata_store.domain.targets.Table;
import de.hpi.isg.metadata_store.domain.util.IdUtils;
import de.hpi.isg.metadata_store.exceptions.NameAmbigousException;

/**
 * The default implementation of the {@link Table}.
 *
 */
public class DefaultTable extends AbstractTarget implements Table {

    public static Table buildAndRegister(final Observer observer, final Schema schema, final int id, final String name,
            final String description,
            final Location location) {
        final DefaultTable newTable = new DefaultTable(observer, schema, id, name, description, location);
        newTable.register();
        return newTable;
    }

    public static Table buildAndRegister(final Observer observer, final Schema schema, final String name,
            final String description,
            final Location location) {
        final DefaultTable newTable = new DefaultTable(observer, schema, -1, name, description, location);
        newTable.register();
        return newTable;
    }

    private static final long serialVersionUID = 1695408629652071459L;

    @ExcludeHashCodeEquals
    private final Collection<Column> columns;

    @ExcludeHashCodeEquals
    private final Schema schema;

    private DefaultTable(final Observer observer, final Schema schema, final int id, final String name,
            final String description,
            final Location location) {
        super(observer, id, name, description, location);
        this.columns = Collections.synchronizedSet(new HashSet<Column>());
        this.schema = schema;
    }

    @Override
    public Column addColumn(final MetadataStore metadataStore, final String name, final String description,
            final int index) {
        Validate.isTrue(metadataStore.getSchemas().contains(getSchema()));
        IdUtils idUtils = metadataStore.getIdUtils();
        final int localSchemaId = idUtils.getLocalSchemaId(getId());
        final int localTableId = idUtils.getLocalTableId(getId());
        final int columnId = idUtils.createGlobalId(localSchemaId, localTableId, idUtils.getMinColumnNumber() + index);
        final Location location = new DefaultLocation();
        location.getProperties().put(Location.INDEX, index + "");
        final Column column = DefaultColumn
                .buildAndRegister(metadataStore, this, columnId, name, description, location);
        this.columns.add(column);
        return column;
    }

    @Override
    public Collection<Column> getColumns() {
        return Collections.unmodifiableCollection(this.columns);
    }

    /**
     * @return the parent schema
     */
    @Override
    public Schema getSchema() {
        return this.schema;
    }

    @Override
    public String toString() {
        return String.format("Table[%s, %d columns, %08x]", getName(), getColumns().size(), getId());
    }

    @Override
    public Column getColumnByName(String name) throws NameAmbigousException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet.");
        // return null;
    }

    @Override
    public Collection<Column> getColumnsByName(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet.");
        // return null;
    }

    @Override
    public Column getColumnById(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet.");
        // return null;
    }
}
