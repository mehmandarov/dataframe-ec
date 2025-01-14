package io.github.vmzakharov.ecdataframe.dataframe;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DataFrameToStringTest
{
    @Test
    public void toCsv()
    {
        DataFrame dataFrame = new DataFrame("Employees")
                .addStringColumn("Name").addLongColumn("EmployeeId").addDateColumn("HireDate").addStringColumn("Dept")
                .addDoubleColumn("Salary").addDateTimeColumn("Event")
                .addRow("Alice", 1234, LocalDate.of(2020, 1, 1), "Accounting", 110000.0, LocalDateTime.of(2020, 10, 11, 1, 2, 3))
                .addRow("Bob", 1233, LocalDate.of(2010, 1, 1), "Bee-bee-boo-boo", 100000.0, LocalDateTime.of(2020, 11, 22, 13, 25, 36))
                .addRow("Carl", 10000, LocalDate.of(2005, 11, 21), "Controllers", 130000.0, null)
                .addRow("Diane", 10001, LocalDate.of(2012, 9, 20), "", 130000.0, LocalDateTime.of(2022, 8, 21, 4, 5, 6))
                .addRow("Ed", 10002, null, "", 0.0, LocalDateTime.of(2022, 10, 11, 21, 32, 53))
                ;

        Assert.assertEquals(
                  "Name,EmployeeId,HireDate,Dept,Salary,Event\n"
                + "\"Alice\",1234,2020-01-01,\"Accounting\",110000.0,2020-10-11T01:02:03\n"
                + "\"Bob\",1233,2010-01-01,\"Bee-bee-boo-boo\",100000.0,2020-11-22T13:25:36\n"
                + "\"Carl\",10000,2005-11-21,\"Controllers\",130000.0,\n"
                + "\"Diane\",10001,2012-09-20,\"\",130000.0,2022-08-21T04:05:06\n"
                + "\"Ed\",10002,,\"\",0.0,2022-10-11T21:32:53\n"
                + "",
                dataFrame.asCsvString());
    }

    @Test
    public void toCsvWithLimit()
    {
        DataFrame dataFrame = new DataFrame("Employees")
                .addStringColumn("Name").addLongColumn("EmployeeId").addDateColumn("HireDate").addStringColumn("Dept").addDoubleColumn("Salary")
                .addRow("Alice", 1234, LocalDate.of(2020, 1, 1), "Accounting", 110000.0)
                .addRow("Bob", 1233, LocalDate.of(2010, 1, 1), "Bee-bee-boo-boo", 100000.0)
                .addRow("Carl", 10000, LocalDate.of(2005, 11, 21), "Controllers", 130000.0)
                .addRow("Diane", 10001, LocalDate.of(2012, 9, 20), "", 130000.0)
                .addRow("Ed", 10002, null, "", 0.0)
                ;

        Assert.assertEquals(
                  "Name,EmployeeId,HireDate,Dept,Salary\n"
                + "\"Alice\",1234,2020-01-01,\"Accounting\",110000.0\n"
                + "\"Bob\",1233,2010-01-01,\"Bee-bee-boo-boo\",100000.0\n"
                + "\"Carl\",10000,2005-11-21,\"Controllers\",130000.0\n"
                + "",
                dataFrame.asCsvString(3));
    }

    @Test
    public void toCsvWithNulls()
    {
        DataFrame dataFrame = new DataFrame("Employees")
                .addStringColumn("Name").addLongColumn("EmployeeId").addDateColumn("HireDate").addStringColumn("Dept").addDoubleColumn("Salary");

        dataFrame
                .addRow("Alice", 1234, LocalDate.of(2020, 1, 1), "Accounting", 110000.0)
                .addRow("Bob", null, LocalDate.of(2010, 1, 1), "Bee-bee-boo-boo", 100000.0)
                .addRow("Carl", 10000, null, "Controllers", 130000.0)
                .addRow("Diane", 10001, LocalDate.of(2012, 9, 20), "", null)
                .addRow("Ed", 10002, null, "", 0.0)
                ;

        Assert.assertEquals(
                "Name,EmployeeId,HireDate,Dept,Salary\n"
                + "\"Alice\",1234,2020-01-01,\"Accounting\",110000.0\n"
                + "\"Bob\",,2010-01-01,\"Bee-bee-boo-boo\",100000.0\n"
                + "\"Carl\",10000,,\"Controllers\",130000.0\n"
                + "\"Diane\",10001,2012-09-20,\"\",\n"
                + "\"Ed\",10002,,\"\",0.0\n"
                + "",
                dataFrame.asCsvString());
    }
}
