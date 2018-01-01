package org.wxd.junit.demo.simple;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SimpleServiceDemoTest.class, SimpleControllerDemoTest.class})
public class SimpleDemoAllTest {
}
