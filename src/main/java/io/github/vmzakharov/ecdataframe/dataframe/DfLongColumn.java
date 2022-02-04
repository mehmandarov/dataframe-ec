package io.github.vmzakharov.ecdataframe.dataframe;

import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;

abstract public class DfLongColumn
extends DfColumnAbstract
{
    public DfLongColumn(DataFrame newDataFrame, String newName)
    {
        super(newDataFrame, newName);
    }

    abstract public long getLong(int rowIndex);

    @Override
    public String getValueAsString(int rowIndex)
    {
        return Long.toString(this.getLong(rowIndex));
    }

    public abstract ImmutableLongList toLongList();

    public ValueType getType()
    {
        return ValueType.LONG;
    }

    @Override
    public void addRowToColumn(int rowIndex, DfColumn target)
    {
        if (this.isNull(rowIndex))
        {
            target.addEmptyValue();
        }
        else
        {
            ((DfLongColumnStored) target).addLong(this.getLong(rowIndex), false);
        }
    }

    @Override
    public DfColumn mergeWithInto(DfColumn other, DataFrame target)
    {
        DfLongColumn mergedCol = (DfLongColumn) this.validateAndCreateTargetColumn(other, target);

        mergedCol.addAllItemsFrom(this);
        mergedCol.addAllItemsFrom((DfLongColumn) other);

        return mergedCol;
    }

    protected abstract void addAllItemsFrom(DfLongColumn items);

    @Override
    public DfCellComparator columnComparator(DfColumn otherColumn)
    {
        DfLongColumn otherLongColumn = (DfLongColumn) otherColumn;

        return (thisRowIndex, otherRowIndex) -> {
            int thisMappedIndex = this.dataFrameRowIndex(thisRowIndex);
            int otherMappedIndex = otherLongColumn.dataFrameRowIndex(otherRowIndex);

            return new ComparisonResult.LongComparisonResult(
                () -> this.getLong(thisMappedIndex),
                () -> otherLongColumn.getLong(otherMappedIndex),
                this.isNull(thisMappedIndex),
                otherLongColumn.isNull(otherMappedIndex)
            );
        };
    }
}
