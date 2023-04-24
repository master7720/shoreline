package com.caspian.api.config.setting;

import com.caspian.api.config.Config;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NumberConfig<T extends Number> extends Config<T>
{
    // Value min and max bounds. If the current value exceeds these bounds,
    // then {@link #setValue(Number)} will clamp the value to the bounds.
    private final T min, max;

    // Number display format. Used to determine the format of the number in
    // the ClickGui when displaying the value.
    private final NumberDisplay format;

    public NumberConfig(String name, String desc, T min, T value, T max,
                        NumberDisplay format)
    {
        super(name, desc, value);
        this.min = min;
        this.max = max;
        this.format = format;
    }

    public NumberConfig(String name, String desc, T min, T value, T max)
    {
        this(name, desc, min, value, max, NumberDisplay.DEFAULT);
    }

    public T getMin()
    {
        return min;
    }

    public T getMax()
    {
        return max;
    }

    public int getRoundingScale()
    {
        // equal to number of decimal places in value
        String strValue = String.valueOf(getValue());
        return strValue.substring(strValue.indexOf(".") + 1).length();
    }

    public NumberDisplay getFormat()
    {
        return format;
    }

    @Override
    public void setValue(T val)
    {
        // clamp
        if (val.doubleValue() < min.doubleValue())
        {
            super.setValue(min);
        }

        else if (val.doubleValue() > max.doubleValue())
        {
            super.setValue(min);
        }

        // inbounds
        else
        {
            super.setValue(val);
        }
    }

    @Override
    public JsonObject toJson()
    {
        return new JsonPrimitive(getValue()).getAsJsonObject();
    }

    @Override
    public void fromJson(JsonObject jsonObj)
    {
        // get config as number
        if (getValue() instanceof Integer)
        {
            Integer val = jsonObj.getAsInt();
            setValue((T) val);
        }

        else if (getValue() instanceof Float)
        {
            Float val = jsonObj.getAsFloat();
            setValue((T) val);
        }

        else if (getValue() instanceof Double)
        {
            Double val = jsonObj.getAsDouble();
            setValue((T) val);
        }
    }
}