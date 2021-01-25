package io.github.vmzakharov.ecdataframe.dataframe;

import org.eclipse.collections.impl.factory.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction.*;

public class DataFrameAggregationTest
{
    private static final double TOLERANCE = 0.00001;

    private DataFrame dataFrame;

    @Before
    public void initialiseDataFrame()
    {
        this.dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow("Alice", "Abc",  123L, 10.0, 100.0)
                .addRow("Bob",   "Def",  456L, 12.0, -25.0)
                .addRow("Carol", "Xyz", -789L, 17.0,  42.0);
    }

    @Test
    public void sumAll()
    {
        DataFrame expected = new DataFrame("sum")
                .addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow(-210L, 39.0, 117.0);

        DataFrameUtil.assertEquals(expected, this.dataFrame.sum(Lists.immutable.of("Bar", "Baz", "Qux")));
        DataFrameUtil.assertEquals(expected, this.dataFrame.aggregate(Lists.immutable.of(sum("Bar"), sum("Baz"), sum("Qux"))));
    }

    @Test
    public void minAll()
    {
        DataFrame expected = new DataFrame("sum")
                .addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow(-789L, 10.0, -25.0);

        DataFrameUtil.assertEquals(expected, this.dataFrame.aggregate(Lists.immutable.of(min("Bar"), min("Baz"), min("Qux"))));
    }

    @Test
    public void maxAll()
    {
        DataFrame expected = new DataFrame("sum")
                .addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow(456L, 17.0, 100.0);

        DataFrameUtil.assertEquals(expected, this.dataFrame.aggregate(Lists.immutable.of(max("Bar"), max("Baz"), max("Qux"))));
    }

    @Test
    public void averageAll()
    {
        DataFrame expected = new DataFrame("sum")
                .addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow(-70L, 13.0, 39.0);

        DataFrameUtil.assertEquals(expected, this.dataFrame.aggregate(Lists.immutable.of(avg("Bar"), avg("Baz"), avg("Qux"))));
    }

    @Test
    public void differentAggregationsAll()
    {
        DataFrameUtil.assertEquals(
                new DataFrame("variety")
                    .addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                    .addRow(-210L, 10.0, 100.0),
                this.dataFrame.aggregate(Lists.immutable.of(sum("Bar"), min("Baz"), max("Qux"))));

        DataFrameUtil.assertEquals(
                new DataFrame("variety")
                    .addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                    .addRow(456L, 13.0, 117.0),
                this.dataFrame.aggregate(Lists.immutable.of(max("Bar"), avg("Baz"), sum("Qux"))));

    }

    @Test
    public void sumEmpty()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux");

        DataFrame summed = dataFrame.sum(Lists.immutable.of("Bar", "Baz", "Qux"));

        Assert.assertEquals(0L, summed.getLongColumn("Bar").getLong(0));
        Assert.assertEquals(0.0, summed.getDoubleColumn("Baz").getDouble(0), TOLERANCE);
        Assert.assertEquals(0.0, summed.getDoubleColumn("Qux").getDouble(0), TOLERANCE);
    }

    @Test
    public void aggregationsAllWithCalculatedColumns()
    {
        this.dataFrame.addLongColumn("BarBar", "Bar * 2");
        this.dataFrame.addDoubleColumn("BazBaz", "Baz * 2");

        DataFrame expectedSum = new DataFrame("Sum of FrameOfData")
                .addLongColumn("Bar").addDoubleColumn("Baz").addLongColumn("BarBar").addDoubleColumn("BazBaz")
                .addRow(-210L, 39.0, -420L, 78.0);

        DataFrameUtil.assertEquals(expectedSum, dataFrame.sum(Lists.immutable.of("Bar", "Baz", "BarBar", "BazBaz")));
        DataFrameUtil.assertEquals(expectedSum, dataFrame.aggregate(Lists.immutable.of(sum("Bar"), sum("Baz"), sum("BarBar"), sum("BazBaz"))));

        DataFrame expectedMin = new DataFrame("Min of FrameOfData")
                .addLongColumn("Bar").addDoubleColumn("Baz").addLongColumn("BarBar").addDoubleColumn("BazBaz")
                .addRow(-789L, 10.0, -1578L, 20.0);

        DataFrameUtil.assertEquals(expectedMin, dataFrame.aggregate(Lists.immutable.of(min("Bar"), min("Baz"), min("BarBar"), min("BazBaz"))));

        DataFrame expectedMax = new DataFrame("Max of FrameOfData")
                .addLongColumn("Bar").addDoubleColumn("Baz").addLongColumn("BarBar").addDoubleColumn("BazBaz")
                .addRow(456L, 17.0, 912L, 34.0);

        DataFrameUtil.assertEquals(expectedMax, dataFrame.aggregate(Lists.immutable.of(max("Bar"), max("Baz"), max("BarBar"), max("BazBaz"))));

        DataFrame expectedAvg = new DataFrame("Avg of FrameOfData")
                .addLongColumn("Bar").addDoubleColumn("Baz").addLongColumn("BarBar").addDoubleColumn("BazBaz")
                .addRow(-70L, 13.0, -140L, 26.0);

        DataFrameUtil.assertEquals(expectedAvg, dataFrame.aggregate(Lists.immutable.of(avg("Bar"), avg("Baz"), avg("BarBar"), avg("BazBaz"))));
    }

    @Test
    public void sumEmptyWithCalculatedColumns()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz")
                .addDoubleColumn("BazBaz", "Baz * 2").addLongColumn("BarBar", "Bar * 2");

        DataFrame summed = dataFrame.sum(Lists.immutable.of("Bar", "Baz", "BazBaz", "BarBar"));

        DataFrame expected = new DataFrame("Sum of FrameOfData")
                .addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("BazBaz").addLongColumn("BarBar")
                .addRow(0L, 0.0, 0.0, 0L);

        DataFrameUtil.assertEquals(expected, summed);
    }

    @Test(expected = RuntimeException.class)
    public void sumNonNumericTriggersError()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow("Alice", "Abc",  123L, 10.0, 20.0)
                .addRow("Bob",   "Def",  456L, 12.0, 25.0)
                .addRow("Carol", "Xyz",  789L, 15.0, 40.0);

        DataFrame summed = dataFrame.sum(Lists.immutable.of("Foo", "Bar", "Baz"));

        Assert.fail("Shouldn't get to this line");
    }

    @Test
    public void sumGroupingOneRow()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow("Alice", "Abc",  123L, 10.0, 20.0);

        DataFrame summed = dataFrame.sumBy(Lists.immutable.of("Bar", "Baz", "Qux"), Lists.immutable.of("Name"));

        Assert.assertEquals(1, summed.rowCount());

        Assert.assertEquals("Alice", summed.getString("Name", 0));
        Assert.assertEquals(   123L, summed.getLong("Bar", 0));
        Assert.assertEquals(   10.0, summed.getDouble("Baz", 0), TOLERANCE);
        Assert.assertEquals(   20.0, summed.getDouble("Qux", 0), TOLERANCE);
    }

    @Test
    public void sumGroupingSimple()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux");

        dataFrame.addRow("Alice", "Abc",  123L, 10.0, 22.0);
        dataFrame.addRow("Alice", "Xyz",  456L, 11.0, 20.0);

        DataFrame summed = dataFrame.sumBy(Lists.immutable.of("Bar", "Baz", "Qux"), Lists.immutable.of("Name"));

        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Name").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow("Alice", 579, 21.0, 42.0);

        DataFrameUtil.assertEquals(expected, summed);
    }

    @Test
    public void minGroupingSimple()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux");

        dataFrame.addRow("Alice", "Abc",  123L, 10.0, 22.0);
        dataFrame.addRow("Alice", "Xyz",  456L, 11.0, 20.0);

        DataFrame summed = dataFrame.aggregateBy(Lists.immutable.of(min("Bar"), min("Baz"), min("Qux")), Lists.immutable.of("Name"));

        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Name").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow("Alice", 123L, 10.0, 20.0);

        DataFrameUtil.assertEquals(expected, summed);
    }

    @Test
    public void maxGroupingSimple()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux");

        dataFrame.addRow("Alice", "Abc",  123L, 10.0, 22.0);
        dataFrame.addRow("Alice", "Xyz",  456L, 11.0, 20.0);

        DataFrame summed = dataFrame.aggregateBy(Lists.immutable.of(max("Bar"), max("Baz"), max("Qux")), Lists.immutable.of("Name"));

        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Name").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow("Alice", 456L, 11.0, 22.0);

        DataFrameUtil.assertEquals(expected, summed);
    }

    @Test
    public void mixedGroupingSimple()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux");

        dataFrame.addRow("Alice", "Abc",  123L, 10.0, 22.0);
        dataFrame.addRow("Alice", "Xyz",  456L, 11.0, 20.0);

        DataFrame summed = dataFrame.aggregateBy(Lists.immutable.of(sum("Bar"), min("Baz"), max("Qux")), Lists.immutable.of("Name"));

        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Name").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow("Alice", 579L, 10.0, 22.0);

        DataFrameUtil.assertEquals(expected, summed);
    }

    @Test
    public void sumWithGrouping()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux");

        dataFrame.addRow("Bob",   "Def",  456L, 12.0, 25.0);
        dataFrame.addRow("Alice", "Abc",  123L, 10.0, 20.0);
        dataFrame.addRow("Carol", "Rrr",  789L, 15.0, 40.0);
        dataFrame.addRow("Bob",   "Def",  111L, 12.0, 25.0);
        dataFrame.addRow("Carol", "Qqq",  789L, 15.0, 40.0);
        dataFrame.addRow("Carol", "Zzz",  789L, 15.0, 40.0);

        DataFrame summed = dataFrame.sumBy(Lists.immutable.of("Bar", "Baz", "Qux"), Lists.immutable.of("Name"));

        Assert.assertEquals(3, summed.rowCount());

        DataFrame expected = new DataFrame("Expected")
            .addStringColumn("Name").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow("Bob",	  567, 24,  50)
                .addRow("Alice",  123, 10,  20)
                .addRow("Carol", 2367, 45, 120);

        DataFrameUtil.assertEquals(expected, summed);
    }

    @Test
    public void differentAggregationsWithGrouping()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux");

        dataFrame.addRow("Bob",   "Def",  456L, 12.0, 45.0);
        dataFrame.addRow("Alice", "Abc",  123L, 10.0, 20.0);
        dataFrame.addRow("Carol", "Rrr",  789L, 14.0, 20.0);
        dataFrame.addRow("Bob",   "Def",  111L, 12.0, 25.0);
        dataFrame.addRow("Carol", "Qqq",  789L, 11.0, 30.0);
        dataFrame.addRow("Carol", "Zzz",  789L, 15.0, 40.0);

        DataFrame summed = dataFrame.aggregateBy(Lists.immutable.of(sum("Bar"), min("Baz"), max("Qux")), Lists.immutable.of("Name"));

        Assert.assertEquals(3, summed.rowCount());

        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Name").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow("Bob",	  567, 12.0, 45.0)
                .addRow("Alice",  123, 10.0, 20.0)
                .addRow("Carol", 2367, 11.0, 40.0);

        DataFrameUtil.assertEquals(expected, summed);
    }

    @Test
    public void sumWithGroupingByTwoColumns()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux");

        dataFrame.addRow("Bob",   "Def",  456L, 12.0, 25.0);
        dataFrame.addRow("Bob",   "Abc",  123L, 44.0, 33.0);
        dataFrame.addRow("Alice", "Qqq",  123L, 10.0, 20.0);
        dataFrame.addRow("Carol", "Rrr",  789L, 15.0, 40.0);
        dataFrame.addRow("Bob",   "Def",  111L, 12.0, 25.0);
        dataFrame.addRow("Carol", "Qqq",   10L, 55.0, 22.0);
        dataFrame.addRow("Carol", "Rrr",  789L, 16.0, 41.0);

        DataFrame summed = dataFrame.sumBy(Lists.immutable.of("Bar", "Baz", "Qux"), Lists.immutable.of("Name", "Foo"));

        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux")
                .addRow("Bob",	 "Def",   567L, 24.0,  50.0)
                .addRow("Bob",	 "Abc",   123L, 44.0,  33.0)
                .addRow("Alice", "Qqq",   123L, 10.0,  20.0)
                .addRow("Carol", "Rrr",  1578L, 31.0,  81.0)
                .addRow("Carol", "Qqq",    10L, 55.0,  22.0);

        DataFrameUtil.assertEquals(expected, summed);
    }
        
    @Test
    public void sumOfAndByCalculatedColumns()
    {
        DataFrame dataFrame = new DataFrame("FrameOfData")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("Bar").addDoubleColumn("Baz").addDoubleColumn("Qux");

        dataFrame.addRow("Bob",   "Def",  456L, 12.0, 25.0);
        dataFrame.addRow("Bob",   "Abc",  123L, 44.0, 33.0);
        dataFrame.addRow("Alice", "Qqq",  123L, 10.0, 20.0);
        dataFrame.addRow("Carol", "Rrr",  789L, 15.0, 40.0);
        dataFrame.addRow("Bob",   "Def",  111L, 12.0, 25.0);
        dataFrame.addRow("Carol", "Qqq",   10L, 55.0, 22.0);
        dataFrame.addRow("Carol", "Rrr",  789L, 16.0, 41.0);

        dataFrame.addStringColumn("aFoo", "'a' + Foo");
        dataFrame.addLongColumn("BarBar", "Bar * 2");
        dataFrame.addDoubleColumn("BazBaz", "Baz * 2");

        DataFrame summed = dataFrame.sumBy(Lists.immutable.of("BarBar", "BazBaz"), Lists.immutable.of("Name", "aFoo"));

        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Name").addStringColumn("Foo").addLongColumn("BarBar").addDoubleColumn("BazBaz")
                .addRow("Bob",	 "aDef",  1134L,  48.0)
                .addRow("Bob",	 "aAbc",   246L,  88.0)
                .addRow("Alice", "aQqq",   246L,  20.0)
                .addRow("Carol", "aRrr",  3156L,  62.0)
                .addRow("Carol", "aQqq",    20L, 110.0);

        DataFrameUtil.assertEquals(expected, summed);
    }
}
