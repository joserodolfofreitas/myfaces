/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.util;

import org.junit.Test;
import org.testng.Assert;

import java.util.HashSet;
import java.util.Set;

public class XorShiftRandomTest
{

    @Test
    public void testXorShiftRandom()
    {
        XorShiftRandom random = new XorShiftRandom(System.nanoTime());
        Set<Long> randomVals = new HashSet<Long>(10);

        int randomCount = 100;

        for (int i = 0; i < randomCount; i ++)
        {
            randomVals.add(random.random());
        }

        // if the random generator is good then we had no collisions.
        Assert.assertEquals(randomVals.size(), 100);
    }


    @Test
    public void testThreadsafeXorShiftRandom()
    {
        XorShiftRandom random = new ThreadsafeXorShiftRandom();
        Set<Long> randomVals = new HashSet<Long>(10);

        int randomCount = 100;

        for (int i = 0; i < randomCount; i ++)
        {
            randomVals.add(random.random());
        }

        // if the random generator is good then we had no collisions.
        Assert.assertEquals(randomVals.size(), 100);
    }
}
