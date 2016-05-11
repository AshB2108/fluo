/*
 * Copyright 2014 Fluo authors (see AUTHORS)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.fluo.integration;

import java.util.concurrent.TimeUnit;

import io.fluo.api.client.FluoAdmin;
import io.fluo.api.client.FluoAdmin.InitOpts;
import io.fluo.api.client.FluoFactory;
import io.fluo.api.config.FluoConfiguration;
import io.fluo.api.mini.MiniFluo;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;

/**
 * Base Integration Test class implemented using MiniFluo
 */
public class ITBaseMini extends ITBase {

  protected static MiniFluo miniFluo;

  protected void setAppConfig(Configuration config) {}

  protected void setConfig(FluoConfiguration config) {}

  @Before
  public void setUpFluo() throws Exception {

    config = new FluoConfiguration();
    config.setApplicationName("mini-test" + testCounter.getAndIncrement());
    config.setAccumuloInstance(miniAccumulo.getInstanceName());
    config.setAccumuloUser(USER);
    config.setAccumuloPassword(PASSWORD);
    config.setAccumuloZookeepers(miniAccumulo.getZooKeepers());
    config.setInstanceZookeepers(miniAccumulo.getZooKeepers() + "/fluo");
    config.setAccumuloTable(getNextTableName());
    config.setWorkerThreads(5);
    config.addObservers(getObservers());
    config.setMiniStartAccumulo(false);

    setConfig(config);
    setAppConfig(config.getAppConfiguration());

    config.setTransactionRollbackTime(1, TimeUnit.SECONDS);

    try (FluoAdmin admin = FluoFactory.newAdmin(config)) {
      InitOpts opts = new InitOpts().setClearZookeeper(true).setClearTable(true);
      admin.initialize(opts);
    }

    config.getAppConfiguration().clear();

    client = FluoFactory.newClient(config);
    miniFluo = FluoFactory.newMiniFluo(config);
  }

  @After
  public void tearDownFluo() throws Exception {
    miniFluo.close();
    client.close();
  }
}