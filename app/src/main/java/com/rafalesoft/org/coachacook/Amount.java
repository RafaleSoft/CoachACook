package com.rafalesoft.org.coachacook;

import androidx.annotation.NonNull;

class Amount implements Comparable
{
    private Double	_quantity = 0.0;
    private Unit 	_unit = Unit.COUNT;

    /**
     *  Implements Comparable
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(@NonNull Object o)
    {
        if (o instanceof Amount)
        {
            Amount a = (Amount)o;
            if (!a.get_unit().canConvert(_unit))
                throw new ClassCastException();

            double v1 = _quantity * _unit.getSIFactor();
            double v2 = a.get_quantity() * a.get_unit().getSIFactor();
            return Double.compare(v1, v2);
        }
        else
            throw new ClassCastException();
    }

    /**
     * Implements Object
     * @param   obj   the reference object with which to compare.
     * @return  true if this object is the same as the obj
     *          argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Amount)
        {
            Amount a = (Amount)obj;
            if (!a.get_unit().canConvert(_unit))
                throw new ClassCastException();

            double v1 = _quantity * _unit.getSIFactor();
            double v2 = a.get_quantity() * a.get_unit().getSIFactor();

            return (0 == Double.compare(v1,v2));
        }
        else
            throw new ClassCastException();
    }

    /**
     * Quantity getter.
     * @return ingredient quantity.
     */
    public Double get_quantity()
    {
        return _quantity;
    }

    /**
     * Quantity setter.
     * @param quantity : the amount of ingredient.
     */
    void set_quantity(Double quantity)
    {
        _quantity = quantity;
    }

    /**
     * Unit getter.
     * @return the unit of ingredient quantity.
     */
    public Unit get_unit()
    {
        return _unit;
    }

    /**
     * Unit setter.
     * @param unit : the new unit.
     */
    void set_unit(Unit unit)
    {
        _unit = unit;
    }
}
