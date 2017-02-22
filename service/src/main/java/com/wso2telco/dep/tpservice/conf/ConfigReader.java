/*******************************************************************************
 * Copyright (c) 2015-2017, WSO2.Telco Inc. (http://www.wso2telco.com)
 *
 * All Rights Reserved. WSO2.Telco Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wso2telco.dep.tpservice.conf;

import com.wso2telco.dep.tpservice.model.ConfigDTO;
import io.dropwizard.setup.Environment;

import java.io.Serializable;

public class ConfigReader implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8945346314273239957L;
	private ConfigDTO configDTO;
	private static ConfigReader reader;
	private Environment env;


	private ConfigReader(final ConfigDTO configDTO,final Environment env) {
		this.configDTO = configDTO;
		this.env= env;

	}
	public static void init(final ConfigDTO configDTO,final Environment env){
		reader = new ConfigReader(configDTO,env);
	}


	public static synchronized ConfigReader getInstance() {
		return reader;
	}
	
	public ConfigDTO getConfigDTO(){
		return configDTO;
	}
	
	public Environment getEnvironment(){
		return env;
	}
}
