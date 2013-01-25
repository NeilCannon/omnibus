package org.fuzzyrobot.omnibus.core;

/**
 * User: neil
 * Date: 14/11/2012
 */
public class Channel {
    private final Class clazz;
    private final String id;

    public Channel(Class clazz) {
        this.clazz = clazz;
        this.id = null;
    }

    public Channel(Class clazz, String id) {
        this.clazz = clazz;
        this.id = id;
    }

    public boolean isAssignableFrom(Class<?> cls) {
        return clazz.isAssignableFrom(cls);
    }

    public boolean isInstance(Object object) {
        return clazz.isInstance(object);
    }

    public Class getClazz() {
        return clazz;
    }

    public String getId() {
        return id;
    }

    public boolean isAssignableFrom(Channel other) {
        if (!isAssignableFrom(other.getClazz())) {
            return false;
        }
        String otherId = other.getId();
        if (id == null && otherId == null) {
            return true;
        }
        return otherId != null ? otherId.equals(id) : false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        //sb.append("Channel");
        sb.append("{").append(BusContext.getShortClassName(clazz.getName()));
        if (id != null) {
            sb.append(", id='").append(id).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        if (clazz != null ? !clazz.equals(channel.clazz) : channel.clazz != null) return false;
        if (id != null ? !id.equals(channel.id) : channel.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
