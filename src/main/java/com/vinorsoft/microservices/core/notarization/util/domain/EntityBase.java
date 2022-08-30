package com.vinorsoft.microservices.core.notarization.util.domain;

/**
 * Base class for an entity, as explained in the book "Domain Driven Design".
 * All entities in this project have an identity attribute with type Long and
 * name id. Inspired by the DDD Sample project.
 * 
 */
public abstract class EntityBase<T extends EntityBase<T>> {

    /**
     * This identity field has the wrapper class type Long so that an entity which
     * has not been saved is recognizable by a null identity.
     */
    protected Long ID;

    /**
     * Returns the identity of this entity object.
     * 
     * @return the identity of this entity object
     * 
     * @deprecated You should not pass this identity to the interface layer, if you
     *             can identify an entity by a domain key. You may use it in the
     *             domain layer, as accesses by identity are more efficient.
     */
    public Long getId() {
        return ID;
    }

    /**
     * Entities compare by identity, not by attributes.
     * Comparison by this method is to be preferred over comparison by
     * {@link #equals(Object)}, as this method is typed stronger.
     *
     * @param that The other entity of the same type
     * @return true if the identities are the same, regardless of the other
     *         attributes.
     * @throws IllegalStateException one of the entities does not have the identity
     *                               attribute set.
     */
    public boolean sameIdentityAs(final T that) {
        return this.equals(that);
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof EntityBase)) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        final EntityBase<?> that = (EntityBase<?>) object;
        _checkIdentity(this);
        _checkIdentity(that);
        return this.ID.equals(that.getId());
    }

    /**
     * Checks the passed entity, if it has an identity. It gets an identity only by
     * saving.
     * 
     * @param entity the entity to be checked
     * @throws IllegalStateException the passed entity does not have the identity
     *                               attribute set.
     */
    private void _checkIdentity(final EntityBase<?> entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("Comparison identity missing in entity: " + entity);
        }
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

}
