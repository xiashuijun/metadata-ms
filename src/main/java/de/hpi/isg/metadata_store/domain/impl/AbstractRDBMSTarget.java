package de.hpi.isg.metadata_store.domain.impl;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import de.hpi.isg.metadata_store.domain.Location;
import de.hpi.isg.metadata_store.domain.Target;
import de.hpi.isg.metadata_store.domain.common.impl.ExcludeHashCodeEquals;
import de.hpi.isg.metadata_store.domain.factories.SQLInterface;

public abstract class AbstractRDBMSTarget extends AbstractTarget {

    private static final long serialVersionUID = -2207050281912169066L;
    
    @ExcludeHashCodeEquals
    private Reference<IntCollection> childIdCache; 

    @ExcludeHashCodeEquals
    private SQLInterface sqlInterface;

    public AbstractRDBMSTarget(RDBMSMetadataStore observer, int id, String name, Location location, boolean isFreshlyCreated) {
        super(observer, id, name, location);
        this.sqlInterface = observer.getSQLInterface();

        // If we just created this Target, it cannot have children.
        // Therefore, we can safely build a new child ID cache.
        if (isFreshlyCreated) {
            this.childIdCache = new SoftReference<IntCollection>(new IntOpenHashSet());
        }
    }
    
    /**
     * @return the IDs of the children of this {@link Target} or {@code null}.
     */
    public IntCollection getChildIdCache() {
        if (this.childIdCache == null) {
            return null;
        }
        
        IntCollection childIdCache = this.childIdCache.get();
        if (childIdCache == null) {
            // Clear the reference if it has lost the cache.
            clearChildIdCache();
        }
        
        return childIdCache;
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

    public SQLInterface getSqlInterface() {
        return sqlInterface;
    }

    @Override
    public String toString() {
        return String.format("Target[%s, %s, %08x]", this.getName(), this.getLocation(), this.getId());
    }

}