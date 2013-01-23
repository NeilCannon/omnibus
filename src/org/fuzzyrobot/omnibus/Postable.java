package org.fuzzyrobot.omnibus;

/**
 * User: neil
 * Date: 07/11/2012
 */
public interface Postable {
    void publish(Channel channel, Object value);
//    void provide(Class clazz, ParameterisedProvider provider);
}
