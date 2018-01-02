package org.wxd.junit.demo.dbunit.infrastructure;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import java.util.Arrays;

public enum DBType {
    H2("H2"), MySQL("MySQL"), Oracle("Oracle"), SQLServer("Microsoft SQL Server"), PostgreSQL("PostgreSQL");

    private final String productName;

    DBType(final String productName) {
        this.productName = productName;
    }

    public static DBType valueFrom(final String databaseProductName) {
        Optional<DBType> databaseTypeOptional = Iterators.tryFind(Arrays.asList(DBType.values()).iterator(), (Predicate<DBType>) input -> input.productName.equals(databaseProductName));
        if (databaseTypeOptional.isPresent()) {
            return databaseTypeOptional.get();
        }
        throw new UnsupportedOperationException(String.format("Can not support database type [%s].", databaseProductName));
    }
}
