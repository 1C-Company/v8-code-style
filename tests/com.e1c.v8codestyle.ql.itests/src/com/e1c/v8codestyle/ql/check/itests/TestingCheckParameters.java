/**
 *
 */
package com.e1c.v8codestyle.ql.check.itests;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.WrongParameterException;

/**
 * @author Dmitriy Marmyshev
 *
 */
public class TestingCheckParameters
    implements ICheckParameters
{
    private final Map<String, Object> data;

    public TestingCheckParameters(Map<String, Object> data)
    {
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public int getInt(String name)
    {
        return get(name, Integer.class).intValue();
    }

    @Override
    public long getLong(String name)
    {
        return get(name, Long.class).longValue();
    }

    @Override
    public boolean getBoolean(String name)
    {
        return get(name, Boolean.class).booleanValue();
    }

    @Override
    public double getDouble(String name)
    {
        return get(name, Double.class).doubleValue();
    }

    @Override
    public String getString(String name)
    {
        return get(name, String.class);
    }

    @Override
    public Set<String> keySet()
    {
        return this.data.keySet();
    }

    @Override
    public String toString()
    {
        return this.data.toString();
    }

    private <T> T get(String name, Class<T> type) throws WrongParameterException
    {
        Object value = this.data.get(name);
        if (value == null)
            throw new WrongParameterException(name, "Parameter not found");
        if (type.isInstance(value))
            return type.cast(value);
        if (value instanceof WrongParameterException)
            throw new WrongParameterException(name, ((WrongParameterException)value).getMessage());
        throw new WrongParameterException(name,
            MessageFormat.format("Parameter has a different type {0}", value.getClass().getSimpleName()));
    }

}
