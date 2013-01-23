package org.fuzzyrobot.omnibus;

/**
 * User: neil
 * Date: 07/11/2012
 */
public interface Subscriber2<T1, T2> {
    void receive(T1 value1, T2 value2);
}
