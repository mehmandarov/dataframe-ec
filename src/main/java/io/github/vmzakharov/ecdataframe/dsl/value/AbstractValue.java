package io.github.vmzakharov.ecdataframe.dsl.value;

import static io.github.vmzakharov.ecdataframe.util.ExceptionFactory.exception;

abstract public class AbstractValue
implements Value
{

    public void throwExceptionIfNull(Object newValue)
    {
        if (newValue == null)
        {
            exception("${type} value cannot contain null, a void value should be used instead").with("type", this.getType()).fire();
        }
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName() + ">" + this.asStringLiteral();
    }

    public void checkSameTypeForComparison(Value other)
    {
        if (null == other)
        {
            throw exception("Cannot compare a ${className} to null")
                    .with("className",  this.getClass().getName()).getUnsupported();
        }

        if (!other.isVoid() && (this.getClass() != other.getClass()))
        {
            throw exception("Cannot compare a ${className} to a ${otherClassName}")
                    .with("thisClassName",  this.getClass().getName())
                    .with("otherClassName", other.getClass().getName())
                    .getUnsupported();
        }
    }
}
