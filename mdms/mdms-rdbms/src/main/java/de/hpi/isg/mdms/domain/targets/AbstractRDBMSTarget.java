package de.hpi.isg.mdms.domain.targets;

import de.hpi.isg.mdms.domain.RDBMSMetadataStore;
import de.hpi.isg.mdms.model.location.Location;
import de.hpi.isg.mdms.model.targets.Target;
import de.hpi.isg.mdms.model.common.ExcludeHashCodeEquals;
import de.hpi.isg.mdms.rdbms.SQLInterface;
import de.hpi.isg.mdms.model.targets.AbstractTarget;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.sql.SQLException;

/**
 * This class is the abstract super class for all kind of {@link Target} objects that can be stored inside an
 * {@link de.hpi.isg.mdms.domain.RDBMSMetadataStore}.
 * 
 * @author fabian
 *
 */
public abstract class AbstractRDBMSTarget extends AbstractTarget {

    private static final long serialVersionUID = -2207050281912169066L;

    @ExcludeHashCodeEquals
    // private Reference<IntCollection> childIdCache;
    private transient IntCollection childIdCache;

    @ExcludeHashCodeEquals
    protected final transient RDBMSMetadataStore metadataStore;

    public AbstractRDBMSTarget(RDBMSMetadataStore observer, int id, String name, String description, Location location,
            boolean isFreshlyCreated) {
        super(observer, id, name, description, location);
        if (id != this.getId()) {
            // FIXME: Schema IDs can potentially be -1; in that case, an ID will be autogenerated.
            // For now, we simply detect this situation.
            throw new IllegalStateException(String.format("Target should have ID %08x but actually has %08x.", id,
                    this.getId()));
        }
        this.metadataStore = observer;

        // If we just created this Target, it cannot have children.
        // Therefore, we can safely build a new child ID cache.
        if (isFreshlyCreated) {
            // this.childIdCache = new SoftReference<IntCollection>(new IntOpenHashSet());
            this.childIdCache = new IntOpenHashSet();
        }
    }

    /**
     * @return the IDs of the children of this {@link Target} or {@code null}.
     */
    public IntCollection getChildIdCache() {
        // if (this.childIdCache == null) {
        // return null;
        // }
        //
        // IntCollection childIdCache = this.childIdCache.get();
        // if (childIdCache == null) {
        // // Clear the reference if it has lost the cache.
        // clearChildIdCache();
        // }
        //
        // return childIdCache;
        return this.childIdCache;
    }

    /**
     * Removes the child ID cache.
     */
    protected void clearChildIdCache() {
        this.childIdCache = null;
    }

    /**
     * Adds the given ID to the child ID cache if the cache is present.
     */
    protected void addToChildIdCache(int childId) {
        IntCollection childIdCache = getChildIdCache();
        if (childIdCache != null) {
            childIdCache.add(childId);
        }
    }

    /**
     * Stores this target.
     */
    abstract public void store() throws SQLException;

    public SQLInterface getSqlInterface() {
        return this.metadataStore.getSQLInterface();
    }

    @Override
    public String toString() {
        return String.format("Target[%s, %s, %08x]", this.getName(), this.getLocation(), this.getId());
    }
}
