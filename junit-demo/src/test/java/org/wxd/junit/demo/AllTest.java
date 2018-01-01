package org.wxd.junit.demo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.wxd.junit.demo.db.DBAllTest;
import org.wxd.junit.demo.other.OtherInterfaceAllTest;
import org.wxd.junit.demo.simple.SimpleDemoAllTest;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {SimpleDemoAllTest.class, OtherInterfaceAllTest.class, DBAllTest.class})
public class AllTest {
}
