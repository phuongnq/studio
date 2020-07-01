/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.studio.api.v1.service.workflow;

/**
 * workflow job property (name and value pair) 
 * 
 * @author hyanghee
 *
 */
public class WorkflowJobProperty {

	String jobId;
	String name;
	String value;
	
	/**
	 * default constructor
	 */
	public WorkflowJobProperty() {}
	
	/**
	 * constructor 
	 * @param name
	 * 			property name
	 * @param value
	 * 			property value
	 */
	public WorkflowJobProperty(String jobId, String name, String value) {
		this.jobId = jobId;
		this.name = name;
		this.value = value;
	}
	

	/** job id property getter */
	public String getJobId() {
		return jobId;
	}

	/** job id property setter */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		return name + ":" + value;
	}
	
}
