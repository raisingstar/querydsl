/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.query._oracle;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.mysema.query.Connections;
import com.mysema.query.DeleteBaseTest;
import com.mysema.query.Target;
import com.mysema.query.sql.OracleTemplates;
import com.mysema.testutil.FilteringTestRunner;
import com.mysema.testutil.Label;
import com.mysema.testutil.ResourceCheck;

@RunWith(FilteringTestRunner.class)
@ResourceCheck("/oracle.run")
@Label(Target.ORACLE)
public class DeleteOracleTest extends DeleteBaseTest{
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        Connections.initOracle();
    }

    @Before
    public void setUp() throws SQLException {        
        dialect = new OracleTemplates().newLineToSingleSpace();
        super.setUp();
    }

}
