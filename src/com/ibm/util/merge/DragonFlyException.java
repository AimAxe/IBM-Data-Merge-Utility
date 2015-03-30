/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.util.merge;

/**
 * Simple custom Exception Class
 *
 * @author Mike Storey
 */
public class DragonFlyException extends Exception {
	private static final long serialVersionUID = 1L;
	private String errorCode="-Code Not Set-";
	
	/**
	 * Constructor
	 * 
	 * @param message The exception message
	 * @param errorCode The error code
	 */
	public DragonFlyException(String message, String errorCode){
        super(message);
        this.errorCode=errorCode;
    }

	/**
	 * Get the Error Code
	 * @return String the error code
	 */
    public String getErrorCode(){
        return this.errorCode;
    }
}